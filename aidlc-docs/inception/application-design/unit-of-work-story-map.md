# Unit of Work - Requirements Mapping - 테이블오더 서비스

## Overview

이 문서는 요구사항을 Unit of Work에 매핑합니다.

**Total Units**: 3
- Unit 1: Frontend (Customer + Admin UI)
- Unit 2: Backend Customer API
- Unit 3: Backend Admin API

---

## Functional Requirements Mapping

### FR-1: 고객용 기능 (Customer Features)

#### FR-1.1: 테이블 태블릿 자동 로그인 및 세션 관리
- **Unit 1 (Frontend)**: TableLoginPage, useAuth hook, SessionStorage 관리
- **Unit 2 (Customer API)**: POST /api/customer/login, CustomerService.login(), TableSessionService.startSession()

#### FR-1.2: 메뉴 조회 및 탐색
- **Unit 1 (Frontend)**: MenuPage, MenuCard, useMenu hook
- **Unit 2 (Customer API)**: GET /api/customer/menus, GET /api/customer/categories, MenuService.getMenus()

#### FR-1.3: 장바구니 관리
- **Unit 1 (Frontend)**: CartPage, CartItem, useCart hook, LocalStorage 관리
- **Unit 2 (Customer API)**: N/A (Frontend only)

#### FR-1.4: 주문 생성
- **Unit 1 (Frontend)**: CartPage.handleCheckout(), useOrder.createOrder()
- **Unit 2 (Customer API)**: POST /api/customer/orders, CustomerService.createOrder(), OrderService.createOrder(), OrderService.publishNewOrderEvent()

#### FR-1.5: 주문 내역 조회
- **Unit 1 (Frontend)**: OrderHistoryPage, OrderCard, useOrderHistory hook
- **Unit 2 (Customer API)**: GET /api/customer/orders, CustomerService.getOrders()

#### FR-1.6: 주문 취소
- **Unit 1 (Frontend)**: OrderCard.handleCancel(), useOrder.cancelOrder()
- **Unit 2 (Customer API)**: DELETE /api/customer/orders/{orderId}, CustomerService.cancelOrder(), OrderService.canCancelOrder()

---

### FR-2: 관리자용 기능 (Admin Features)

#### FR-2.1: 매장 인증
- **Unit 1 (Frontend)**: AdminLoginPage, useAuth hook, SessionStorage (JWT 저장)
- **Unit 3 (Admin API)**: POST /api/admin/login, AdminService.login(), JwtTokenProvider.generateToken()

#### FR-2.2: 실시간 주문 모니터링
- **Unit 1 (Frontend)**: AdminDashboardPage, TableCard, OrderDetailModal, useOrderStream hook (SSE)
- **Unit 3 (Admin API)**: GET /api/admin/orders/stream (SSE), SseEmitterManager, OrderService.publishNewOrderEvent(), OrderService.publishOrderStatusChangeEvent()

#### FR-2.3: 테이블 관리
- **Unit 1 (Frontend)**: TableManagementPage, PastOrdersModal, useTable hook
- **Unit 3 (Admin API)**: 
  - POST /api/admin/tables/{tableId}/end-session (세션 종료)
  - DELETE /api/admin/orders/{orderId} (주문 삭제)
  - GET /api/admin/tables/{tableId}/past-orders (과거 내역)
  - AdminService, TableSessionService

#### FR-2.4: 메뉴 관리 (MVP - 조회만)
- **Unit 1 (Frontend)**: MenuManagementPage, useMenu hook
- **Unit 2 (Customer API)**: GET /api/customer/menus, MenuService.getMenus() (Customer API 재사용)

---

## Non-Functional Requirements Mapping

### NFR-1: Performance
- **Unit 1 (Frontend)**: 
  - 로딩 인디케이터 (LoadingSpinner)
  - 최적화된 렌더링 (React.memo, useMemo)
- **Unit 2 (Customer API)**: 
  - 3초 이내 응답 시간
  - Connection pooling (HikariCP)
  - 인덱스 최적화 (PostgreSQL)
- **Unit 3 (Admin API)**: 
  - SSE 연결 관리 (타임아웃, 재연결)
  - 3초 이내 주문 표시

### NFR-2: Security
- **Unit 1 (Frontend)**: 
  - JWT 토큰 SessionStorage 저장
  - Axios interceptor (토큰 자동 첨부)
  - Input validation
- **Unit 2 (Customer API)**: 
  - JWT 검증 필터 (JwtAuthenticationFilter)
  - 입력 검증 (@Valid)
- **Unit 3 (Admin API)**: 
  - JWT 검증 필터 (JwtAuthenticationFilter)
  - 비밀번호 bcrypt 해싱 (PasswordEncoder)
  - 입력 검증 (@Valid)

