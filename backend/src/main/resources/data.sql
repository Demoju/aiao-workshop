-- Sample test data for Table Order system

-- 1. Store 생성
INSERT INTO store (store_code, name) VALUES ('STORE001', 'Test Store');

-- 2. Admin 생성 (password: "password")
INSERT INTO admin (store_id, username, password, login_attempts) 
VALUES (1, 'admin', '$2b$10$dAzG9tVybxdT9Q6hhx4BN.ferwq2zQTi4W01JjJ7.SeXoRmog0Jzu', 0);

-- 3. Table 생성
INSERT INTO "table" (store_id, table_number, password) 
VALUES (1, '1', '$2b$10$dAzG9tVybxdT9Q6hhx4BN.ferwq2zQTi4W01JjJ7.SeXoRmog0Jzu');

-- 4. Categories 생성
INSERT INTO categories (category_name, store_id) VALUES ('메인메뉴', 1);
INSERT INTO categories (category_name, store_id) VALUES ('사이드메뉴', 1);
INSERT INTO categories (category_name, store_id) VALUES ('음료', 1);

-- 5. Menus 생성
INSERT INTO menus (menu_name, price, description, category_id, store_id, is_available) VALUES
('김치찌개', 8000.00, '돼지고기 김치찌개', 1, 1, true),
('된장찌개', 7000.00, '두부 된장찌개', 1, 1, true),
('비빔밥', 9000.00, '야채 비빔밥', 1, 1, true),
('계란말이', 5000.00, '치즈 계란말이', 2, 1, true),
('김치전', 6000.00, '바삭한 김치전', 2, 1, true),
('콜라', 2000.00, '코카콜라 500ml', 3, 1, true),
('사이다', 2000.00, '칠성사이다 500ml', 3, 1, true);

-- 6. Active Session 생성
INSERT INTO table_session (store_id, table_id, session_id, is_active) 
VALUES (1, 1, 'session-001', true);

-- 7. Sample Orders 생성
INSERT INTO orders (store_id, table_id, session_id, order_number, status, total_amount) 
VALUES 
(1, 1, 1, 'ORD001', 'PENDING', 15000.00),
(1, 1, 1, 'ORD002', 'PREPARING', 8000.00);

-- 8. Order Items 생성
INSERT INTO order_items (order_id, menu_id, quantity, unit_price) VALUES
(1, 1, 1, 8000.00),
(1, 4, 1, 5000.00),
(1, 6, 1, 2000.00),
(2, 2, 1, 7000.00),
(2, 7, 1, 2000.00);
