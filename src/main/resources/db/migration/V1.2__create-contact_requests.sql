CREATE TABLE contact_requests (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subject VARCHAR(100) NOT NULL,
    message VARCHAR(2000) NOT NULL,
    email_sent BOOLEAN NOT NULL DEFAULT FALSE,
    whatsapp_sent BOOLEAN NOT NULL DEFAULT FALSE,
    email_sent_at TIMESTAMP,
    whatsapp_sent_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    CONSTRAINT fk_contact_user FOREIGN KEY (user_id) REFERENCES users(id)
);
