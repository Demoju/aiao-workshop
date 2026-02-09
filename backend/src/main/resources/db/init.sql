-- Create tables

CREATE TABLE IF NOT EXISTS stores (
    store_id BIGSERIAL PRIMARY KEY,
    store_name VARCHAR(100) NOT NULL,
    store_code VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tables (
    table_id BIGSERIAL PRIMARY KEY,
    table_number VARCHAR(20) NOT NULL,
    store_id BIGINT NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(store_id),
    UNIQUE (store_id, table_number)
);

CREATE TABLE IF NOT EXISTS table_sessions (
    session_id VARCHAR(36) PRIMARY KEY,
    table_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (table_id) REFERENCES tables(table_id)
);

CREATE TABLE IF NOT EXISTS categories (
    category_id BIGSERIAL PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL,
    store_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(store_id)
);

CREATE TABLE IF NOT EXISTS menus (
    menu_id BIGSERIAL PRIMARY KEY,
    menu_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    description VARCHAR(500),
    image_url VARCHAR(500),
    category_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (store_id) REFERENCES stores(store_id)
);

CREATE TABLE IF NOT EXISTS orders (
    order_id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    table_id BIGINT NOT NULL,
    session_id VARCHAR(36) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    order_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (table_id) REFERENCES tables(table_id),
    FOREIGN KEY (session_id) REFERENCES table_sessions(session_id)
);

CREATE TABLE IF NOT EXISTS order_items (
    order_item_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES menus(menu_id)
);

CREATE TABLE IF NOT EXISTS admins (
    admin_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    store_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(store_id)
);

-- Create indexes

CREATE INDEX IF NOT EXISTS idx_orders_session ON orders(session_id);
CREATE INDEX IF NOT EXISTS idx_orders_table ON orders(table_id);
CREATE INDEX IF NOT EXISTS idx_orders_order_time ON orders(order_time);
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_menus_category ON menus(category_id);

-- Insert sample data

INSERT INTO stores (store_name, store_code) VALUES
('테이블오더 본점', 'STORE001')
ON CONFLICT DO NOTHING;

INSERT INTO tables (table_number, store_id, password_hash) VALUES
('T01', 1, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'), -- password: 1234
('T02', 1, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('T03', 1, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('T04', 1, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('T05', 1, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy')
ON CONFLICT DO NOTHING;

INSERT INTO categories (category_name, store_id) VALUES
('메인 요리', 1),
('사이드 메뉴', 1),
('음료', 1),
('디저트', 1)
ON CONFLICT DO NOTHING;

INSERT INTO menus (menu_name, price, description, image_url, category_id, store_id, is_available) VALUES
('김치찌개', 8000.00, '얼큰한 김치찌개', 'https://example.com/kimchi.jpg', 1, 1, true),
('된장찌개', 7000.00, '구수한 된장찌개', 'https://example.com/doenjang.jpg', 1, 1, true),
('불고기', 15000.00, '달콤한 불고기', 'https://example.com/bulgogi.jpg', 1, 1, true),
('비빔밥', 9000.00, '영양 만점 비빔밥', 'https://example.com/bibimbap.jpg', 1, 1, true),
('냉면', 10000.00, '시원한 냉면', 'https://example.com/naengmyeon.jpg', 1, 1, false),
('김치전', 12000.00, '바삭한 김치전', 'https://example.com/kimchijeon.jpg', 2, 1, true),
('계란말이', 8000.00, '부드러운 계란말이', 'https://example.com/egg.jpg', 2, 1, true),
('콜라', 2000.00, '시원한 콜라', 'https://example.com/cola.jpg', 3, 1, true),
('사이다', 2000.00, '청량한 사이다', 'https://example.com/sprite.jpg', 3, 1, true),
('아이스크림', 3000.00, '달콤한 아이스크림', 'https://example.com/icecream.jpg', 4, 1, true)
ON CONFLICT DO NOTHING;

INSERT INTO admins (username, password_hash, store_id) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1) -- password: 1234
ON CONFLICT DO NOTHING;
