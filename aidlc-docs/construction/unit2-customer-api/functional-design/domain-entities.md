# Domain Entities - Unit 2 (Backend Customer API)

## Overview

Backend Customer API에서 사용하는 도메인 엔티티를 Java class로 정의합니다.

---

## 1. Store Domain

### Store
```java
@Data
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;
    
    @Column(nullable = false, length = 100)
    private String storeName;
    
    @Column(nullable = false, unique = true, length = 50)
    private String storeCode;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
```

**설명**: 매장 정보

**사용처**:
- Table, Menu, Category, Admin과 관계
- 로그인 시 storeId 사용

---

## 2. Table Domain

### Table
```java
@Data
@Table(name = "tables")
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tableId;
    
    @Column(nullable = false, length = 20)
    private String tableNumber;
    
    @Column(nullable = false)
    private Long storeId;
    
    @Column(nullable = false, length = 255)
    private String passwordHash;  // bcrypt 해싱
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
```

**설명**: 테이블 정보

**사용처**:
- 테이블 로그인
- 주문 생성 시 tableId 사용
- TableSession과 1:N 관계

---

### TableSession
```java
@Data
@Table(name = "table_sessions")
public class TableSession {
    @Id
    @Column(length = 36)
    private String sessionId;  // UUID
    
    @Column(nullable = false)
    private Long tableId;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    @Column
    private LocalDateTime endTime;
    
    @Column(nullable = false)
    private Boolean isActive;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
```

**설명**: 테이블 세션 정보

**사용처**:
- 주문 생성 시 sessionId 사용
- 주문 내역 조회 시 세션 필터링
- 세션 종료 시 endTime 설정

---

## 3. Menu Domain

### Menu
```java
@Data
@Table(name = "menus")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;
    
    @Column(nullable = false, length = 100)
    private String menuName;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(length = 500)
    private String description;
    
    @Column(length = 500)
    private String imageUrl;
    
    @Column(nullable = false)
    private Long categoryId;
    
    @Column(nullable = false)
    private Long storeId;
    
    @Column(nullable = false)
    private Boolean isAvailable;  // 품절 여부
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
}
```

**설명**: 메뉴 정보

**사용처**:
- 메뉴 조회 API
- 주문 생성 시 메뉴 검증
- OrderItem과 N:1 관계

---

### Category
```java
@Data
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    
    @Column(nullable = false, length = 50)
    private String categoryName;
    
    @Column(nullable = false)
    private Long storeId;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
```

**설명**: 메뉴 카테고리

**사용처**:
- 카테고리 조회 API
- 메뉴 조회 시 카테고리 필터링

---

## 4. Order Domain

### Order
```java
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    
    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;  // ORD-YYYYMMDD-NNN
    
    @Column(nullable = false)
    private Long tableId;
    
    @Column(nullable = false, length = 36)
    private String sessionId;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @Column(nullable = false)
    private LocalDateTime orderTime;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    // Transient field (not stored in DB)
    @Transient
    private List<OrderItem> items;
}
```

**설명**: 주문 정보

**사용처**:
- 주문 생성 API
- 주문 내역 조회 API
- 주문 취소 API
- OrderItem과 1:N 관계

---

### OrderItem
```java
@Data
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;
    
    @Column(nullable = false)
    private Long orderId;
    
    @Column(nullable = false)
    private Long menuId;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;  // 주문 시점 가격
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    // Transient field (not stored in DB)
    @Transient
    private String menuName;  // Menu 테이블 JOIN
}
```

**설명**: 주문 항목

**사용처**:
- Order 객체 내부
- 주문 내역 조회 시 메뉴 정보 포함

---

### OrderStatus
```java
public enum OrderStatus {
    PENDING("대기중"),
    PREPARING("준비중"),
    COMPLETED("완료"),
    CANCELLED("취소됨");
    
    private final String description;
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
```

**설명**: 주문 상태

**사용처**:
- Order 엔티티
- 주문 상태 변경 (Admin API)

---

## 5. Admin Domain

### Admin
```java
@Data
@Table(name = "admins")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false, length = 255)
    private String passwordHash;  // bcrypt 해싱
    
    @Column(nullable = false)
    private Long storeId;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
}
```

**설명**: 관리자 정보

**사용처**:
- 관리자 로그인 (Admin API)
- JWT 토큰 발급

---

## 6. DTO (Data Transfer Objects)

### TableLoginRequestDto
```java
@Data
public class TableLoginRequestDto {
    @NotNull(message = "매장 ID는 필수입니다")
    private Long storeId;
    
    @NotBlank(message = "테이블 번호는 필수입니다")
    @Size(max = 20)
    private String tableNumber;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}
```

**설명**: 테이블 로그인 요청 DTO

**사용처**:
- POST /api/customer/login

---

### TableLoginResponseDto
```java
@Data
@AllArgsConstructor
public class TableLoginResponseDto {
    private Long tableId;
    private String sessionId;
    private Long storeId;
    private String tableNumber;
}
```

**설명**: 테이블 로그인 응답 DTO

**사용처**:
- POST /api/customer/login 응답

---

### MenuResponseDto
```java
@Data
@AllArgsConstructor
public class MenuResponseDto {
    private Long menuId;
    private String menuName;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private Boolean isAvailable;
}
```

**설명**: 메뉴 응답 DTO

**사용처**:
- GET /api/customer/menus 응답

---

