--liquibase formatted sql

--changeset preobrazhenskiimy:SM-795-channels-alter-1
alter table channels
    add column update_ts timestamp,
    add column updated_by varchar(50),
    add column delete_ts timestamp,
    add column deleted_by varchar(50)
;

--changeset preobrazhenskiimy:SM-795-categories-alter-1
alter table categories
    add column update_ts timestamp,
    add column updated_by varchar(50),
    add column delete_ts timestamp,
    add column deleted_by varchar(50)
;
