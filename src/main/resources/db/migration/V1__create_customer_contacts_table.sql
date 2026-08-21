CREATE TABLE customer_contacts (

    id BIGINT NOT NULL AUTO_INCREMENT,

    customer_id BIGINT NOT NULL,

    contact_type VARCHAR(20) NOT NULL,

    contact_value VARCHAR(255) NOT NULL,

    is_primary BOOLEAN NOT NULL DEFAULT FALSE,

    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    verified_at DATETIME NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT pk_customer_contacts
        PRIMARY KEY (id),

    CONSTRAINT fk_customer_contacts_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id),

    CONSTRAINT uk_customer_contact
        UNIQUE (customer_id, contact_type, contact_value)
);

CREATE INDEX idx_customer_contacts_customer_id
    ON customer_contacts(customer_id);

CREATE INDEX idx_customer_contacts_contact_value
    ON customer_contacts(contact_value);

CREATE INDEX idx_customer_contacts_customer_primary
    ON customer_contacts(customer_id, is_primary);