### CategoryResponseDto
```java
@Data
@AllArgsConstructor
public class CategoryResponseDto {
    private Long categoryId;
    private String categoryName;
    private Long storeId;
}
```

**설명**: 카테고리 응답 DTO

**사용처**:
- GET /api/customer/categories 응답

---

### OrderRequestDto
```java
@Data
public class OrderRequestDto {
    @NotNull(message = "테이블 ID는 필수입니다")
    private Long tableId;
    
    @NotBlank(message = "세션 ID는 필수입니다")
    private String sessionId;
    
    @NotEmpty(message = "주문 항목은 필수입니다")
    @Valid
    private List<OrderItemRequestDto> items;
    
    @NotNull(message = "총 금액은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal totalAmount;
}
```

**설명**: 주문 생성 요청 DTO

**사용처**:
- POST /api/customer/orders

---

### OrderItemRequestDto
```java
@Data
public class OrderItemRequestDto {
    @NotNull(message = "메뉴 ID는 필수입니다")
    private Long menuId;
    
    @NotNull(message = "수량은 필수입니다")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다")
    private Integer quantity;
    
    @NotNull(message = "단가는 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal unitPrice;
}
```

**설명**: 주문 항목 요청 DTO

**사용처**:
- OrderRequestDto 내부

---

### OrderResponseDto
```java
@Data
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private String orderNumber;
    private Long tableId;
    private String sessionId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime orderTime;
    private List<OrderItemResponseDto> items;
}
```

**설명**: 주문 응답 DTO

**사용처**:
- POST /api/customer/orders 응답
- GET /api/customer/orders 응답

---

### OrderItemResponseDto
```java
@Data
@AllArgsConstructor
public class OrderItemResponseDto {
    private Long orderItemId;
    private Long menuId;
    private String menuName;
    private Integer quantity;
    private BigDecimal unitPrice;
}
```

**설명**: 주문 항목 응답 DTO

**사용처**:
- OrderResponseDto 내부

---

### ErrorResponseDto
```java
@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private Integer status;
    private String message;
    private String timestamp;  // ISO 8601 format
    private String path;
}
```

**설명**: 에러 응답 DTO

**사용처**:
- GlobalExceptionHandler
- 모든 에러 응답

---

## 7. Entity Relationships

```
Store (1) ─── (N) Table
Store (1) ─── (N) Menu
Store (1) ─── (N) Category
Store (1) ─── (N) Admin

Table (1) ─── (N) TableSession
Table (1) ─── (N) Order

TableSession (1) ─── (N) Order

Category (1) ─── (N) Menu

Order (1) ─── (N) OrderItem

Menu (1) ─── (N) OrderItem
```

---

## 8. Database Schema

### Tables

#### stores
```sql
CREATE TABLE stores (
    store_id BIGSERIAL PRIMARY KEY,
    store_name VARCHAR(100) NOT NULL,
    store_code VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

#### tables
```sql
CREATE TABLE tables (
    table_id BIGSERIAL PRIMARY KEY,
    table_number VARCHAR(20) NOT NULL,
    store_id BIGINT NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(store_id),
    UNIQUE (store_id, table_number)
);
```

#### table_sessions
```sql
CREATE TABLE table_sessions (
    session_id VARCHAR(36) PRIMARY KEY,
    table_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (table_id) REFERENCES tables(table_id)
);
```

#### categories
```sql
CREATE TABLE categories (
    category_id BIGSERIAL PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL,
    store_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(store_id)
);
```

#### menus
```sql
CREATE TABLE menus (
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
```

#### orders
```sql
CREATE TABLE orders (
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

CREATE INDEX idx_orders_session ON orders(session_id);
CREATE INDEX idx_orders_table ON orders(table_id);
CREATE INDEX idx_orders_order_time ON orders(order_time);
```

#### order_items
```sql
CREATE TABLE order_items (
    order_item_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES menus(menu_id)
);

CREATE INDEX idx_order_items_order ON order_items(order_id);
```

#### admins
```sql
CREATE TABLE admins (
    admin_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    store_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(store_id)
);
```

---

## 9. MyBatis Mapper Interfaces

### CustomerMapper
```java
@Mapper
public interface CustomerMapper {
    // 테이블 로그인
    Table selectTableByCredentials(@Param("storeId") Long storeId, 
                                   @Param("tableNumber") String tableNumber);
    
    // 메뉴 조회
    List<Menu> selectMenus(@Param("categoryId") Long categoryId);
    
    // 카테고리 조회
    List<Category> selectCategories();
    
    // 주문 생성
    void insertOrder(Order order);
    void insertOrderItems(@Param("items") List<OrderItem> items);
    
    // 주문 내역 조회
    List<Order> selectOrdersBySession(@Param("tableId") Long tableId, 
                                      @Param("sessionId") String sessionId);
    List<OrderItem> selectOrderItemsByOrderId(@Param("orderId") Long orderId);
    
    // 주문 취소
    void deleteOrderItems(@Param("orderId") Long orderId);
    void deleteOrder(@Param("orderId") Long orderId);
    Order selectOrderById(@Param("orderId") Long orderId);
    
    // 세션 관리
    TableSession selectActiveSession(@Param("tableId") Long tableId);
    void insertTableSession(TableSession session);
    
    // 주문 번호 생성
    Integer countTodayOrders();
}
```

---

## Notes

- 모든 엔티티는 Java class로 정의
- DTO는 API 요청/응답 전용
- MyBatis Mapper는 데이터 액세스 레이어
- Database schema는 PostgreSQL 기준

