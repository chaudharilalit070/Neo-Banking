CREATE INDEX idx_customers_status
    ON customers (customer_status);

CREATE INDEX idx_customers_name
    ON customers (last_name, first_name);

CREATE INDEX idx_customers_created_at
    ON customers (created_at);