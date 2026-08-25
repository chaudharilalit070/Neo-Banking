CREATE TABLE customer_consents (

    id BIGINT NOT NULL AUTO_INCREMENT,

    customer_id BIGINT NOT NULL,

    consent_type VARCHAR(50) NOT NULL,

    status VARCHAR(20) NOT NULL,

    consent_version VARCHAR(50) NOT NULL,

    consent_text_version VARCHAR(50) NOT NULL,

    source VARCHAR(30) NOT NULL,

    granted_at DATETIME NULL,

    withdrawn_at DATETIME NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_customer_consents_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT
);