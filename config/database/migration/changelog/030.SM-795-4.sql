--liquibase formatted sql

--changeset preobrazhenskiimy:SM-795-4-channels-alter-1
alter table public.channels add column ord smallint;

--changeset preobrazhenskiimy:SM-795-4-channels-update-1
update public.channels set ord = 1 where id = 'a33ef2b8-5e3b-4f2f-ab96-0cb1dd9a943d';

--changeset preobrazhenskiimy:SM-795-4-channels-update-2
update public.channels set ord = 2 where id = 'ac082a77-c95a-409c-a87d-80e5a7b5af06';

--changeset preobrazhenskiimy:SM-795-4-channels-update-3
update public.channels set ord = 3 where id = '6c340c6e-d80a-41bb-b865-c9f264ab23f5';

--changeset preobrazhenskiimy:SM-795-4-channels-update-4
update public.channels set ord = 4 where id = 'd6f37608-c3b5-db88-5cfa-0255ef1605b8';

--changeset preobrazhenskiimy:SM-795-4-categories-alter-1
alter table public.categories add column ord smallint;

--changeset preobrazhenskiimy:SM-795-4-categories-update-1
update public.categories set ord = 1 where id = '997e6f56-74e2-4d3a-b5c9-0044be242dd9';

--changeset preobrazhenskiimy:SM-795-4-categories-update-2
update public.categories set ord = 2 where id = '09c5f95f-490b-4218-9f87-ba500769171d';

--changeset preobrazhenskiimy:SM-795-4-categories-update-3
update public.categories set ord = 3 where id = 'f805e1d1-0def-48bf-9d5b-34474c1733cb';