### NFR-3: Scalability
- **Unit 1 (Frontend)**: 
  - 반응형 디자인 (모든 디바이스)
  - Code splitting (React.lazy)
- **Unit 2 (Customer API)**: 
  - Stateless API (수평 확장 가능)
  - Docker 컨테이너화
- **Unit 3 (Admin API)**: 
  - Stateless API (수평 확장 가능)
  - Docker 컨테이너화
  - SSE 연결 관리

### NFR-4: Usability
- **Unit 1 (Frontend)**: 
  - 직관적 UI/UX
  - 터치 친화적 (44x44px 버튼)
  - 명확한 에러 메시지 (ErrorBoundary)
  - 다국어 지원 (한국어 + 영어)
- **Unit 2 (Customer API)**: 
  - 표준화된 에러 응답 (ErrorResponseDto)
- **Unit 3 (Admin API)**: 
  - 표준화된 에러 응답 (ErrorResponseDto)

### NFR-5: Reliability
- **Unit 1 (Frontend)**: 
  - Error Boundary (에러 캐치)
  - 로컬 상태 복구 (LocalStorage, SessionStorage)
- **Unit 2 (Customer API)**: 
  - 트랜잭션 관리 (@Transactional)
  - GlobalExceptionHandler
- **Unit 3 (Admin API)**: 
  - 트랜잭션 관리 (@Transactional)
  - GlobalExceptionHandler
  - SSE 재연결 로직

### NFR-6: Maintainability
- **Unit 1 (Frontend)**: 
  - TypeScript (타입 안정성)
  - Component 분리 (재사용성)
  - Custom hooks (로직 분리)
  - Unit 테스트 (Jest + React Testing Library)
- **Unit 2 (Customer API)**: 
  - Layered Architecture (관심사 분리)
  - DTO 패턴 (Entity와 분리)
  - TDD (Unit 테스트)
- **Unit 3 (Admin API)**: 
  - Layered Architecture (관심사 분리)
  - DTO 패턴 (Entity와 분리)
  - TDD (Unit 테스트)

### NFR-7: Deployment
- **Unit 1 (Frontend)**: 
  - Docker 컨테이너
  - Nginx (정적 파일 서빙)
- **Unit 2 (Customer API)**: 
  - Docker 컨테이너
  - Spring Boot embedded Tomcat
- **Unit 3 (Admin API)**: 
  - Docker 컨테이너
  - Spring Boot embedded Tomcat
- **All Units**: 
  - Docker Compose (로컬 배포)

---

## Technical Stack Mapping

### Unit 1: Frontend
- **Framework**: React 18+
- **Language**: TypeScript
- **State Management**: Zustand
- **HTTP Client**: Axios
- **Routing**: React Router
- **Styling**: CSS Modules or Tailwind CSS
- **Testing**: Jest + React Testing Library

### Unit 2: Backend Customer API
- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **ORM**: MyBatis
- **Database**: PostgreSQL
- **Security**: Spring Security + JWT
- **Testing**: JUnit 5 + Mockito

### Unit 3: Backend Admin API
- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **ORM**: MyBatis
- **Database**: PostgreSQL
- **Security**: Spring Security + JWT
- **Real-time**: SSE (Server-Sent Events)
- **Testing**: JUnit 5 + Mockito

---

## Data Model Mapping

### Unit 1: Frontend (TypeScript Interfaces)
```typescript
interface Menu {
  menuId: number;
  menuName: string;
  price: number;
  description: string;
  imageUrl: string;
  categoryId: number;
}

interface Order {
  orderId: number;
  orderNumber: string;
  tableId: number;
  sessionId: string;
  totalAmount: number;
  status: 'PENDING' | 'PREPARING' | 'COMPLETED';
  orderTime: string;
  items: OrderItem[];
}

interface CartItem {
  menu: Menu;
  quantity: number;
}
```

### Unit 2 & 3: Backend (Domain Entities)
```java
// Shared Domain Entities
@Entity
public class Menu {
    private Long menuId;
    private String menuName;
    private Integer price;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private Long storeId;
}

@Entity
public class Order {
    private Long orderId;
    private String orderNumber;
    private Long tableId;
    private String sessionId;
    private Integer totalAmount;
    private String status;
    private LocalDateTime orderTime;
}
```

---

## API Endpoint Mapping

### Unit 1 → Unit 2 (Customer API)

| Frontend Component | API Endpoint | Backend Handler |
|-------------------|--------------|-----------------|
| MenuPage | GET /api/customer/menus | CustomerController.getMenus() |
| MenuPage | GET /api/customer/categories | CustomerController.getCategories() |
| CartPage | POST /api/customer/orders | CustomerController.createOrder() |
| OrderHistoryPage | GET /api/customer/orders | CustomerController.getOrders() |
| OrderCard | DELETE /api/customer/orders/{orderId} | CustomerController.cancelOrder() |
| TableLoginPage | POST /api/customer/login | CustomerController.login() |

