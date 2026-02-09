# Code Generation Plan - Unit 2 (Backend Customer API)

## Unit Overview
- **Unit Name**: Unit 2 - Backend Customer API
- **Purpose**: 고객용 REST API 제공
- **Technology**: Java 17, Spring Boot 3.x, MyBatis, PostgreSQL, Redis
- **Code Location**: `backend/` (workspace root)
- **Documentation Location**: `aidlc-docs/construction/unit2-customer-api/code/`

## Unit Context

### Responsibilities
- 메뉴 조회 API
- 주문 생성 API (세션 시작 포함)
- 주문 내역 조회 API (현재 세션)
- 주문 취소 API
- 테이블 로그인 API
- 카테고리 조회 API

### API Endpoints
- `POST /api/customer/login` - 테이블 로그인
- `GET /api/customer/menus` - 메뉴 조회
- `GET /api/customer/categories` - 카테고리 조회
- `POST /api/customer/orders` - 주문 생성
- `GET /api/customer/orders` - 주문 내역 조회
- `DELETE /api/customer/orders/{orderId}` - 주문 취소

### Dependencies
- PostgreSQL Database
- Redis (Session & Idempotency Cache)
- Shared Domain entities
- Shared Security components

---

## Code Generation Steps

### Step 1: Project Structure Setup
- [x] Create backend/ directory structure
- [x] Create Gradle build configuration (build.gradle)
- [x] Create application.yml configuration files
- [x] Create .gitignore for backend
- [x] Create Dockerfile for backend

