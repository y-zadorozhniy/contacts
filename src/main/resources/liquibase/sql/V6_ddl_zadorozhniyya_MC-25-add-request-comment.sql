--liquebase formatted sql

--changeset zadorozhniyya:MC-25-add-request-comment logicalFilePath:/

ALTER TABLE IF EXISTS change_requests
    ADD COLUMN IF NOT EXISTS comment text;