### Unit 1 → Unit 3 (Admin API)

| Frontend Component | API Endpoint | Backend Handler |
|-------------------|--------------|-----------------|
| AdminLoginPage | POST /api/admin/login | AdminController.login() |
| AdminDashboardPage | GET /api/admin/orders | AdminController.getOrders() |
| AdminDashboardPage | GET /api/admin/orders/stream (SSE) | AdminController.streamOrders() |
| OrderDetailModal | PATCH /api/admin/orders/{orderId}/status | AdminController.updateOrderStatus() |
| OrderDetailModal | DELETE /api/admin/orders/{orderId} | AdminController.deleteOrder() |
| TableManagementPage | POST /api/admin/tables/{tableId}/end-session | AdminController.endTableSession() |
| PastOrdersModal | GET /api/admin/tables/{tableId}/past-orders | AdminController.getPastOrders() |

---

## User Scenarios Mapping

### Scenario 1: 첫 방문 고객 (Customer Journey)

| Step | Unit 1 (Frontend) | Unit 2 (Customer API) | Unit 3 (Admin API) |
|------|-------------------|----------------------|-------------------|
| 1. 테이블 초기 설정 | TableLoginPage | POST /api/customer/login | - |
| 2. 메뉴 탐색 | MenuPage, MenuCard | GET /api/customer/menus | - |
| 3. 장바구니 추가 | CartPage, useCart | - | - |
| 4. 주문 확정 | CartPage.handleCheckout() | POST /api/customer/orders | SSE event (new-order) |
| 5. 주문 번호 확인 | Order confirmation | - | - |
| 6. 메뉴 화면 이동 | MenuPage | - | - |

### Scenario 2: 관리자 주문 처리 (Admin Journey)

| Step | Unit 1 (Frontend) | Unit 2 (Customer API) | Unit 3 (Admin API) |
|------|-------------------|----------------------|-------------------|
| 1. 관리자 로그인 | AdminLoginPage | - | POST /api/admin/login |
| 2. 대시보드 확인 | AdminDashboardPage | - | GET /api/admin/orders/stream (SSE) |
| 3. 신규 주문 알림 | TableCard (시각적 강조) | - | SSE event received |
| 4. 주문 상세 확인 | OrderDetailModal | - | - |
| 5. 주문 상태 변경 | OrderDetailModal | - | PATCH /api/admin/orders/{orderId}/status |
| 6. 테이블 정리 | TableManagementPage | - | POST /api/admin/tables/{tableId}/end-session |

---

## Testing Mapping

### Unit 1: Frontend Tests

| Test Type | Components | Tools |
|-----------|------------|-------|
| Component Tests | All pages, components | Jest + React Testing Library |
| Hook Tests | useMenu, useCart, useOrder, etc. | @testing-library/react-hooks |
| Integration Tests | API calls (mocked) | MSW or Axios Mock Adapter |

### Unit 2: Backend Customer API Tests

| Test Type | Components | Tools |
|-----------|------------|-------|
| Unit Tests | Services, Mappers | JUnit 5 + Mockito |
| Controller Tests | CustomerController | MockMvc |
| Mapper Tests | CustomerMapper | MyBatis Test |

### Unit 3: Backend Admin API Tests

| Test Type | Components | Tools |
|-----------|------------|-------|
| Unit Tests | Services, Mappers | JUnit 5 + Mockito |
| Controller Tests | AdminController | MockMvc |
| Mapper Tests | AdminMapper | MyBatis Test |
| SSE Tests | SseEmitterManager | Custom SSE test |

---

## Summary

### Requirements Coverage

| Requirement Category | Unit 1 | Unit 2 | Unit 3 |
|---------------------|--------|--------|--------|
| Customer Features (FR-1) | ✓ | ✓ | - |
| Admin Features (FR-2) | ✓ | - | ✓ |
| Performance (NFR-1) | ✓ | ✓ | ✓ |
| Security (NFR-2) | ✓ | ✓ | ✓ |
| Scalability (NFR-3) | ✓ | ✓ | ✓ |
| Usability (NFR-4) | ✓ | ✓ | ✓ |
| Reliability (NFR-5) | ✓ | ✓ | ✓ |
| Maintainability (NFR-6) | ✓ | ✓ | ✓ |
| Deployment (NFR-7) | ✓ | ✓ | ✓ |

**All requirements are covered across the 3 units.**

---

## Notes

- Unit 1 (Frontend)는 모든 UI 요구사항 담당
- Unit 2 (Customer API)는 고객용 기능 요구사항 담당
- Unit 3 (Admin API)는 관리자용 기능 요구사항 담당
- NFR은 모든 Unit에 분산되어 구현
- 각 Unit은 독립적으로 테스트 가능
