CREATE TABLE taxes (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, name VARCHAR(120) NOT NULL, tax_type VARCHAR(20) NOT NULL,
 rate DECIMAL(12,4) NOT NULL, description VARCHAR(500), product_id BIGINT,
 effective_start DATE, effective_end DATE, active BOOLEAN NOT NULL DEFAULT TRUE,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_tax_product FOREIGN KEY(product_id) REFERENCES products(id), INDEX idx_taxes_active(active)
);
CREATE TABLE discounts (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, code VARCHAR(40) NOT NULL UNIQUE, name VARCHAR(120) NOT NULL,
 discount_type VARCHAR(20) NOT NULL, discount_value DECIMAL(12,4) NOT NULL,
 starts_at DATE, ends_at DATE, max_uses INT, usage_count INT NOT NULL DEFAULT 0,
 active BOOLEAN NOT NULL DEFAULT TRUE, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 INDEX idx_discounts_active(active), INDEX idx_discounts_date(ends_at)
);
CREATE TABLE quotations (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, quotation_number VARCHAR(40) NOT NULL UNIQUE,
 customer_id BIGINT NOT NULL, status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
 created_on DATE NOT NULL, expires_on DATE NOT NULL, payment_terms VARCHAR(255), notes TEXT,
 discount_id BIGINT, subtotal DECIMAL(14,2) NOT NULL, discount_amount DECIMAL(14,2) NOT NULL,
 tax_amount DECIMAL(14,2) NOT NULL, grand_total DECIMAL(14,2) NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_quote_customer FOREIGN KEY(customer_id) REFERENCES customers(id), CONSTRAINT fk_quote_discount FOREIGN KEY(discount_id) REFERENCES discounts(id),
 INDEX idx_quotes_customer(customer_id), INDEX idx_quotes_status(status), INDEX idx_quotes_expiry(expires_on)
);
CREATE TABLE quotation_lines (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, quotation_id BIGINT NOT NULL, product_id BIGINT NOT NULL,
 plan_id BIGINT, quantity INT NOT NULL, variant_id BIGINT, unit_price DECIMAL(12,2) NOT NULL, tax_rate DECIMAL(12,4) NOT NULL,
 line_subtotal DECIMAL(14,2) NOT NULL, line_tax DECIMAL(14,2) NOT NULL, line_total DECIMAL(14,2) NOT NULL,
 CONSTRAINT fk_quote_line_quote FOREIGN KEY(quotation_id) REFERENCES quotations(id), CONSTRAINT fk_quote_line_product FOREIGN KEY(product_id) REFERENCES products(id), CONSTRAINT fk_quote_line_variant FOREIGN KEY(variant_id) REFERENCES product_variants(id), CONSTRAINT fk_quote_line_plan FOREIGN KEY(plan_id) REFERENCES plans(id), INDEX idx_quote_lines_quote(quotation_id)
);
