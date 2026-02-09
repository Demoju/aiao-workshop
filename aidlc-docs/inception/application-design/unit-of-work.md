# Unit of Work - 테이블오더 서비스

## Overview

테이블오더 서비스는 **3개의 Unit of Work**로 분해됩니다:
- **Unit 1**: Frontend (Customer + Admin UI)
- **Unit 2**: Backend Customer API
- **Unit 3**: Backend Admin API

### Deployment Model
- **Frontend와 Backend 별도 배포**
- Frontend: 단일 애플리케이션 (Customer + Admin 통합)
- Backend: Domain-based 분리 (Customer API, Admin API)

### Development Strategy
- **병렬 개발**: Frontend와 Backend 동시 개발
- **Mock 사용**: Backend API Mock을 사용하여 Frontend 독립 개발
- **Contract-first approach**: API 계약 먼저 정의

---

## Code Organization Strategy (Greenfield)

### Monorepo Structure
```
aiao-workshop/
├── frontend/                    # Unit 1: Frontend
│   ├── src/
│   │   ├── customer/           # Customer domain
│   │   │   ├── pages/
│   │   │   └── components/
│   │   ├── admin/              # Admin domain
│   │   │   ├── pages/
│   │   │   └── components/
│   │   ├── shared/             # Shared components
│   │   ├── hooks/              # Custom hooks
│   │   ├── services/           # API services
│   │   ├── store/              # Zustand store
│   │   └── types/              # TypeScript types
│   ├── public/
│   ├── package.json
│   └── tsconfig.json
│
├── backend/                     # Backend (Units 2 & 3)
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/tableorder/
│   │       │       ├── customer/        # Unit 2: Customer API
│   │       │       │   ├── controller/
│   │       │       │   ├── service/
│   │       │       │   ├── mapper/
│   │       │       │   └── dto/
│   │       │       ├── admin/           # Unit 3: Admin API
│   │       │       │   ├── controller/
│   │       │       │   ├── service/
│   │       │       │   ├── mapper/
│   │       │       │   └── dto/
│   │       │       ├── domain/          # Shared domain entities
│   │       │       ├── security/        # JWT, filters
│   │       │       ├── exception/       # Global exception handler
│   │       │       └── config/          # Configuration
│   │       └── resources/
│   │           ├── mappers/
│   │           │   ├── customer/        # Customer MyBatis XML
│   │           │   └── admin/           # Admin MyBatis XML
│   │           └── application.yml
│   ├── pom.xml (or build.gradle)
│   └── Dockerfile
│
├── docker-compose.yml           # PostgreSQL, Backend, Frontend
├── README.md
└── aidlc-docs/                  # AI-DLC documentation
```

### Application Code Location Rules
- **Application code**: Workspace root (frontend/, backend/)
- **Documentation**: aidlc-docs/ only
- **Never mix**: Application code never goes in aidlc-docs/

---

## Unit 1: Frontend (Customer + Admin UI)

### Purpose
고객용 및 관리자용 웹 UI 제공

### Responsibilities
- **Customer Domain**:
  - 메뉴 조회 및 탐색
  - 장바구니 관리
  - 주문 생성 및 내역 조회
  - 주문 취소
  - 테이블 로그인
- **Admin Domain**:
  - 관리자 로그인
  - 실시간 주문 모니터링 (SSE)
  - 테이블 관리
  - 메뉴 조회
- **Shared**:
  - 공통 컴포넌트 (Header, ErrorBoundary, LoadingSpinner, ConfirmDialog)
  - Custom hooks (useMenu, useCart, useOrder, useOrderHistory, useOrderStream, useAuth, useTable)
  - API 서비스 레이어
  - Zustand 상태 관리

### Components Included
- **Pages**: 8개 (MenuPage, CartPage, OrderHistoryPage, TableLoginPage, AdminLoginPage, AdminDashboardPage, TableManagementPage, MenuManagementPage)
- **Components**: 10개 (MenuCard, CartItem, OrderCard, TableCard, OrderDetailModal, PastOrdersModal, Header, ErrorBoundary, LoadingSpinner, ConfirmDialog)
- **Custom Hooks**: 7개 (useMenu, useCart, useOrder, useOrderHistory, useOrderStream, useAuth, useTable)

### Technology Stack
- React 18+
- TypeScript
- Zustand (상태 관리)
- Axios (HTTP 클라이언트)
- React Router (라우팅)
- CSS Modules or Tailwind CSS (스타일링)

### Dependencies
- Backend Customer API (Unit 2) - REST API
- Backend Admin API (Unit 3) - REST API + SSE

### Development Priority
**Priority 1** (병렬 개발 가능 - Mock 사용)

### Testing Strategy
- Unit 테스트: Jest + React Testing Library
- Component 테스트
- Custom hook 테스트

---

## Unit 2: Backend Customer API

### Purpose
고객용 REST API 제공

### Responsibilities
- 메뉴 조회 API
- 주문 생성 API (세션 시작 포함)
- 주문 내역 조회 API (현재 세션)
- 주문 취소 API
- 테이블 로그인 API
- 카테고리 조회 API

### Components Included
- **Controller**: CustomerController
- **Service**: CustomerService, OrderService (일부), MenuService, TableSessionService
- **Mapper**: CustomerMapper
- **DTO**: OrderRequestDto, OrderResponseDto, MenuResponseDto, CategoryResponseDto, TableLoginRequestDto, TableLoginResponseDto
- **Domain**: Store, Table, Menu, Category, Order, OrderItem, TableSession

