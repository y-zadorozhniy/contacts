--liquebase formatted sql

--changeset zadorozhniyya:MC-35-tags-unique logicalFilePath:/

CREATE UNIQUE INDEX IF NOT EXISTS tags_name_uindex
    ON tags (name);
