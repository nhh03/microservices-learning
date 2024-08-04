--liquibase formatted sql

-- Changeset changelog-0001:1
alter table media modify column data longblob;