### API Endpoints
- `GET /api/customer/menus` - 메뉴 조회
- `GET /api/customer/categories` - 카테고리 조회
- `POST /api/customer/orders` - 주문 생성
- `GET /api/customer/orders` - 주문 내역 조회
- `DELETE /api/customer/orders/{orderId}` - 주문 취소
- `POST /api/customer/login` - 테이블 로그인

### Technology Stack
- Java 17+
- Spring Boot 3.x
- MyBatis
- PostgreSQL
- Lombok
- Spring Security (JWT)

### Dependencies
- PostgreSQL Database
- Shared Domain entities
- Shared Security components (JWT)

### Development Priority
**Priority 1** (병렬 개발 가능)

### Testing Strategy
- Unit 테스트: JUnit 5 + Mockito
- Service 레이어 테스트
- Controller 레이어 테스트 (MockMvc)
- Mapper 테스트 (MyBatis Test)

---

## Unit 3: Backend Admin API

### Purpose
관리자용 REST API 및 SSE 제공

### Responsibilities
- 관리자 로그인 API (JWT 발급)
- 주문 목록 조회 API
- 주문 상태 변경 API
- 주문 삭제 API
- 테이블 세션 종료 API
- 과거 주문 내역 조회 API
- 실시간 주문 업데이트 SSE 엔드포인트

### Components Included
- **Controller**: AdminController
- **Service**: AdminService, OrderService (일부), TableSessionService
- **Mapper**: AdminMapper
- **DTO**: AdminLoginRequestDto, AdminLoginResponseDto, OrderStatusUpdateDto, ErrorResponseDto
- **Domain**: Admin
- **SSE**: SseEmitterManager

### API Endpoints
- `POST /api/admin/login` - 관리자 로그인
- `GET /api/admin/orders` - 주문 목록 조회
- `PATCH /api/admin/orders/{orderId}/status` - 주문 상태 변경
- `DELETE /api/admin/orders/{orderId}` - 주문 삭제
- `POST /api/admin/tables/{tableId}/end-session` - 테이블 세션 종료
- `GET /api/admin/tables/{tableId}/past-orders` - 과거 주문 내역 조회
- `GET /api/admin/orders/stream` - SSE 실시간 주문 업데이트

### Technology Stack
- Java 17+
- Spring Boot 3.x
- MyBatis
- PostgreSQL
- Lombok
- Spring Security (JWT)
- SSE (Server-Sent Events)

### Dependencies
- PostgreSQL Database
- Shared Domain entities
- Shared Security components (JWT, PasswordEncoder)

### Development Priority
**Priority 1** (병렬 개발 가능)

### Testing Strategy
- Unit 테스트: JUnit 5 + Mockito
- Service 레이어 테스트
- Controller 레이어 테스트 (MockMvc)
- Mapper 테스트 (MyBatis Test)
- SSE 테스트

---

## Shared Components (Cross-Unit)

### Domain Layer
- **Entities**: Store, Table, Menu, Category, Order, OrderItem, TableSession, Admin
- **Location**: `backend/src/main/java/com/tableorder/domain/`
- **Used by**: Unit 2, Unit 3

### Security Layer
- **Components**: JwtAuthenticationFilter, JwtTokenProvider, PasswordEncoder
- **Location**: `backend/src/main/java/com/tableorder/security/`
- **Used by**: Unit 2, Unit 3

### Exception Handling
- **Components**: GlobalExceptionHandler, Custom Exceptions
- **Location**: `backend/src/main/java/com/tableorder/exception/`
- **Used by**: Unit 2, Unit 3

### Configuration
- **Components**: SecurityConfig, MyBatisConfig, WebConfig
- **Location**: `backend/src/main/java/com/tableorder/config/`
- **Used by**: Unit 2, Unit 3

---

## Development Sequence

### Phase 1: Foundation (Parallel)
- **Unit 2**: Backend Customer API 개발
- **Unit 3**: Backend Admin API 개발
- **Unit 1**: Frontend 개발 (Backend Mock 사용)

### Phase 2: Integration
- Frontend와 Backend 통합
- Mock 제거 및 실제 API 연결
- 통합 테스트

### Phase 3: Build and Test
- 전체 시스템 빌드
- Unit 테스트 실행
- Docker Compose로 로컬 배포 테스트

---

## Unit Summary

| Unit | Type | Components | Priority | Dependencies |
|------|------|------------|----------|--------------|
| **Unit 1** | Frontend | 8 pages + 10 components + 7 hooks | 1 | Unit 2, Unit 3 (Mock) |
| **Unit 2** | Backend API | 1 controller + 4 services + 1 mapper | 1 | PostgreSQL, Shared |
| **Unit 3** | Backend API | 1 controller + 3 services + 1 mapper + SSE | 1 | PostgreSQL, Shared |

**Total Units**: 3
**Development Approach**: Parallel with mocking
**Integration Points**: REST API + SSE

---

## Notes

- Frontend는 단일 Unit으로 Customer와 Admin UI 통합
- Backend는 Domain-based로 Customer API와 Admin API 분리
- 병렬 개발을 위해 Frontend는 Backend Mock 사용
- Shared components (Domain, Security, Exception)는 Backend 내부에서 공유
- Monorepo 구조로 frontend/, backend/ 디렉토리 분리
