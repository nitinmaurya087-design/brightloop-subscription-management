CREATE TABLE customers (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(160) NOT NULL,
  email VARCHAR(190) NOT NULL UNIQUE,
  phone VARCHAR(40),
  company_name VARCHAR(160),
  billing_address VARCHAR(255),
  city VARCHAR(100), state VARCHAR(100), country VARCHAR(100), postal_code VARCHAR(30), tax_id VARCHAR(80),
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_customers_name (full_name), INDEX idx_customers_status (status)
);
CREATE TABLE products (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(160) NOT NULL,
  product_type VARCHAR(80) NOT NULL,
  description TEXT,
  sales_price DECIMAL(12,2) NOT NULL,
  cost_price DECIMAL(12,2),
  notes TEXT,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_products_status (status)
);
CREATE TABLE product_variants (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT NOT NULL,
  name VARCHAR(120) NOT NULL,
  attribute_value VARCHAR(255),
  additional_price DECIMAL(12,2) NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_variant_product FOREIGN KEY (product_id) REFERENCES products(id),
  INDEX idx_variants_product (product_id)
);
CREATE TABLE plans (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT NOT NULL,
  name VARCHAR(160) NOT NULL,
  description TEXT,
  billing_price DECIMAL(12,2) NOT NULL,
  billing_frequency VARCHAR(20) NOT NULL,
  minimum_quantity INT NOT NULL DEFAULT 1,
  available_from DATE,
  available_until DATE,
  auto_close_on_expiry BOOLEAN NOT NULL DEFAULT TRUE,
  allow_early_closure BOOLEAN NOT NULL DEFAULT TRUE,
  allow_pause BOOLEAN NOT NULL DEFAULT TRUE,
  allow_renewal BOOLEAN NOT NULL DEFAULT TRUE,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_plan_product FOREIGN KEY (product_id) REFERENCES products(id),
  INDEX idx_plans_status (status), INDEX idx_plans_product (product_id)
);
