CREATE TABLE audit_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    customer_id CHAR(36) NOT NULL,
    action VARCHAR(50) NOT NULL,
    previous_status VARCHAR(30) NULL,
    new_status VARCHAR(30) NULL,
    reason VARCHAR(100) NULL,
    actor_id VARCHAR(100) NULL,
    actor_type VARCHAR(30) NULL,
    correlation_id VARCHAR(100) NULL,
    occurred_at DATETIME NOT NULL,
    source VARCHAR(100) NOT NULL,

    PRIMARY KEY (id),

    INDEX idx_audit_customer_occurred (customer_id, occurred_at),
    INDEX idx_audit_correlation (correlation_id),

    CONSTRAINT fk_audit_events_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE event_outbox (
    id BIGINT NOT NULL AUTO_INCREMENT,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(100) NOT NULL,
    event_type VARCHAR(200) NOT NULL,
    payload JSON NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL,
    published_at DATETIME NULL,
    retry_count INT NOT NULL DEFAULT 0,

    PRIMARY KEY (id),

    INDEX idx_event_outbox_status_created (status, created_at),
    INDEX idx_event_outbox_aggregate (aggregate_type, aggregate_id)
);

CREATE TABLE processed_event (
    id BIGINT NOT NULL AUTO_INCREMENT,
    event_id VARCHAR(100) NOT NULL,
    event_type VARCHAR(200) NOT NULL,
    processed_at DATETIME NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT uk_processed_event_event_id UNIQUE (event_id)
);
