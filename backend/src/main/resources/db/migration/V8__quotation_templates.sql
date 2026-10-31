CREATE TABLE quotation_templates (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, name VARCHAR(140) NOT NULL UNIQUE,
 description VARCHAR(500), validity_days INT NOT NULL DEFAULT 14,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE quotation_template_lines (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, template_id BIGINT NOT NULL,
 product_id BIGINT NOT NULL, plan_id BIGINT, variant_id BIGINT, quantity INT NOT NULL DEFAULT 1,
 CONSTRAINT fk_template_line_template FOREIGN KEY(template_id) REFERENCES quotation_templates(id) ON DELETE CASCADE,
 CONSTRAINT fk_template_line_product FOREIGN KEY(product_id) REFERENCES products(id),
 CONSTRAINT fk_template_line_plan FOREIGN KEY(plan_id) REFERENCES plans(id),
 CONSTRAINT fk_template_line_variant FOREIGN KEY(variant_id) REFERENCES product_variants(id),
 INDEX idx_template_lines_template(template_id)
);
