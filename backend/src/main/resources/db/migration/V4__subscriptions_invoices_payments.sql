CREATE TABLE subscriptions (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, subscription_number VARCHAR(40) NOT NULL UNIQUE,
 customer_id BIGINT NOT NULL, plan_id BIGINT NOT NULL, start_date DATE NOT NULL, expiry_date DATE,
 billing_frequency VARCHAR(20) NOT NULL, payment_terms VARCHAR(255), status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
 quantity INT NOT NULL DEFAULT 1, unit_price DECIMAL(12,2) NOT NULL, subtotal DECIMAL(14,2) NOT NULL,
 tax_amount DECIMAL(14,2) NOT NULL, discount_amount DECIMAL(14,2) NOT NULL, grand_total DECIMAL(14,2) NOT NULL,
 next_billing_date DATE, paused_on DATE, auto_close_on_expiry BOOLEAN NOT NULL DEFAULT TRUE,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_subscription_customer FOREIGN KEY(customer_id) REFERENCES customers(id), CONSTRAINT fk_subscription_plan FOREIGN KEY(plan_id) REFERENCES plans(id),
 INDEX idx_subscriptions_status(status), INDEX idx_subscriptions_next_billing(next_billing_date), INDEX idx_subscriptions_customer(customer_id)
);
CREATE TABLE invoices (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, invoice_number VARCHAR(40) NOT NULL UNIQUE,
 customer_id BIGINT NOT NULL, subscription_id BIGINT, quotation_id BIGINT,
 billing_period_start DATE, billing_period_end DATE, issue_date DATE NOT NULL, due_date DATE NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'DRAFT', subtotal DECIMAL(14,2) NOT NULL, tax_amount DECIMAL(14,2) NOT NULL,
 discount_amount DECIMAL(14,2) NOT NULL, total DECIMAL(14,2) NOT NULL, amount_paid DECIMAL(14,2) NOT NULL DEFAULT 0,
 outstanding DECIMAL(14,2) NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_invoice_customer FOREIGN KEY(customer_id) REFERENCES customers(id),
 CONSTRAINT fk_invoice_subscription FOREIGN KEY(subscription_id) REFERENCES subscriptions(id),
 CONSTRAINT fk_invoice_quotation FOREIGN KEY(quotation_id) REFERENCES quotations(id),
 CONSTRAINT uq_invoice_subscription_period UNIQUE(subscription_id,billing_period_start),
 INDEX idx_invoices_status(status), INDEX idx_invoices_due_date(due_date), INDEX idx_invoices_customer(customer_id), INDEX idx_invoices_number(invoice_number)
);
CREATE TABLE invoice_lines (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, invoice_id BIGINT NOT NULL, product_id BIGINT NOT NULL,
 description VARCHAR(255) NOT NULL, quantity INT NOT NULL, unit_price DECIMAL(12,2) NOT NULL,
 tax_rate DECIMAL(12,4) NOT NULL, tax_amount DECIMAL(12,2) NOT NULL, line_total DECIMAL(14,2) NOT NULL,
 CONSTRAINT fk_invoice_line_invoice FOREIGN KEY(invoice_id) REFERENCES invoices(id), CONSTRAINT fk_invoice_line_product FOREIGN KEY(product_id) REFERENCES products(id), INDEX idx_invoice_lines_invoice(invoice_id)
);
CREATE TABLE payments (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, payment_number VARCHAR(40) NOT NULL UNIQUE,
 invoice_id BIGINT NOT NULL, customer_id BIGINT NOT NULL, amount DECIMAL(14,2) NOT NULL,
 payment_method VARCHAR(30) NOT NULL, transaction_reference VARCHAR(120), payment_date TIMESTAMP NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED', notes VARCHAR(500), created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT fk_payment_invoice FOREIGN KEY(invoice_id) REFERENCES invoices(id), CONSTRAINT fk_payment_customer FOREIGN KEY(customer_id) REFERENCES customers(id),
 INDEX idx_payments_customer(customer_id), INDEX idx_payments_date(payment_date), INDEX idx_payments_invoice(invoice_id)
);
CREATE TABLE quotation_conversion (
 quotation_id BIGINT NOT NULL PRIMARY KEY, subscription_id BIGINT NOT NULL UNIQUE,
 CONSTRAINT fk_conversion_quote FOREIGN KEY(quotation_id) REFERENCES quotations(id), CONSTRAINT fk_conversion_subscription FOREIGN KEY(subscription_id) REFERENCES subscriptions(id)
);
