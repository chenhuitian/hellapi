INSERT INTO products (name, price, description, deleted, deleted_at) VALUES
('Keyboard', 199.99, 'Mechanical keyboard', FALSE, NULL),
('Mouse', 49.90, 'Wireless mouse', FALSE, NULL),
('Monitor', 1299.00, '27-inch 4K display', FALSE, NULL);

INSERT INTO roles (name) VALUES ('USER'), ('ADMIN');

INSERT INTO role_permissions (role_id, permission) SELECT id, 'PRODUCT_READ' FROM roles WHERE name = 'USER' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'PRODUCT_CREATE' FROM roles WHERE name = 'USER' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'PRODUCT_UPDATE' FROM roles WHERE name = 'USER' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'PRODUCT_READ' FROM roles WHERE name = 'ADMIN' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'PRODUCT_CREATE' FROM roles WHERE name = 'ADMIN' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'PRODUCT_UPDATE' FROM roles WHERE name = 'ADMIN' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'PRODUCT_DELETE' FROM roles WHERE name = 'ADMIN' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'PRODUCT_LIST_DELETED' FROM roles WHERE name = 'ADMIN' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'PRODUCT_RESTORE' FROM roles WHERE name = 'ADMIN' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'ROLE_MANAGE' FROM roles WHERE name = 'ADMIN' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'TRADE_READ' FROM roles WHERE name = 'USER' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'TRADE_CREATE' FROM roles WHERE name = 'USER' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'TRADE_UPDATE' FROM roles WHERE name = 'USER' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'TRADE_READ' FROM roles WHERE name = 'ADMIN' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'TRADE_CREATE' FROM roles WHERE name = 'ADMIN' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'TRADE_UPDATE' FROM roles WHERE name = 'ADMIN' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'TRADE_DELETE' FROM roles WHERE name = 'ADMIN' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'TRADE_LIST_DELETED' FROM roles WHERE name = 'ADMIN' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'TRADE_RESTORE' FROM roles WHERE name = 'ADMIN' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'SINGAPORE_STOCK_READ' FROM roles WHERE name = 'USER' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'SINGAPORE_STOCK_TRADE' FROM roles WHERE name = 'USER' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'SINGAPORE_STOCK_READ' FROM roles WHERE name = 'ADMIN' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'SINGAPORE_STOCK_TRADE' FROM roles WHERE name = 'ADMIN' LIMIT 1;
INSERT INTO role_permissions (role_id, permission) SELECT id, 'SINGAPORE_STOCK_DELETE' FROM roles WHERE name = 'ADMIN' LIMIT 1;
