CREATE TABLE password_reset_tokens (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 user_id BIGINT NOT NULL,
 token_hash VARCHAR(64) NOT NULL UNIQUE,
 expires_at TIMESTAMP NOT NULL,
 consumed BOOLEAN NOT NULL DEFAULT FALSE,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_reset_token_user FOREIGN KEY(user_id) REFERENCES app_users(id) ON DELETE CASCADE,
 INDEX idx_reset_user(user_id), INDEX idx_reset_expiry(expires_at)
);
