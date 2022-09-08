--liquebase formatted sql

--changeset zadorozhniyya:MC-13-change-requests-list logicalFilePath:/

ALTER TABLE IF EXISTS change_requests
    ADD COLUMN IF NOT EXISTS created_at timestamp DEFAULT current_timestamp;