-- Sample test data for Table Order system

-- 1. Store 생성
INSERT INTO store (store_code, name) VALUES ('STORE001', 'Test Store');

-- 2. Admin 생성 (password: "password")
INSERT INTO admin (store_id, username, password, login_attempts) 
VALUES (1, 'admin', '$2b$10$dAzG9tVybxdT9Q6hhx4BN.ferwq2zQTi4W01JjJ7.SeXoRmog0Jzu', 0);

-- 3. Table 생성
INSERT INTO "table" (store_id, table_number, password) 
VALUES (1, '1', '$2b$10$dAzG9tVybxdT9Q6hhx4BN.ferwq2zQTi4W01JjJ7.SeXoRmog0Jzu');

-- 4. Active Session 생성
INSERT INTO table_session (store_id, table_id, is_active) 
VALUES (1, 1, true);

-- 5. Sample Orders 생성
INSERT INTO orders (store_id, table_id, session_id, order_number, status, total_amount) 
VALUES 
(1, 1, 1, 'ORD001', 'PENDING', 15000.00),
(1, 1, 1, 'ORD002', 'PREPARING', 8000.00);
