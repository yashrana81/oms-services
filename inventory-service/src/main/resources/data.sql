-- Seed data for sample products (safe to run multiple times)
INSERT IGNORE INTO products (id, name, description, category, price, stock_available, created_at, updated_at) 
VALUES (1, 'MacBook Pro', '16-inch laptop with M3 chip', 'Electronics', 2499.99, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT IGNORE INTO products (id, name, description, category, price, stock_available, created_at, updated_at) 
VALUES (2, 'iPhone 15 Pro', '128GB smartphone with A17 chip', 'Electronics', 999.99, 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT IGNORE INTO products (id, name, description, category, price, stock_available, created_at, updated_at) 
VALUES (3, 'Logitech MX Master 3', 'Wireless ergonomic mouse', 'Accessories', 99.99, 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT IGNORE INTO products (id, name, description, category, price, stock_available, created_at, updated_at) 
VALUES (4, 'Nike Air Max', 'Running shoes for men', 'Clothing', 149.99, 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT IGNORE INTO products (id, name, description, category, price, stock_available, created_at, updated_at) 
VALUES (5, 'Clean Code', 'A handbook of agile software craftsmanship', 'Books', 39.99, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT IGNORE INTO products (id, name, description, category, price, stock_available, created_at, updated_at) 
VALUES (6, 'PS5 Console', 'PlayStation 5 gaming console', 'Electronics', 499.99, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);