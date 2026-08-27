CREATE TABLE customer_lifecycle (
    id BIGINT NOT NULL AUTO_INCREMENT,
    customer_id CHAR(36) NOT NULL,
    previous_status VARCHAR(30) NULL,
    current_status VARCHAR(30) NOT NULL,
    reason VARCHAR(50) NOT NULL,
    effective_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_customer_lifecycle_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE INDEX idx_customer_lifecycle_customer_id ON customer_lifecycle(customer_id);
CREATE INDEX idx_customer_lifecycle_customer_status ON customer_lifecycle(customer_id, current_status);
CREATE INDEX idx_customer_lifecycle_effective_at ON customer_lifecycle(customer_id, effective_at);
