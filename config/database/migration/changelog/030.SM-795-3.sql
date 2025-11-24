--liquibase formatted sql

--changeset preobrazhenskiimy:SM-795-2-channels-clear-1
delete from public.channels;

--changeset preobrazhenskiimy:SM-795-2-categories-clear-1
delete from public.categories;

--changeset preobrazhenskiimy:SM-795-2-channels-fill-1
insert into public.channels(id, create_ts, created_by, code, name) values
    ('a33ef2b8-5e3b-4f2f-ab96-0cb1dd9a943d', '2025-01-01 00:01:00', 'service', 'push', 'Push Notifications'),
    ('ac082a77-c95a-409c-a87d-80e5a7b5af06', '2025-01-01 00:02:00', 'service', 'email', 'Email'),
    ('6c340c6e-d80a-41bb-b865-c9f264ab23f5', '2025-01-01 00:03:00', 'service', 'sms', 'SMS'),
    ('d6f37608-c3b5-db88-5cfa-0255ef1605b8', '2025-01-01 00:04:00', 'service', 'whatsapp', 'Whatsapp');

--changeset preobrazhenskiimy:SM-795-2-categories-fill-1
insert into public.categories(id, create_ts, created_by, code, name, description, parent_category_id) values
    ('997e6f56-74e2-4d3a-b5c9-0044be242dd9', '2025-01-01 00:01:00', 'service', 'rewards', 'Rewards', 'Exclusive rewards and limited-time offers', NULL),
    ('09c5f95f-490b-4218-9f87-ba500769171d', '2025-01-01 00:02:00', 'service', 'news', 'News and updates', 'Discover new features, trends and more', NULL),
    ('f805e1d1-0def-48bf-9d5b-34474c1733cb', '2025-01-01 00:03:00', 'service', 'suggestions', 'Personalised recommendations', 'Journey suggestions tailored to your preferences', NULL);

--changeset preobrazhenskiimy:SM-795-2-category_channels-fill-1
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
