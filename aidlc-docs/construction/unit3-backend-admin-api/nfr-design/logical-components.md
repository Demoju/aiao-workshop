# Logical Components - Unit 3 (Backend Admin API)

## Overview
NFR 요구사항을 지원하는 논리적 컴포넌트

---

## 1. Security Components

### 1.1 JwtTokenProvider
- **Purpose**: JWT 토큰 생성 및 검증
- **Responsibilities**:
  - JWT 토큰 생성 (16시간 만료)
  - JWT 토큰 검증
  - Claims 추출 (adminId, storeId)
- **Dependencies**: jjwt library
- **Methods**:
  ```java
  String generateToken(Admin admin)
  boolean validateToken(String token)
  Long getAdminIdFromToken(String token)
  Long getStoreIdFromToken(String token)
  ```

### 1.2 JwtAuthenticationFilter
- **Purpose**: HTTP 요청에서 JWT 검증
- **Responsibilities**:
  - Authorization 헤더에서 JWT 추출
  - JWT 검증
  - SecurityContext에 인증 정보 설정
- **Dependencies**: JwtTokenProvider, Spring Security
- **Flow**:
  ```
  Request → Extract JWT → Validate → Set Authentication → Continue
  ```

### 1.3 PasswordEncoder
- **Purpose**: 비밀번호 해싱 및 검증
- **Responsibilities**:
  - 비밀번호 bcrypt 해싱
  - 비밀번호 검증
- **Implementation**: BCryptPasswordEncoder (Spring Security)

---

## 2. SSE Components

### 2.1 SseEmitterManager
- **Purpose**: SSE 연결 관리 및 이벤트 브로드캐스트
- **Responsibilities**:
  - SseEmitter 생성 및 저장
  - 이벤트 브로드캐스트
  - 연결 정리 (타임아웃, 에러)
- **Data Structure**:
  ```java
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
  ```
- **Methods**:
  ```java
  SseEmitter createEmitter(String adminId)
  void broadcast(OrderEvent event)
  void removeEmitter(String adminId)
  ```

### 2.2 OrderEventPublisher
- **Purpose**: 주문 이벤트 발행
- **Responsibilities**:
  - 새 주문 생성 시 이벤트 발행
  - SseEmitterManager에 이벤트 전달
- **Dependencies**: SseEmitterManager
- **Methods**:
  ```java
  void publishNewOrderEvent(Order order)
  ```

---

## 3. Connection Pool Component

### 3.1 HikariCP Configuration
- **Purpose**: 데이터베이스 연결 풀 관리
- **Configuration**:
  ```yaml
  spring:
    datasource:
      hikari:
        minimum-idle: 5
        maximum-pool-size: 11
        connection-timeout: 30000
        idle-timeout: 600000
        max-lifetime: 1800000
  ```
- **Benefit**: 연결 재사용, 성능 향상

---

## 4. Exception Handling Components

### 4.1 GlobalExceptionHandler
- **Purpose**: 전역 예외 처리
- **Responsibilities**:
  - 모든 예외 캐치
  - 일관된 에러 응답 생성
- **Implementation**:
  ```java
  @ControllerAdvice
  public class GlobalExceptionHandler {
      @ExceptionHandler(Exception.class)
      public ResponseEntity<ErrorResponse> handleException(Exception e)
      
      @ExceptionHandler(AuthenticationException.class)
      public ResponseEntity<ErrorResponse> handleAuthException(AuthenticationException e)
  }
  ```

### 4.2 Custom Exceptions
- **Purpose**: 비즈니스 예외 정의
- **Examples**:
  - `AccountLockedException`
  - `InvalidStatusTransitionException`
  - `OrderNotFoundException`
  - `UncompletedOrdersException`

---

## 5. Logging Component

### 5.1 SLF4J Logger
- **Purpose**: 로깅
- **Configuration**:
  ```yaml
  logging:
    level:
      root: DEBUG
      com.tableorder: DEBUG
  ```
