--liquibase formatted sql

--changeset preobrazhenskiimy:SM-836-client_prefs-alter-1
alter table client_prefs
    add column create_ts timestamp,
    add column created_by varchar(50),
    add column update_ts timestamp,
    add column updated_by varchar(50),
    add column delete_ts timestamp,
    add column deleted_by varchar(50)
;
