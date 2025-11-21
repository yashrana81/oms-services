-- Seed data for order statuses (safe to run multiple times)
INSERT IGNORE INTO order_status (id, name) VALUES (1, 'ordered');
INSERT IGNORE INTO order_status (id, name) VALUES (2, 'cancelled');
INSERT IGNORE INTO order_status (id, name) VALUES (3, 'shipped');
INSERT IGNORE INTO order_status (id, name) VALUES (4, 'delivered');