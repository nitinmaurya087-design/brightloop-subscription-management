CREATE TABLE audit_logs (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 actor_email VARCHAR(190) NOT NULL,
 action VARCHAR(60) NOT NULL,
 entity_type VARCHAR(40) NOT NULL,
 entity_id BIGINT,
 details VARCHAR(1000),
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 INDEX idx_audit_entity(entity_type, entity_id),
 INDEX idx_audit_created(created_at)
);
