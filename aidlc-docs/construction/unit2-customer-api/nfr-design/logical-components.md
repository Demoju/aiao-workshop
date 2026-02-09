# Logical Components - Unit 2 (Backend Customer API)

## Overview

이 문서는 Unit 2 (Backend Customer API)의 NFR 요구사항을 지원하기 위한 논리적 컴포넌트와 인프라 요소를 정의합니다.

---

## 1. Infrastructure Components

### 1.1 Redis (Session & Cache)

**용도**:
- Session 저장소
- Idempotency Key 캐시

**설정**:
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: ${REDIS_PASSWORD}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
```

**데이터 구조**:
```
# Session
Key: "session:{sessionId}"
Value: TableSession (JSON)
TTL: 24 hours

# Idempotency
Key: "idempotency:{idempotencyKey}"
Value: OrderResponseDto (JSON)
TTL: 24 hours
```

**근거**:
- 빠른 Session 조회
- 서버 재시작 시에도 Session 유지
- 중복 요청 방지

---

### 1.2 PostgreSQL Database

**용도**: 영구 데이터 저장

**Connection Pool**: HikariCP (Spring Boot 기본)

**설정**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tableorder
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
```

**테이블 구조**:
```sql
-- Tables
CREATE TABLE tables (
    table_id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL,
    table_number VARCHAR(10) NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

-- Menus
CREATE TABLE menus (
    menu_id BIGSERIAL PRIMARY KEY,
    menu_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    category_id BIGINT,
    is_available BOOLEAN DEFAULT true
);

-- Orders
CREATE TABLE orders (
    order_id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    table_id BIGINT NOT NULL,
    session_id BIGINT,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    order_time TIMESTAMP NOT NULL
);

-- Order Items
CREATE TABLE order_items (
    order_item_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL
);

-- Indexes
CREATE INDEX idx_orders_table_id ON orders(table_id);
CREATE INDEX idx_orders_session_id ON orders(session_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_menu_id ON order_items(menu_id);
```

---

## 2. Application Components

### 2.1 Controller Layer

**책임**: HTTP 요청/응답 처리

**컴포넌트**:
- `OrderController`: 주문 관련 API
- `MenuController`: 메뉴 조회 API
- `AuthController`: 인증 API

**패턴**:
- @RestController
- @RequestMapping
- @Valid 입력 검증
- ResponseEntity 반환

---

### 2.2 Service Layer

**책임**: 비즈니스 로직 처리

**컴포넌트**:
- `OrderService`: 주문 생성, 조회, 취소
- `MenuService`: 메뉴 조회, 검증
- `AuthService`: 로그인, 비밀번호 검증
- `SessionService`: Session 관리

**패턴**:
- @Service
- @Transactional
- DTO 변환
- 비즈니스 규칙 검증

---

### 2.3 Repository Layer (MyBatis Mapper)

**책임**: 데이터 접근

**컴포넌트**:
- `OrderMapper`: Order CRUD
- `OrderItemMapper`: OrderItem CRUD
- `MenuMapper`: Menu 조회
- `TableMapper`: Table 조회

**패턴**:
- @Mapper
- XML 기반 SQL
- ResultMap

---

### 2.4 Security Components

**컴포넌트**:
- `SecurityConfig`: Spring Security 설정
- `SessionAuthenticationFilter`: Session 인증 필터
- `BCryptPasswordEncoder`: 비밀번호 해싱

**패턴**:
- Filter Chain
- Authentication
- Authorization

---

### 2.5 Exception Handling Components

**컴포넌트**:
- `GlobalExceptionHandler`: 전역 예외 처리
- `ErrorCode`: 에러 코드 enum
- Custom Exceptions:
  - `BusinessException`
  - `UnauthorizedException`
  - `PriceChangedException`
  - `MenuNotFoundException`

**패턴**:
- @RestControllerAdvice
- @ExceptionHandler
- ErrorResponse DTO

---

## 3. Cross-Cutting Concerns

### 3.1 Logging

**컴포넌트**: SLF4J + Logback

**설정**: logback-spring.xml

**로그 파일**:
- `logs/application.log`: 현재 로그
- `logs/application.{date}.log`: 일일 로테이션

---

### 3.2 Monitoring

**컴포넌트**: Spring Boot Actuator

**Endpoints**:
- `/actuator/health`: Health check
- `/actuator/metrics`: 메트릭

---

### 3.3 API Documentation

**컴포넌트**: Swagger/OpenAPI

**Endpoints**:
- `/swagger-ui.html`: Swagger UI
- `/v3/api-docs`: OpenAPI Spec

---

## 4. Component Interaction Diagram

```
┌─────────────┐
│   Client    │
│  (Frontend) │
└──────┬──────┘
       │ HTTP
       ▼
┌─────────────────────────────────────┐
│      Spring Security Filter         │
│  (SessionAuthenticationFilter)      │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│         Controller Layer            │
│  (OrderController, MenuController)  │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│          Service Layer              │
│  (OrderService, SessionService)     │
└──────┬──────────────────────────────┘
       │
       ├──────────────┬─────────────┐
       ▼              ▼             ▼
┌──────────┐   ┌──────────┐  ┌──────────┐
│  MyBatis │   │  Redis   │  │ BCrypt   │
│  Mapper  │   │ Template │  │ Encoder  │
└────┬─────┘   └──────────┘  └──────────┘
     │
     ▼
┌──────────┐
│PostgreSQL│
│ Database │
└──────────┘
```

---

## 5. Deployment Architecture

```
┌─────────────────────────────────────┐
│         Application Server          │
│                                     │
│  ┌─────────────────────────────┐   │
│  │   Spring Boot Application   │   │
│  │   (Port 8080)               │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │   Embedded Tomcat           │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
              │
              ├──────────────┬─────────────┐
              ▼              ▼             ▼
     ┌──────────────┐ ┌──────────┐ ┌──────────┐
     │  PostgreSQL  │ │  Redis   │ │   Logs   │
     │  (Port 5432) │ │(Port 6379│ │  (File)  │
     └──────────────┘ └──────────┘ └──────────┘
```

---

## 6. Configuration Management

### 6.1 Environment Variables

```bash
# Database
DB_USERNAME=tableorder_user
DB_PASSWORD=<password>

# Redis
REDIS_PASSWORD=<password>

# Application
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev
```

### 6.2 Application Profiles

**application.yml** (공통):
```yaml
spring:
  application:
    name: table-order-api
```

**application-dev.yml** (개발):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tableorder_dev
logging:
  level:
    com.example: DEBUG
```

**application-prod.yml** (프로덕션):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://prod-db:5432/tableorder
logging:
  level:
    com.example: INFO
```

---

## Summary

이 문서는 Unit 2 (Backend Customer API)의 논리적 컴포넌트와 인프라 요소를 정의했습니다. Redis, PostgreSQL, Spring Security, MyBatis 등의 컴포넌트가 어떻게 상호작용하는지 명확히 하여 Code Generation 단계에서 실제 구현의 기반을 제공합니다.

