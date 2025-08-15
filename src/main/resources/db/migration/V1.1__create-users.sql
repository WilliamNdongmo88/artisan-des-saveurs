CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    consent BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    activation_token TEXT,
    reset_password_token TEXT,
    reset_password_expiry TIMESTAMP,
    role_id BIGINT,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES role(id)
);
