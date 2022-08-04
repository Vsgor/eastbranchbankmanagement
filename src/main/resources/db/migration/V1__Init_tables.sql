CREATE TABLE accounts.client
(
    id       BIGSERIAL    NOT NULL,
    email    VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(255) NOT NULL,
    active   BOOLEAN      NOT NULL,
    CONSTRAINT pk_client PRIMARY KEY (id)
);

ALTER TABLE accounts.client
    ADD CONSTRAINT uc_client_email UNIQUE (email);

ALTER TABLE accounts.client
    ADD CONSTRAINT uc_client_username UNIQUE (username);

CREATE TABLE accounts.slot
(
    id        BIGSERIAL NOT NULL,
    state     BIGINT    NOT NULL,
    client_id BIGINT    NOT NULL,
    active    BOOLEAN   NOT NULL,
    CONSTRAINT pk_slot PRIMARY KEY (id)
);

ALTER TABLE accounts.slot
    ADD CONSTRAINT FK_SLOT_ON_CLIENT FOREIGN KEY (client_id) REFERENCES accounts.client (id);