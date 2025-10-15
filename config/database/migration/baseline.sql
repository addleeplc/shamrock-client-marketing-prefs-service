create table channels(
  id uuid primary key,
  create_ts timestamp,
  created_by varchar(50),
  code varchar(64),
  name varchar(256)
);

create unique index channels_code_uk on channels(code);

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

create unique index categories_code_uk on categories(code);

create index categories_parent_idx on categories(parent_category_id);

create table categories_channels(
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

create unique index categories_channels_pair_uk on categories_channels(channel_id, category_id);

create index categories_channels_channel_idx on categories_channels(channel_id);

create index categories_channels_category_idx on categories_channels(category_id);

create table client_prefs(
  client_id uuid primary key,
  client_uid varchar(32),
  client_email varchar(254),
  prefs jsonb not null default '{}'::jsonb
);

create unique index client_prefs_client_uid_uk on client_prefs(client_uid);

create index client_prefs_email_idx on client_prefs(lower(client_email));
