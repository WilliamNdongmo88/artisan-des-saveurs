CREATE TABLE role (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    user_id BIGINT,
    CONSTRAINT fk_user_role FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