- **Usage**:
  ```java
  @Slf4j
  public class AdminService {
      public void login(String username, String password) {
          log.debug("Login attempt for user: {}", username);
      }
  }
  ```

---

## 6. CORS Component

### 6.1 WebConfig
- **Purpose**: CORS 설정
- **Implementation**:
  ```java
  @Configuration
  public class WebConfig implements WebMvcConfigurer {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
          registry.addMapping("/api/**")
              .allowedOrigins("*")
              .allowedMethods("*")
              .allowedHeaders("*");
      }
  }
  ```

---

## 7. Testing Components

### 7.1 Test Configuration
- **Purpose**: 테스트 환경 설정
- **Components**:
  - `@SpringBootTest`: 통합 테스트
  - `@WebMvcTest`: Controller 테스트
  - `@DataJpaTest`: Mapper 테스트

### 7.2 Mock Objects
- **Purpose**: 의존성 모킹
- **Implementation**: Mockito
- **Usage**:
  ```java
  @Mock
  private AdminMapper adminMapper;
  
  @InjectMocks
  private AdminService adminService;
  ```

---

## Component Interaction Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Admin Client                         │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ HTTP Request (JWT in Header)
                     ↓
┌─────────────────────────────────────────────────────────┐
│              JwtAuthenticationFilter                    │
│  - Extract JWT                                          │
│  - Validate Token (JwtTokenProvider)                    │
│  - Set Authentication                                   │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│                AdminController                          │
│  - Receive Request                                      │
│  - Delegate to Service                                  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│                 AdminService                            │
│  - Business Logic                                       │
│  - Call Mapper                                          │
│  - Publish Events (OrderEventPublisher)                 │
└────────────────────┬────────────────────────────────────┘
                     │
                     ├─────────────────────────────────────┐
                     │                                     │
                     ↓                                     ↓
┌──────────────────────────────┐    ┌──────────────────────────────┐
│        AdminMapper           │    │   OrderEventPublisher        │
│  - Database Queries          │    │  - Publish Events            │
│  - MyBatis XML               │    └────────────┬─────────────────┘
└──────────────┬───────────────┘                 │
               │                                 ↓
               ↓                    ┌──────────────────────────────┐
┌──────────────────────────────┐   │    SseEmitterManager         │
│      PostgreSQL              │   │  - Broadcast Events          │
│  - HikariCP Connection Pool  │   │  - Manage Connections        │
└──────────────────────────────┘   └────────────┬─────────────────┘
                                                 │
                                                 │ SSE Event
                                                 ↓
                                    ┌──────────────────────────────┐
                                    │      Admin Clients           │
                                    │  - Real-time Updates         │
                                    └──────────────────────────────┘
```

---

## Component Summary Table

| Component | Type | Purpose | Dependencies |
|-----------|------|---------|--------------|
| JwtTokenProvider | Security | JWT 생성/검증 | jjwt |
| JwtAuthenticationFilter | Security | JWT 필터 | JwtTokenProvider |
| PasswordEncoder | Security | 비밀번호 해싱 | Spring Security |
| SseEmitterManager | SSE | 연결 관리 | Spring Web |
| OrderEventPublisher | SSE | 이벤트 발행 | SseEmitterManager |
| HikariCP | Performance | 연결 풀 | Spring Boot |
| GlobalExceptionHandler | Reliability | 예외 처리 | Spring Web |
| SLF4J Logger | Reliability | 로깅 | Logback |
| WebConfig | Security | CORS 설정 | Spring Web |
| Test Configuration | Testing | 테스트 환경 | JUnit 5, Mockito |

---

## Infrastructure Dependencies

### External Services
- **PostgreSQL**: 데이터 저장소
- **None**: 외부 API 의존성 없음

### Internal Dependencies
- **Customer API (Unit 2)**: 주문 생성 이벤트 수신 (간접)

### Shared Components
- **Domain Entities**: Order, Admin, TableSession (Unit 2와 공유)
- **Security Components**: JWT, PasswordEncoder (Unit 2와 공유)
