CREATE TABLE auth_accounts (
    id UUID PRIMARY KEY,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_auth_accounts_email UNIQUE (email)
);

CREATE INDEX idx_auth_accounts_email ON auth_accounts (email);
