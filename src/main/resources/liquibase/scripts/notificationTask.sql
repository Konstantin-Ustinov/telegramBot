--liquibase formatted sql

--changeSet konstantin: 1
CREATE TABLE notification_task (
                       id int primary key,
                       chat_id bigint,
                       message text,
                       notification_date timestamp
)