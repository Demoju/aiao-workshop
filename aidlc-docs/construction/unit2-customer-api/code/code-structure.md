# Code Structure - Unit 2 (Backend Customer API)

## Overview

이 문서는 Backend Customer API의 코드 구조와 패키지 구성을 설명합니다.

---

## Directory Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── tableorder/
│   │   │           ├── TableOrderApplication.java
│   │   │           ├── config/
│   │   │           │   └── RedisConfig.java
│   │   │           ├── customer/
│   │   │           │   ├── controller/
│   │   │           │   │   ├── AuthController.java
│   │   │           │   │   ├── MenuController.java
│   │   │           │   │   └── OrderController.java
│   │   │           │   ├── dto/
│   │   │           │   │   ├── CategoryResponseDto.java
│   │   │           │   │   ├── MenuResponseDto.java
│   │   │           │   │   ├── OrderItemRequestDto.java
│   │   │           │   │   ├── OrderItemResponseDto.java
│   │   │           │   │   ├── OrderRequestDto.java
│   │   │           │   │   ├── OrderResponseDto.java
│   │   │           │   │   ├── TableLoginRequestDto.java
│   │   │           │   │   └── TableLoginResponseDto.java
│   │   │           │   ├── mapper/
│   │   │           │   │   ├── CategoryMapper.java
│   │   │           │   │   ├── MenuMapper.java
│   │   │           │   │   ├── OrderItemMapper.java
│   │   │           │   │   ├── OrderMapper.java
│   │   │           │   │   ├── TableMapper.java
│   │   │           │   │   └── TableSessionMapper.java
│   │   │           │   └── service/
│   │   │           │       ├── AuthService.java
│   │   │           │       ├── MenuService.java
│   │   │           │       ├── OrderService.java
│   │   │           │       └── SessionService.java
│   │   │           ├── domain/
│   │   │           │   ├── Admin.java
│   │   │           │   ├── Category.java
│   │   │           │   ├── Menu.java
│   │   │           │   ├── Order.java
│   │   │           │   ├── OrderItem.java
│   │   │           │   ├── OrderStatus.java
│   │   │           │   ├── Store.java
│   │   │           │   ├── Table.java
│   │   │           │   └── TableSession.java
│   │   │           ├── exception/
│   │   │           │   ├── BusinessException.java
│   │   │           │   ├── ErrorCode.java
│   │   │           │   ├── ErrorResponseDto.java
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   ├── InvalidOrderStatusException.java
│   │   │           │   ├── MenuNotAvailableException.java
│   │   │           │   ├── MenuNotFoundException.java
│   │   │           │   ├── OrderNotFoundException.java
│   │   │           │   ├── PriceChangedException.java
│   │   │           │   ├── TotalAmountMismatchException.java
│   │   │           │   └── UnauthorizedException.java
│   │   │           └── security/
│   │   │               ├── PasswordEncoderConfig.java
│   │   │               ├── SecurityConfig.java
│   │   │               └── SessionAuthenticationFilter.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── db/
│   │       │   └── init.sql
│   │       ├── logback-spring.xml
│   │       └── mapper/
│   │           └── customer/
│   │               ├── CategoryMapper.xml
│   │               ├── MenuMapper.xml
│   │               ├── OrderItemMapper.xml
│   │               ├── OrderMapper.xml
│   │               ├── TableMapper.xml
│   │               └── TableSessionMapper.xml
│   └── test/
│       └── java/
│           └── com/
│               └── tableorder/
│                   └── customer/
│                       ├── controller/
│                       │   ├── AuthControllerTest.java
│                       │   ├── MenuControllerTest.java
│                       │   └── OrderControllerTest.java
│                       └── service/
│                           ├── AuthServiceTest.java
│                           ├── MenuServiceTest.java
│                           ├── OrderServiceTest.java
│                           └── SessionServiceTest.java
├── build.gradle
├── settings.gradle
├── Dockerfile
└── .gitignore
```

---

## Package Structure

### 1. Root Package: `com.tableorder`

#### `TableOrderApplication.java`
- Spring Boot 메인 애플리케이션 클래스
- `@SpringBootApplication` 어노테이션
- `@MapperScan` 어노테이션으로 MyBatis Mapper 스캔

---

### 2. Config Package: `com.tableorder.config`

#### `RedisConfig.java`
- Redis 연결 설정
- RedisTemplate 빈 생성
- Session 저장소 설정

---

### 3. Customer Package: `com.tableorder.customer`

고객용 API 관련 모든 컴포넌트를 포함합니다.

#### 3.1 Controller Package: `com.tableorder.customer.controller`

**AuthController.java**
- 테이블 로그인 API
- Endpoint: `POST /api/customer/login`

**MenuController.java**
- 메뉴 조회 API
- 카테고리 조회 API
- Endpoints:
  - `GET /api/customer/menus`
  - `GET /api/customer/categories`

**OrderController.java**
- 주문 생성 API
- 주문 내역 조회 API
- 주문 취소 API
- Endpoints:
  - `POST /api/customer/orders`
  - `GET /api/customer/orders`
  - `DELETE /api/customer/orders/{orderId}`

#### 3.2 DTO Package: `com.tableorder.customer.dto`

**Request DTOs**:
- `TableLoginRequestDto`: 로그인 요청
- `OrderRequestDto`: 주문 생성 요청
- `OrderItemRequestDto`: 주문 항목 요청

**Response DTOs**:
- `TableLoginResponseDto`: 로그인 응답
- `MenuResponseDto`: 메뉴 응답
- `CategoryResponseDto`: 카테고리 응답
- `OrderResponseDto`: 주문 응답
- `OrderItemResponseDto`: 주문 항목 응답

#### 3.3 Service Package: `com.tableorder.customer.service`

**AuthService.java**
- 테이블 로그인 비즈니스 로직
- 비밀번호 검증 (bcrypt)
- 세션 생성 또는 조회

**MenuService.java**
- 메뉴 조회 비즈니스 로직
- 카테고리 조회 비즈니스 로직
- 메뉴 검증 로직

**OrderService.java**
- 주문 생성 비즈니스 로직
- 주문 내역 조회 비즈니스 로직
- 주문 취소 비즈니스 로직
- 가격 검증, 총액 검증
- 주문 번호 생성

**SessionService.java**
- 세션 생성 또는 조회
- 세션 활성 상태 확인

#### 3.4 Mapper Package: `com.tableorder.customer.mapper`

MyBatis Mapper 인터페이스:
- `TableMapper.java`: 테이블 조회
- `MenuMapper.java`: 메뉴 조회
- `CategoryMapper.java`: 카테고리 조회
- `OrderMapper.java`: 주문 CRUD
- `OrderItemMapper.java`: 주문 항목 CRUD
- `TableSessionMapper.java`: 세션 CRUD

---

### 4. Domain Package: `com.tableorder.domain`

도메인 엔티티 클래스:
- `Store.java`: 매장
- `Table.java`: 테이블
- `TableSession.java`: 테이블 세션
- `Menu.java`: 메뉴
- `Category.java`: 카테고리
- `Order.java`: 주문
- `OrderItem.java`: 주문 항목
- `OrderStatus.java`: 주문 상태 Enum
- `Admin.java`: 관리자

---

### 5. Exception Package: `com.tableorder.exception`

**Exception Classes**:
- `BusinessException.java`: 비즈니스 예외 기본 클래스
- `UnauthorizedException.java`: 인증 실패
- `MenuNotFoundException.java`: 메뉴 없음
- `MenuNotAvailableException.java`: 메뉴 품절
- `PriceChangedException.java`: 가격 변경
- `TotalAmountMismatchException.java`: 총액 불일치
- `InvalidOrderStatusException.java`: 잘못된 주문 상태
- `OrderNotFoundException.java`: 주문 없음

**Error Handling**:
- `ErrorCode.java`: 에러 코드 Enum
- `ErrorResponseDto.java`: 에러 응답 DTO
- `GlobalExceptionHandler.java`: 전역 예외 처리 (`@ControllerAdvice`)

---

### 6. Security Package: `com.tableorder.security`

**SecurityConfig.java**
- Spring Security 설정
- 인증 필터 체인 구성
- CORS 설정

**SessionAuthenticationFilter.java**
- 세션 기반 인증 필터
- HTTP Header에서 Session ID 추출
- Redis에서 세션 검증

**PasswordEncoderConfig.java**
- BCryptPasswordEncoder 빈 생성

---

## Layer Architecture

```
┌─────────────────────────────────────────┐
│         Controller Layer                │
│  - REST API 엔드포인트                   │
│  - 요청/응답 처리                        │
│  - 입력 검증 (@Valid)                    │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│         Service Layer                   │
│  - 비즈니스 로직                         │
│  - Transaction 관리                     │
│  - 도메인 규칙 적용                      │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│         Repository Layer (MyBatis)      │
│  - 데이터 접근                           │
│  - SQL 쿼리 실행                         │
│  - 엔티티 매핑                           │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│         Database (PostgreSQL)           │
│  - 데이터 영속성                         │
└─────────────────────────────────────────┘
```

---

## Key Design Patterns

### 1. Layered Architecture
- Controller → Service → Repository → Database
- 각 레이어는 명확한 책임 분리
- 의존성은 단방향 (상위 → 하위)

### 2. DTO Pattern
- Entity와 DTO 분리
- API 응답에 Entity 직접 노출 안 함
- 버전 관리 용이

### 3. Exception Handling
- `@ControllerAdvice`로 전역 예외 처리
- 비즈니스 예외는 커스텀 Exception 사용
- 일관된 에러 응답 형식

### 4. Transaction Management
- `@Transactional` 어노테이션 사용
- Service 레이어에서 Transaction 경계 설정
- 주문 생성/취소는 원자성 보장

### 5. Dependency Injection
- Constructor Injection 사용 (`@RequiredArgsConstructor`)
- 테스트 용이성 향상
- 불변성 보장

---

## Configuration Files

### application.yml
- 공통 설정
- Database 연결 정보
- Redis 연결 정보
- MyBatis 설정
- Logging 설정

### application-dev.yml
- 개발 환경 설정
- 상세 로깅
- H2 Console 활성화 (선택)

### application-prod.yml
- 프로덕션 환경 설정
- 최소 로깅
- 보안 강화

### logback-spring.xml
- 로그 출력 형식
- 로그 레벨 설정
- 파일 로테이션 설정

---

## Testing Structure

### Unit Tests
- Service 레이어 테스트 (Mockito)
- Controller 레이어 테스트 (MockMvc)
- 각 클래스당 독립적인 테스트

### Test Naming Convention
- `{ClassName}Test.java`
- 테스트 메서드: `{methodName}_{scenario}_{expectedResult}`
- 예: `login_Success()`, `createOrder_MenuNotFound()`

---

## Build Configuration

### build.gradle
- Spring Boot 3.x
- Java 17
- Dependencies:
  - Spring Web
  - Spring Security
  - MyBatis
  - PostgreSQL Driver
  - Redis
  - Lombok
  - Validation
  - JUnit 5
  - Mockito
  - AssertJ

---

## Notes

- 모든 클래스는 단일 책임 원칙 (SRP) 준수
- 패키지 구조는 기능별 (Feature-based) 구성
- 테스트 커버리지 목표: 80% 이상
