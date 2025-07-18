CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS item_requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description  TEXT      NOT NULL,
    requestor_id BIGINT REFERENCES users (id),
    created      TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    available   BOOLEAN      NOT NULL,
    owner_id    BIGINT REFERENCES users (id),
    request_id  BIGINT REFERENCES item_requests (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    BIGINT                      NOT NULL REFERENCES items (id) ON DELETE CASCADE,
    booker_id  BIGINT                      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status     VARCHAR(20)                 NOT NULL
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text      TEXT      NOT NULL,
    item_id   BIGINT    NOT NULL REFERENCES items (id) ON DELETE CASCADE,
    author_id BIGINT    NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created   TIMESTAMP NOT NULL
);