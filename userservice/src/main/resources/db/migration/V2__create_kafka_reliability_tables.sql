CREATE TABLE IF NOT EXISTS user_outbox_events (
    id UUID PRIMARY KEY,
    aggregate_id VARCHAR(120) NOT NULL,
    topic VARCHAR(120) NOT NULL,
    event_type VARCHAR(120) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP WITH TIME ZONE,
    last_error VARCHAR(1000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    published_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_user_outbox_events_status_created_at
    ON user_outbox_events (status, created_at);

CREATE TABLE IF NOT EXISTS user_processed_kafka_messages (
    message_id VARCHAR(200) PRIMARY KEY,
    topic VARCHAR(120) NOT NULL,
    consumer_group VARCHAR(120) NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);
