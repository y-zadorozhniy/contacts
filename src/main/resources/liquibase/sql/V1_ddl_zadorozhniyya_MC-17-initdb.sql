--liquebase formatted sql

--changeset zadorozhniyya:MC-17-initdb logicalFilePath:/

CREATE TABLE IF NOT EXISTS records
(
    record_id    bigserial PRIMARY KEY,
    full_name    varchar(255) NOT NULL,
    organization varchar(64),
    occupation   varchar(64),
    country      varchar(64),
    city         varchar(64)
);

CREATE TABLE IF NOT EXISTS contacts
(
    contact_id bigserial    NOT NULL PRIMARY KEY,
    value      varchar(255) NOT NULL,
    comment    varchar(255),
    type       varchar(16),
    record_id  bigint,
    FOREIGN KEY (record_id) REFERENCES records (record_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tags
(
    tag_id bigserial    NOT NULL PRIMARY KEY,
    name   varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS record_tags
(
    record_id bigint NOT NULL,
    tag_id    bigint NOT NULL,
    FOREIGN KEY (record_id) REFERENCES records (record_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags (tag_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  bigserial    NOT NULL PRIMARY KEY,
    username varchar(64)  NOT NULL,
    email    varchar(128) NOT NULL,
    password varchar(64)  NOT NULL,
    role     varchar(32)
);

CREATE TABLE IF NOT EXISTS change_requests
(
    change_id      bigserial   NOT NULL PRIMARY KEY,
    user_id        bigint      NOT NULL,
    record_id      bigint,
    previous_value text,
    new_value      text,
    type           varchar(16) NOT NULL,
    status         varchar(16) NOT NULL,
    FOREIGN KEY (record_id) REFERENCES records (record_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);