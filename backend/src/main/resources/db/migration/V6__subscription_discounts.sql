ALTER TABLE subscriptions ADD COLUMN discount_id BIGINT NULL;
ALTER TABLE subscriptions ADD CONSTRAINT fk_subscription_discount FOREIGN KEY(discount_id) REFERENCES discounts(id);
