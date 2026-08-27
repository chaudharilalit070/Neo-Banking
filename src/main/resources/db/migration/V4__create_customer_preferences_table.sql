CREATE TABLE customer_preferences (
    id BIGINT NOT NULL AUTO_INCREMENT,
    customer_id CHAR(36) NOT NULL,
    preferred_language VARCHAR(30) NOT NULL,
    preferred_communication_channel VARCHAR(30) NOT NULL,
    marketing_notifications BOOLEAN NOT NULL DEFAULT FALSE,
    transaction_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    security_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT uk_customer_preferences_customer_id UNIQUE (customer_id),
    CONSTRAINT fk_customer_preferences_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);