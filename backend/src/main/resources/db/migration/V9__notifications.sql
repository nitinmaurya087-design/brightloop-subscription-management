CREATE TABLE notifications (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 recipient_email VARCHAR(190) NOT NULL,
 notification_type VARCHAR(40) NOT NULL,
 subject VARCHAR(190) NOT NULL,
 content TEXT NOT NULL,
 event_key VARCHAR(190) NOT NULL UNIQUE,
 delivery_status VARCHAR(20) NOT NULL,
 error_message VARCHAR(500),
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 INDEX idx_notifications_email(recipient_email), INDEX idx_notifications_type(notification_type), INDEX idx_notifications_created(created_at)
);
