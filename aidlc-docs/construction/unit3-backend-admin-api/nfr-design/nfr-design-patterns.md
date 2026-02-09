# NFR Design Patterns - Unit 3 (Backend Admin API)

## Overview
NFR 요구사항을 구현하기 위한 설계 패턴

---

## 1. Performance Patterns

### Pattern 1.1: Connection Pooling
- **Pattern**: HikariCP Connection Pool
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
- **Benefit**: 연결 재사용으로 성능 향상

### Pattern 1.2: Query Optimization
- **Pattern**: Filtered Query (현재 세션만)
- **Implementation**:
  ```sql
  SELECT * FROM orders 
  WHERE sessionId IN (
    SELECT id FROM table_session WHERE isActive = true
  )
  ORDER BY createdAt DESC
  ```
- **Benefit**: 불필요한 데이터 제외로 쿼리 성능 향상

### Pattern 1.3: Index Strategy
- **Pattern**: Database Indexing
- **Indexes**:
  - `orders.sessionId` (FK index)
  - `orders.status` (조회 최적화)
  - `table_session.tableId, isActive` (복합 인덱스)
- **Benefit**: 조회 성능 향상

---

## 2. Scalability Patterns

### Pattern 2.1: Stateless API
- **Pattern**: Stateless REST API
- **Implementation**: JWT 토큰 기반 인증 (서버 세션 없음)
- **Benefit**: 수평 확장 가능

### Pattern 2.2: SSE Connection Management
- **Pattern**: In-Memory Connection Registry
- **Implementation**:
  ```java
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
  ```
- **Benefit**: 단순하고 빠른 연결 관리

---

## 3. Security Patterns

### Pattern 3.1: JWT Authentication
- **Pattern**: Token-Based Authentication
- **Flow**:
  ```
  Client → Login → JWT Token → Store in SessionStorage
  Client → API Request → JWT in Authorization Header
  Server → Validate JWT → Process Request
  ```
- **Implementation**:
  ```java
  @Component
  public class JwtAuthenticationFilter extends OncePerRequestFilter {
      // JWT 검증 로직
  }
  ```

### Pattern 3.2: Password Hashing
- **Pattern**: BCrypt Hashing
- **Implementation**:
  ```java
  @Bean
  public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }
  ```
- **Benefit**: 안전한 비밀번호 저장

### Pattern 3.3: Account Locking
- **Pattern**: Failed Attempt Counter
- **Logic**:
  ```
  loginAttempts++
  if (loginAttempts >= 5) {
      lockedUntil = now + 30 minutes
  }
  ```
- **Benefit**: 무차별 대입 공격 방지

---

## 4. Reliability Patterns

### Pattern 4.1: Global Exception Handler
- **Pattern**: @ControllerAdvice
- **Implementation**:
  ```java
  @ControllerAdvice
  public class GlobalExceptionHandler {
      @ExceptionHandler(Exception.class)
      public ResponseEntity<ErrorResponse> handleException(Exception e) {
          return ResponseEntity.status(500)
              .body(new ErrorResponse(e.getMessage()));
      }
  }
  ```
- **Benefit**: 일관된 에러 응답

### Pattern 4.2: Logging Strategy
- **Pattern**: SLF4J + Logback
- **Configuration**:
  ```yaml
  logging:
    level:
      root: DEBUG
      com.tableorder: DEBUG
  ```
- **Benefit**: 디버깅 및 모니터링

---

## 5. SSE Patterns

### Pattern 5.1: Event Broadcasting
- **Pattern**: Pub-Sub Pattern
- **Flow**:
  ```
  Customer API → Order Created → Event Published
  SseEmitterManager → Broadcast to All Emitters
  Admin Clients → Receive Event
  ```
- **Implementation**:
  ```java
  public void broadcast(OrderEvent event) {
      emitters.values().forEach(emitter -> {
          try {
              emitter.send(event);
          } catch (IOException e) {
              emitters.remove(emitter);
          }
      });
  }
  ```

### Pattern 5.2: Connection Cleanup
- **Pattern**: Callback-Based Cleanup
- **Implementation**:
  ```java
  emitter.onCompletion(() -> emitters.remove(adminId));
  emitter.onTimeout(() -> emitters.remove(adminId));
  emitter.onError(e -> emitters.remove(adminId));
  ```
- **Benefit**: 자동 리소스 정리

---

## 6. Testing Patterns

### Pattern 6.1: Unit Testing
- **Pattern**: Mockito-Based Unit Tests
- **Example**:
  ```java
  @ExtendWith(MockitoExtension.class)
  class AdminServiceTest {
      @Mock
      private AdminMapper adminMapper;
      
      @InjectMocks
      private AdminService adminService;
      
      @Test
      void testLogin() {
          // Test logic
      }
  }
  ```

### Pattern 6.2: Controller Testing
- **Pattern**: MockMvc
- **Example**:
  ```java
  @WebMvcTest(AdminController.class)
  class AdminControllerTest {
      @Autowired
      private MockMvc mockMvc;
      
      @Test
      void testLoginEndpoint() throws Exception {
          mockMvc.perform(post("/api/admin/login"))
              .andExpect(status().isOk());
      }
  }
  ```

---

## 7. CORS Pattern

### Pattern 7.1: Global CORS Configuration
- **Pattern**: WebMvcConfigurer
- **Implementation**:
  ```java
  @Configuration
  public class WebConfig implements WebMvcConfigurer {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
          registry.addMapping("/api/**")
              .allowedOrigins("*")
              .allowedMethods("*");
      }
  }
  ```
- **Note**: 개발 환경용, 프로덕션에서는 특정 도메인만 허용

---

## 8. Concurrency Pattern

### Pattern 8.1: Last-Write-Wins
- **Pattern**: No Locking
- **Implementation**: 기본 UPDATE 쿼리
- **Trade-off**: 간단하지만 데이터 손실 가능성

---

## Pattern Summary Table

| Category | Pattern | Implementation | Benefit |
|----------|---------|----------------|---------|
| Performance | Connection Pooling | HikariCP | 연결 재사용 |
| Performance | Query Optimization | Filtered Query | 성능 향상 |
| Performance | Indexing | DB Indexes | 조회 속도 |
| Scalability | Stateless API | JWT | 수평 확장 |
| Security | JWT Auth | JwtAuthenticationFilter | Stateless 인증 |
| Security | Password Hashing | BCrypt | 안전한 저장 |
| Security | Account Locking | Failed Attempt Counter | 공격 방지 |
| Reliability | Exception Handler | @ControllerAdvice | 일관된 에러 |
| Reliability | Logging | SLF4J + Logback | 디버깅 |
| SSE | Event Broadcasting | Pub-Sub | 실시간 업데이트 |
| SSE | Connection Cleanup | Callbacks | 자동 정리 |
| Testing | Unit Testing | Mockito | 격리 테스트 |
| Testing | Controller Testing | MockMvc | 통합 테스트 |
| CORS | Global Config | WebMvcConfigurer | 개발 편의 |
| Concurrency | Last-Write-Wins | No Locking | 단순성 |
