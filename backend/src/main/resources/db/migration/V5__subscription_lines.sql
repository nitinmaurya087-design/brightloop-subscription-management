CREATE TABLE subscription_lines (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, subscription_id BIGINT NOT NULL, product_id BIGINT NOT NULL,
 variant_id BIGINT, description VARCHAR(255) NOT NULL, quantity INT NOT NULL,
 unit_price DECIMAL(12,2) NOT NULL, tax_rate DECIMAL(12,4) NOT NULL DEFAULT 0,
 CONSTRAINT fk_subscription_line_subscription FOREIGN KEY(subscription_id) REFERENCES subscriptions(id),
 CONSTRAINT fk_subscription_line_product FOREIGN KEY(product_id) REFERENCES products(id),
 CONSTRAINT fk_subscription_line_variant FOREIGN KEY(variant_id) REFERENCES product_variants(id),
 INDEX idx_subscription_lines_subscription(subscription_id)
);
