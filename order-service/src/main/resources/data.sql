-- Seed data for order statuses (required for application to work)
INSERT INTO order_status (id, name) VALUES (1, 'ordered');
INSERT INTO order_status (id, name) VALUES (2, 'cancelled');
INSERT INTO order_status (id, name) VALUES (3, 'shipped');
INSERT INTO order_status (id, name) VALUES (4, 'delivered');