--liquibase formatted sql

--changeset preobrazhenskiimy:SM-795-channels-create-1
create table channels(
  id uuid primary key,
  create_ts timestamp,
  created_by varchar(50),
  code varchar(64),
  name varchar(256)
);

--changeset preobrazhenskiimy:SM-795-channels_code_uk-create-1
create unique index channels_code_uk on channels(code);

--changeset preobrazhenskiimy:SM-795-categories-create-1
create table categories(
  id uuid primary key,
  create_ts timestamp,
  created_by varchar(50),
  code varchar(64),
  name varchar(256),
  description text,
  parent_category_id uuid,
  foreign key(parent_category_id) references categories(id) on update cascade on delete set null
);

--changeset preobrazhenskiimy:SM-795-categories_code_uk-create-1
create unique index categories_code_uk on categories(code);

--changeset preobrazhenskiimy:SM-795-categories_parent_idx-create-1
create index categories_parent_idx on categories(parent_category_id);

--changeset preobrazhenskiimy:SM-795-category_channels-create-1
create table category_channels(
  id uuid primary key,
  create_ts timestamp,
  created_by varchar(50),
  channel_id uuid not null,
  category_id uuid not null,
  opt_in boolean not null default false,
  editable boolean not null default true,
  foreign key(channel_id) references channels(id) on update cascade on delete cascade,
  foreign key(category_id) references categories(id) on update cascade on delete cascade
);

--changeset preobrazhenskiimy:SM-795-category_channels_pair_uk-create-1
create unique index category_channels_pair_uk on category_channels(channel_id, category_id);

--changeset preobrazhenskiimy:SM-795-category_channels_channel_idx-create-1
create index category_channels_channel_idx on category_channels(channel_id);

--changeset preobrazhenskiimy:SM-795-category_channels_category_idx-create-1
create index category_channels_category_idx on category_channels(category_id);

--changeset preobrazhenskiimy:SM-795-client_prefs-create-1
create table client_prefs(
  client_id uuid,
  client_uid varchar(128),
  client_email varchar(254),
  prefs jsonb not null default '{}'::jsonb
);

--changeset preobrazhenskiimy:SM-795-client_prefs_client_id_uk-create-1
create unique index client_prefs_client_id_uid_uk on client_prefs(client_id, client_uid);

--changeset preobrazhenskiimy:SM-795-client_prefs_email_idx-create-1
create index client_prefs_email_idx on client_prefs(lower(client_email));

--changeset preobrazhenskiimy:SM-795-uuid_generate_v4-create-1
create function uuid_generate_v4() RETURNS uuid
    language plpgsql
    as $$
declare
	res uuid;
begin
	select md5(random()::text || clock_timestamp()::text)::uuid into res;
	return res;
end;
$$;

create or replace function client_prefs_id_check()
returns trigger as $$
begin
    if new.client_id is null and new.client_uid is null then
        raise exception 'Any of columns: client_id or client_uid should be defined / not null';
    end if;

    return new;
end;
$$ language plpgsql;

create trigger trg_client_prefs_id_notnull
before insert or update on client_prefs
for each row
execute function client_prefs_id_check();

--changeset preobrazhenskiimy:SM-795-channels-fill-1
insert into public.channels(id, create_ts, created_by, code, name) values
    ('ac082a77-c95a-409c-a87d-80e5a7b5af06', now(), 'service', 'email', 'Email'),
    ('a33ef2b8-5e3b-4f2f-ab96-0cb1dd9a943d', now(), 'service', 'push', 'Push Notifications'),
    ('6c340c6e-d80a-41bb-b865-c9f264ab23f5', now(), 'service', 'sms', 'SMS'),
    ('d6f37608-c3b5-db88-5cfa-0255ef1605b8', now(), 'service', 'whatsapp', 'Whatsapp');

--changeset preobrazhenskiimy:SM-795-categories-fill-1
insert into public.categories(id, create_ts, created_by, code, name, description, parent_category_id) values
    ('997e6f56-74e2-4d3a-b5c9-0044be242dd9', now(), 'service', 'rewards', 'Rewards', 'Exclusive rewards and limited-time offers', NULL),
    ('09c5f95f-490b-4218-9f87-ba500769171d', now(), 'service', 'news', 'News and updates', 'Discover new features, trends and more', NULL),
    ('f805e1d1-0def-48bf-9d5b-34474c1733cb', now(), 'service', 'suggestions', 'Personalised recommendations', 'Journey suggestions tailored to your preferences', NULL);

--changeset preobrazhenskiimy:SM-795-category_channels-fill-1
insert into public.category_channels(id, create_ts, created_by, channel_id, category_id, opt_in, editable) values
    ('39d3a8ed-1bb6-1eee-eaac-d735cc60bf20', now(), 'service', 'ac082a77-c95a-409c-a87d-80e5a7b5af06', '997e6f56-74e2-4d3a-b5c9-0044be242dd9', true, true),
    ('53bc4579-9206-b733-149c-612115cf42f4', now(), 'service', 'a33ef2b8-5e3b-4f2f-ab96-0cb1dd9a943d', '997e6f56-74e2-4d3a-b5c9-0044be242dd9', true, true),
    ('965942d1-65e8-20bb-e9fe-7ec521e0d400', now(), 'service', '6c340c6e-d80a-41bb-b865-c9f264ab23f5', '997e6f56-74e2-4d3a-b5c9-0044be242dd9', true, true),
    ('eb1f4b7f-6af0-40ef-943d-fb23e61ba81c', now(), 'service', 'd6f37608-c3b5-db88-5cfa-0255ef1605b8', '997e6f56-74e2-4d3a-b5c9-0044be242dd9', true, true),
    ('eb3df2c1-ee13-e0e5-f8b5-d4479d929df4', now(), 'service', 'ac082a77-c95a-409c-a87d-80e5a7b5af06', '09c5f95f-490b-4218-9f87-ba500769171d', true, true),
    ('553baf6f-c015-4120-749b-1487d6deb6de', now(), 'service', 'ac082a77-c95a-409c-a87d-80e5a7b5af06', 'f805e1d1-0def-48bf-9d5b-34474c1733cb', false, true),
    ('e6c8e4db-05bf-342b-5a2c-c249cf55e7f9', now(), 'service', 'a33ef2b8-5e3b-4f2f-ab96-0cb1dd9a943d', 'f805e1d1-0def-48bf-9d5b-34474c1733cb', false, true),
    ('2eff95b9-de35-df5b-c556-13ac04e7028a', now(), 'service', '6c340c6e-d80a-41bb-b865-c9f264ab23f5', 'f805e1d1-0def-48bf-9d5b-34474c1733cb', false, true),
    ('13e8da4b-7bab-6500-cfa7-2b8222e2d3d2', now(), 'service', 'd6f37608-c3b5-db88-5cfa-0255ef1605b8', 'f805e1d1-0def-48bf-9d5b-34474c1733cb', false, true);
