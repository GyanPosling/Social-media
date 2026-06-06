CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(254),
    phone VARCHAR(32),
    display_name VARCHAR(100) NOT NULL,
    bio VARCHAR(500),
    avatar_url VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_users_username UNIQUE (username)
);

CREATE TABLE user_follows (
    id BIGSERIAL PRIMARY KEY,
    follower_id UUID NOT NULL,
    following_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_user_follows_follower_following UNIQUE (follower_id, following_id),
    CONSTRAINT chk_user_follows_not_self CHECK (follower_id <> following_id),
    CONSTRAINT fk_user_follows_follower
        FOREIGN KEY (follower_id)
        REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_follows_following
        FOREIGN KEY (following_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_users_username ON users (username);
CREATE UNIQUE INDEX uk_users_email ON users (email)
WHERE email IS NOT NULL;
CREATE UNIQUE INDEX uk_users_phone ON users (phone)
WHERE phone IS NOT NULL;
CREATE INDEX idx_user_follows_follower_id ON user_follows (follower_id);
CREATE INDEX idx_user_follows_following_id ON user_follows (following_id);
