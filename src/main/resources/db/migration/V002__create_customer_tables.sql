CREATE TABLE customers (
    id CHAR(36) NOT NULL,

    customer_number VARCHAR(30) NOT NULL,

    customer_type VARCHAR(30) NOT NULL,
    customer_status VARCHAR(30) NOT NULL,

    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100) NULL,
    last_name VARCHAR(100) NOT NULL,

    date_of_birth DATE NOT NULL,

    nationality VARCHAR(3) NULL,

    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100) NOT NULL,

CREATE INDEX idx_customers_status
    ON customers (customer_status);

CREATE INDEX idx_customers_name
    ON customers (last_name, first_name);

CREATE INDEX idx_customers_created_at
    ON customers (created_at);

    updated_at TIMESTAMP(6) NULL DEFAULT NULL,
    updated_by VARCHAR(100) NULL,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT pk_customers PRIMARY KEY (id),

    CONSTRAINT uk_customers_customer_number
        UNIQUE (customer_number)

);
