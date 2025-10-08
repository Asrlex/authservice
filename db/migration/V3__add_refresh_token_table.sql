CREATE TABLE auth_refresh_tokens
(
    id              UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    jti             UUID        NOT NULL UNIQUE, -- token id (uuid)
    user_id         VARCHAR      NOT NULL,        -- your users table PK type
    client_id       TEXT,                        -- optional (e.g., web, mobile, device id)
    token_hash      TEXT        NOT NULL,        -- sha256(token)
    created_at      timestamptz NOT NULL DEFAULT now(),
    last_used_at    timestamptz,
    expires_at      timestamptz NOT NULL,
    revoked         boolean     NOT NULL DEFAULT false,
    replaced_by_jti UUID,                        -- jti of the next rotated token
    ip_address      TEXT,
    user_agent      TEXT,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_auth_refresh_tokens_user_id ON auth_refresh_tokens (user_id);
CREATE INDEX idx_auth_refresh_tokens_jti ON auth_refresh_tokens (jti);
CREATE INDEX idx_auth_refresh_tokens_tokenhash ON auth_refresh_tokens (token_hash);
