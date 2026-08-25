CREATE TABLE customer_lifecycle (
    id BIGINT NOT NULL AUTO_INCREMENT,

    customer_id BIGINT NOT NULL,

    previous_status VARCHAR(30) NULL,

    current_status VARCHAR(30) NOT NULL,

    reason VARCHAR(50) NOT NULL,

    effective_at DATETIME NOT NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_customer_lifecycle_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id),

    INDEX idx_customer_lifecycle_customer_id (customer_id),

    INDEX idx_customer_lifecycle_customer_status (
        customer_id,
        current_status
    ),

    INDEX idx_customer_lifecycle_effective_at (
        customer_id,
        effective_at
    )
);