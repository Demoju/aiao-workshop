# Domain Entities - Unit 3 (Backend Admin API)

## Overview
Unit 3에서 사용하는 도메인 엔티티 정의

---

## Admin Entity

### Purpose
관리자 계정 정보

### Attributes
| Attribute | Type | Constraints | Description |
|-----------|------|-------------|-------------|
| id | Long | PK, Auto-increment | 관리자 ID |
| storeId | Long | FK, NOT NULL | 매장 ID |
| username | String | NOT NULL, UNIQUE | 사용자명 |
| password | String | NOT NULL | 비밀번호 (bcrypt 해시) |
| loginAttempts | Integer | DEFAULT 0 | 로그인 시도 횟수 |
| lockedUntil | Timestamp | NULLABLE | 계정 잠금 해제 시각 |
| createdAt | Timestamp | NOT NULL | 생성 시각 |
| updatedAt | Timestamp | NOT NULL | 수정 시각 |

### Business Rules
- username은 매장 내 유일
- password는 bcrypt로 해싱 저장
- loginAttempts가 5회 이상 시 계정 잠금 (30분)
- lockedUntil 시각 이후 자동 잠금 해제

---

## Order Entity (Shared)

### Purpose
주문 정보

### Attributes
| Attribute | Type | Constraints | Description |
|-----------|------|-------------|-------------|
| id | Long | PK, Auto-increment | 주문 ID |
| storeId | Long | FK, NOT NULL | 매장 ID |
| tableId | Long | FK, NOT NULL | 테이블 ID |
| sessionId | Long | FK, NOT NULL | 테이블 세션 ID |
| orderNumber | String | NOT NULL, UNIQUE | 주문 번호 |
| status | Enum | NOT NULL | 주문 상태 (PENDING, PREPARING, COMPLETED, CANCELLED) |
| totalAmount | BigDecimal | NOT NULL | 총 주문 금액 |
| createdAt | Timestamp | NOT NULL | 주문 생성 시각 |
| updatedAt | Timestamp | NOT NULL | 주문 수정 시각 |

### Status Enum
```java
public enum OrderStatus {
    PENDING,      // 대기중
    PREPARING,    // 준비중
    COMPLETED,    // 완료
    CANCELLED     // 취소
}
```

### Business Rules
- 상태 전환: PENDING → PREPARING → COMPLETED (순차적)
- 취소는 언제든 가능 (PENDING/PREPARING/COMPLETED → CANCELLED)
- 완료 또는 취소 상태만 삭제 가능
- orderNumber는 매장 내 유일

---

## OrderItem Entity (Shared)

### Purpose
주문 항목 정보

### Attributes
| Attribute | Type | Constraints | Description |
|-----------|------|-------------|-------------|
| id | Long | PK, Auto-increment | 주문 항목 ID |
| orderId | Long | FK, NOT NULL | 주문 ID |
| menuId | Long | FK, NOT NULL | 메뉴 ID |
| menuName | String | NOT NULL | 메뉴명 (스냅샷) |
| quantity | Integer | NOT NULL | 수량 |
| unitPrice | BigDecimal | NOT NULL | 단가 (스냅샷) |
| subtotal | BigDecimal | NOT NULL | 소계 (quantity * unitPrice) |

### Business Rules
- menuName, unitPrice는 주문 시점 스냅샷 (메뉴 변경 영향 없음)
- subtotal = quantity * unitPrice

---

## TableSession Entity (Shared)

### Purpose
테이블 세션 정보 (고객 이용 시작~종료)

### Attributes
| Attribute | Type | Constraints | Description |
|-----------|------|-------------|-------------|
| id | Long | PK, Auto-increment | 세션 ID |
| storeId | Long | FK, NOT NULL | 매장 ID |
| tableId | Long | FK, NOT NULL | 테이블 ID |
| startedAt | Timestamp | NOT NULL | 세션 시작 시각 |
| endedAt | Timestamp | NULLABLE | 세션 종료 시각 |
| isActive | Boolean | NOT NULL, DEFAULT true | 활성 상태 |

### Business Rules
- 테이블당 하나의 활성 세션만 존재 (isActive = true)
- 세션 종료 시 endedAt 설정, isActive = false
- 세션 종료 전 미완료 주문 확인 필요

---

## Table Entity (Shared)

### Purpose
테이블 정보

### Attributes
| Attribute | Type | Constraints | Description |
|-----------|------|-------------|-------------|
| id | Long | PK, Auto-increment | 테이블 ID |
| storeId | Long | FK, NOT NULL | 매장 ID |
| tableNumber | String | NOT NULL | 테이블 번호 |
| password | String | NOT NULL | 테이블 비밀번호 (bcrypt 해시) |
| createdAt | Timestamp | NOT NULL | 생성 시각 |

### Business Rules
- tableNumber는 매장 내 유일
- password는 bcrypt로 해싱 저장

---

## Store Entity (Shared)

### Purpose
매장 정보

### Attributes
| Attribute | Type | Constraints | Description |
|-----------|------|-------------|-------------|
| id | Long | PK, Auto-increment | 매장 ID |
| storeCode | String | NOT NULL, UNIQUE | 매장 식별자 |
| name | String | NOT NULL | 매장명 |
| createdAt | Timestamp | NOT NULL | 생성 시각 |

### Business Rules
- storeCode는 전역 유일

---

## Entity Relationships

```
Store (1) ─── (N) Admin
Store (1) ─── (N) Table
Store (1) ─── (N) Order
Table (1) ─── (N) TableSession
Table (1) ─── (N) Order
TableSession (1) ─── (N) Order
Order (1) ─── (N) OrderItem
```

### Key Relationships
- **Store → Admin**: 매장은 여러 관리자 보유
- **Store → Table**: 매장은 여러 테이블 보유
- **Table → TableSession**: 테이블은 여러 세션 보유 (시간순)
- **TableSession → Order**: 세션은 여러 주문 포함
- **Order → OrderItem**: 주문은 여러 주문 항목 포함

---

## Database Schema Notes

### Indexes
- `Admin.username` (UNIQUE)
- `Order.orderNumber` (UNIQUE)
- `Order.sessionId` (FK, 조회 최적화)
- `Order.status` (상태별 조회 최적화)
- `TableSession.tableId, isActive` (활성 세션 조회)

### Constraints
- FK constraints on all foreign keys
- CHECK constraint: `Order.totalAmount >= 0`
- CHECK constraint: `OrderItem.quantity > 0`
- CHECK constraint: `OrderItem.subtotal >= 0`
