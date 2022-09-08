--liquebase formatted sql

--changeset zadorozhniyya:MC-34-comment-field logicalFilePath:/

ALTER TABLE IF EXISTS records
    ADD COLUMN IF NOT EXISTS comment text;