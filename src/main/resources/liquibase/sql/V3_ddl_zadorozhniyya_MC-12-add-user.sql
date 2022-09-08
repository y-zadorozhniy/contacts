--liquebase formatted sql

--changeset zadorozhniyya:MC-12-add-user logicalFilePath:/

INSERT INTO users (username, email, password, role)
    VALUES ('admin', '---', '$2a$10$bDV5Kum2AlanuTT/nbn2MuSu9rYpeNOUMPhGJaIBiqx95OhogYDgm', 'ADMIN')
        ON CONFLICT DO NOTHING;

CREATE UNIQUE INDEX IF NOT EXISTS users_username_uindex
    ON users (username);