**Paths**:
- `backend/build.gradle`
- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/application-dev.yml`
- `backend/src/main/resources/application-prod.yml`
- `backend/.gitignore`
- `backend/Dockerfile`

---

### Step 2: Domain Layer Generation
- [x] Create domain package structure
- [x] Generate Store entity
- [x] Generate Table entity
- [x] Generate TableSession entity
- [x] Generate Menu entity
- [x] Generate Category entity
- [x] Generate Order entity
- [x] Generate OrderItem entity
- [x] Generate OrderStatus enum
- [x] Generate Admin entity

**Paths**:
- `backend/src/main/java/com/tableorder/domain/Store.java`
- `backend/src/main/java/com/tableorder/domain/Table.java`
- `backend/src/main/java/com/tableorder/domain/TableSession.java`
- `backend/src/main/java/com/tableorder/domain/Menu.java`
- `backend/src/main/java/com/tableorder/domain/Category.java`
- `backend/src/main/java/com/tableorder/domain/Order.java`
- `backend/src/main/java/com/tableorder/domain/OrderItem.java`
- `backend/src/main/java/com/tableorder/domain/OrderStatus.java`
- `backend/src/main/java/com/tableorder/domain/Admin.java`

---

### Step 3: DTO Layer Generation
- [x] Create dto package structure
- [x] Generate TableLoginRequestDto
- [x] Generate TableLoginResponseDto
- [x] Generate MenuResponseDto
- [x] Generate CategoryResponseDto
- [x] Generate OrderRequestDto
- [x] Generate OrderItemRequestDto
- [x] Generate OrderResponseDto
- [x] Generate OrderItemResponseDto
- [x] Generate ErrorResponseDto

**Paths**:
- `backend/src/main/java/com/tableorder/customer/dto/TableLoginRequestDto.java`
- `backend/src/main/java/com/tableorder/customer/dto/TableLoginResponseDto.java`
- `backend/src/main/java/com/tableorder/customer/dto/MenuResponseDto.java`
- `backend/src/main/java/com/tableorder/customer/dto/CategoryResponseDto.java`
- `backend/src/main/java/com/tableorder/customer/dto/OrderRequestDto.java`
- `backend/src/main/java/com/tableorder/customer/dto/OrderItemRequestDto.java`
- `backend/src/main/java/com/tableorder/customer/dto/OrderResponseDto.java`
- `backend/src/main/java/com/tableorder/customer/dto/OrderItemResponseDto.java`
- `backend/src/main/java/com/tableorder/exception/ErrorResponseDto.java`

---

### Step 4: Exception Layer Generation
- [x] Create exception package structure
- [x] Generate ErrorCode enum
- [x] Generate BusinessException
- [x] Generate UnauthorizedException
- [x] Generate MenuNotFoundException
- [x] Generate MenuNotAvailableException
- [x] Generate PriceChangedException
- [x] Generate TotalAmountMismatchException
- [x] Generate InvalidOrderStatusException
- [x] Generate OrderNotFoundException
- [x] Generate GlobalExceptionHandler

**Paths**:
- `backend/src/main/java/com/tableorder/exception/ErrorCode.java`
- `backend/src/main/java/com/tableorder/exception/BusinessException.java`
- `backend/src/main/java/com/tableorder/exception/UnauthorizedException.java`
- `backend/src/main/java/com/tableorder/exception/MenuNotFoundException.java`
- `backend/src/main/java/com/tableorder/exception/MenuNotAvailableException.java`
- `backend/src/main/java/com/tableorder/exception/PriceChangedException.java`
- `backend/src/main/java/com/tableorder/exception/TotalAmountMismatchException.java`
- `backend/src/main/java/com/tableorder/exception/InvalidOrderStatusException.java`
- `backend/src/main/java/com/tableorder/exception/OrderNotFoundException.java`
- `backend/src/main/java/com/tableorder/exception/GlobalExceptionHandler.java`

---

### Step 5: MyBatis Mapper Interface Generation
- [x] Create mapper package structure
- [x] Generate TableMapper interface
- [x] Generate MenuMapper interface
- [x] Generate CategoryMapper interface
- [x] Generate OrderMapper interface
- [x] Generate OrderItemMapper interface
- [x] Generate TableSessionMapper interface

**Paths**:
- `backend/src/main/java/com/tableorder/customer/mapper/TableMapper.java`
- `backend/src/main/java/com/tableorder/customer/mapper/MenuMapper.java`
- `backend/src/main/java/com/tableorder/customer/mapper/CategoryMapper.java`
- `backend/src/main/java/com/tableorder/customer/mapper/OrderMapper.java`
- `backend/src/main/java/com/tableorder/customer/mapper/OrderItemMapper.java`
- `backend/src/main/java/com/tableorder/customer/mapper/TableSessionMapper.java`

---

### Step 6: MyBatis Mapper XML Generation
- [x] Create mapper XML directory
- [x] Generate TableMapper.xml
- [x] Generate MenuMapper.xml
- [x] Generate CategoryMapper.xml
- [x] Generate OrderMapper.xml
- [x] Generate OrderItemMapper.xml
- [x] Generate TableSessionMapper.xml

**Paths**:
- `backend/src/main/resources/mapper/customer/TableMapper.xml`
- `backend/src/main/resources/mapper/customer/MenuMapper.xml`
- `backend/src/main/resources/mapper/customer/CategoryMapper.xml`
- `backend/src/main/resources/mapper/customer/OrderMapper.xml`
- `backend/src/main/resources/mapper/customer/OrderItemMapper.xml`
- `backend/src/main/resources/mapper/customer/TableSessionMapper.xml`

---

### Step 7: Service Layer Generation
- [x] Create service package structure
- [x] Generate AuthService
- [x] Generate MenuService
- [x] Generate OrderService
- [x] Generate SessionService

**Paths**:
- `backend/src/main/java/com/tableorder/customer/service/AuthService.java`
- `backend/src/main/java/com/tableorder/customer/service/MenuService.java`
- `backend/src/main/java/com/tableorder/customer/service/OrderService.java`
- `backend/src/main/java/com/tableorder/customer/service/SessionService.java`

---

### Step 8: Controller Layer Generation
- [x] Create controller package structure
- [x] Generate AuthController
- [x] Generate MenuController
- [x] Generate OrderController

**Paths**:
- `backend/src/main/java/com/tableorder/customer/controller/AuthController.java`
- `backend/src/main/java/com/tableorder/customer/controller/MenuController.java`
- `backend/src/main/java/com/tableorder/customer/controller/OrderController.java`

---

### Step 9: Security Configuration Generation
- [x] Create security package structure
- [x] Generate SecurityConfig
- [x] Generate SessionAuthenticationFilter
- [x] Generate PasswordEncoderConfig

**Paths**:
- `backend/src/main/java/com/tableorder/security/SecurityConfig.java`
- `backend/src/main/java/com/tableorder/security/SessionAuthenticationFilter.java`
- `backend/src/main/java/com/tableorder/security/PasswordEncoderConfig.java`

---

### Step 10: Redis Configuration Generation
- [x] Create config package structure
- [x] Generate RedisConfig

**Paths**:
- `backend/src/main/java/com/tableorder/config/RedisConfig.java`

---

### Step 11: Logging Configuration Generation
- [x] Generate logback-spring.xml

**Paths**:
- `backend/src/main/resources/logback-spring.xml`

---

### Step 12: Main Application Class Generation
- [x] Generate TableOrderApplication (Spring Boot main class)

**Paths**:
- `backend/src/main/java/com/tableorder/TableOrderApplication.java`

---

### Step 13: Database Migration Scripts Generation
- [x] Generate init.sql (schema + sample data)

**Paths**:
- `backend/src/main/resources/db/init.sql`

---

### Step 14: Unit Test Generation
- [x] Create test directory structure
- [x] Generate AuthServiceTest
- [x] Generate MenuServiceTest
- [x] Generate OrderServiceTest
- [x] Generate SessionServiceTest
- [x] Generate AuthControllerTest
- [x] Generate MenuControllerTest
- [x] Generate OrderControllerTest

**Paths**:
- `backend/src/test/java/com/tableorder/customer/service/AuthServiceTest.java`
- `backend/src/test/java/com/tableorder/customer/service/MenuServiceTest.java`
- `backend/src/test/java/com/tableorder/customer/service/OrderServiceTest.java`
- `backend/src/test/java/com/tableorder/customer/service/SessionServiceTest.java`
- `backend/src/test/java/com/tableorder/customer/controller/AuthControllerTest.java`
- `backend/src/test/java/com/tableorder/customer/controller/MenuControllerTest.java`
- `backend/src/test/java/com/tableorder/customer/controller/OrderControllerTest.java`

---

### Step 15: Docker Compose Configuration Generation
- [x] Generate docker-compose.yml (PostgreSQL, Redis, Backend)
- [x] Generate .env.example

**Paths**:
- `docker-compose.yml` (workspace root)
- `.env.example` (workspace root)

---

### Step 16: Documentation Generation
- [x] Generate API documentation summary
- [x] Generate code structure summary
- [x] Generate deployment guide

**Paths**:
- `aidlc-docs/construction/unit2-customer-api/code/api-documentation.md`
- `aidlc-docs/construction/unit2-customer-api/code/code-structure.md`
- `aidlc-docs/construction/unit2-customer-api/code/deployment-guide.md`

---

### Step 17: README Generation
- [x] Generate backend/README.md

**Paths**:
- `backend/README.md`

---

## Completion Criteria
- [x] All 17 steps completed
- [x] All code files generated in backend/
- [x] All documentation files generated in aidlc-docs/
- [x] Unit tests generated
- [x] Docker Compose configuration ready
- [x] README and deployment guide complete

---

## Notes
- **Application code location**: `backend/` (workspace root)
- **Documentation location**: `aidlc-docs/construction/unit2-customer-api/code/`
- **Standard code generation approach**: Code first, then tests
- **Total files to generate**: ~60 files
- **Estimated scope**: Medium complexity

