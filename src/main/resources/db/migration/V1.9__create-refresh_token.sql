CREATE TABLE refresh_tokens (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Index pour améliorer les performances de recherche
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expiry_date ON refresh_tokens(expiry_date);

-- Commentaires pour documentation
COMMENT ON TABLE refresh_tokens IS 'Table pour stocker les refresh tokens JWT';
COMMENT ON COLUMN refresh_tokens.id IS 'Identifiant unique du refresh token';
COMMENT ON COLUMN refresh_tokens.user_id IS 'Référence vers l\'utilisateur propriétaire du token';
COMMENT ON COLUMN refresh_tokens.token IS 'Valeur unique du refresh token (UUID)';
COMMENT ON COLUMN refresh_tokens.expiry_date IS 'Date d\'expiration du refresh token';