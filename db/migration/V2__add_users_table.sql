CREATE TABLE IF NOT EXISTS users
(
    id            SERIAL PRIMARY KEY,
    username      VARCHAR(50) UNIQUE  NOT NULL,
    email         VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    role          VARCHAR(20)         NOT NULL DEFAULT 'USER',
    created_at    TIMESTAMP                    DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(50)                  DEFAULT 'system',
    updated_at    TIMESTAMP                    DEFAULT CURRENT_TIMESTAMP,
    updated_by    VARCHAR(50)                  DEFAULT 'system',
    deleted_at    TIMESTAMP,
    deleted_by    VARCHAR(50),
    CONSTRAINT role_check CHECK (role IN ('ADMIN', 'USER', 'GUEST'))
);

INSERT INTO users (username, email, password_hash, role)
VALUES ('admin', 'email', '$2b$12$KIXQJZ5E6j1y5Z9Z8e4OeO7y5jF6j1y5Z9e4OeO7y5jF6j1y5Z9e4O', 'ADMIN')
ON CONFLICT (username) DO NOTHING,
('user', 'email', '$2b$12$KIXQJZ5E6j1y5Z9Z8e4OeO7y5jF6j1y5Z9e4OeO7y5jF6j1y5Z9e4O', 'USER')
    ON CONFLICT (username) DO NOTHING,
('guest', 'email', '$2b$12$KIXQJZ5E6j1y5Z9Z8e4OeO7y5jF6j1y5Z9e4OeO7y5jF6j1y5Z9e4O', 'GUEST')
    ON CONFLICT (username) DO NOTHING;
