# NFR Design Patterns - Unit 2 (Backend Customer API)

## Overview

이 문서는 Unit 2 (Backend Customer API)의 비기능 요구사항을 구현하기 위한 구체적인 설계 패턴을 정의합니다.

---

## 1. Performance Patterns

### 1.1 Database Index Strategy

**인덱스 생성 전략**: Primary Key + Foreign Key

**생성할 인덱스**:
```sql
-- Primary Keys (자동 생성)
CREATE INDEX idx_orders_pk ON orders(order_id);
CREATE INDEX idx_order_items_pk ON order_items(order_item_id);
CREATE INDEX idx_menus_pk ON menus(menu_id);
CREATE INDEX idx_tables_pk ON tables(table_id);
CREATE INDEX idx_table_sessions_pk ON table_sessions(session_id);

-- Foreign Keys
CREATE INDEX idx_orders_table_id ON orders(table_id);
CREATE INDEX idx_orders_session_id ON orders(session_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_menu_id ON order_items(menu_id);
CREATE INDEX idx_menus_category_id ON menus(category_id);
```

**근거**:
- FK 인덱스로 JOIN 성능 최적화
- 추가 컬럼 인덱스는 성능 문제 발생 시 추가
- 디스크 공간 절약

### 1.2 Query Optimization Pattern

**N+1 문제 해결**: JOIN으로 한 번에 조회

**OrderMapper.xml**:
```xml
<select id="findOrdersWithItems" resultMap="orderWithItemsMap">
    SELECT 
        o.order_id, o.order_number, o.total_amount, o.status, o.order_time,
        oi.order_item_id, oi.menu_id, oi.quantity, oi.unit_price,
        m.menu_name, m.price
    FROM orders o
    LEFT JOIN order_items oi ON o.order_id = oi.order_id
    LEFT JOIN menus m ON oi.menu_id = m.menu_id
    WHERE o.session_id = #{sessionId}
    ORDER BY o.order_time DESC, oi.order_item_id
</select>

<resultMap id="orderWithItemsMap" type="Order">
    <id property="orderId" column="order_id"/>
    <result property="orderNumber" column="order_number"/>
    <result property="totalAmount" column="total_amount"/>
    <result property="status" column="status"/>
    <result property="orderTime" column="order_time"/>
    <collection property="items" ofType="OrderItem">
        <id property="orderItemId" column="order_item_id"/>
        <result property="menuId" column="menu_id"/>
        <result property="menuName" column="menu_name"/>
        <result property="quantity" column="quantity"/>
        <result property="unitPrice" column="unit_price"/>
    </collection>
</resultMap>
```


### 1.3 Connection Pool Configuration

**설정**: HikariCP 기본값 사용

**application.yml**:
```yaml
spring:
  datasource:
    hikari:
      # 기본값 사용 (명시적 설정 불필요)
      # minimum-idle: 10
      # maximum-pool-size: 10
      # connection-timeout: 30000
      # idle-timeout: 600000
      # max-lifetime: 1800000
```

**근거**:
- Spring Boot 기본값이 대부분의 경우 최적
- 동시 요청 10개 이하로 충분
- 성능 문제 발생 시 튜닝

---

## 2. Security Patterns

### 2.1 Session Management Pattern

**Session 저장소**: Redis

**구조**:
```java
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, TableSession> sessionRedisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, TableSession> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(TableSession.class));
        return template;
    }
}

@Service
public class SessionService {
    @Autowired
    private RedisTemplate<String, TableSession> sessionRedisTemplate;
    
    public void saveSession(TableSession session) {
        String key = "session:" + session.getSessionId();
        sessionRedisTemplate.opsForValue().set(key, session, 24, TimeUnit.HOURS);
    }
    
    public TableSession getSession(String sessionId) {
        String key = "session:" + sessionId;
        return sessionRedisTemplate.opsForValue().get(key);
    }
    
    public boolean isActive(String sessionId) {
        TableSession session = getSession(sessionId);
        return session != null && session.isActive();
    }
}
```

**근거**:
- 빠른 조회 성능
- 서버 재시작 시에도 세션 유지
- 향후 수평 확장 가능

### 2.2 Session ID Transmission Pattern

**전달 방식**: HTTP Header

**구현**:
```java
// Client (Frontend)
axios.get('/api/customer/orders', {
    headers: {
        'X-Session-Id': sessionId
    }
});

// Server (Backend)
@RestController
@RequestMapping("/api/customer")
public class OrderController {
    @GetMapping("/orders")
    public List<OrderResponseDto> getOrders(
            @RequestHeader("X-Session-Id") String sessionId) {
        return orderService.getOrders(sessionId);
    }
}
```

**근거**:
- RESTful 표준
- 모든 HTTP 메서드에 적용 가능
- 깔끔한 API 설계


### 2.3 Password Validation Pattern

**검증 위치**: Service 레이어

**구현**:
```java
@Service
public class AuthService {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private TableMapper tableMapper;
    
    public TableLoginResponseDto login(TableLoginRequestDto request) {
        // 1. 테이블 조회
        Table table = tableMapper.findByStoreIdAndTableNumber(
            request.getStoreId(), 
            request.getTableNumber()
        );
        
        if (table == null) {
            throw new UnauthorizedException("테이블을 찾을 수 없습니다");
        }
        
        // 2. 비밀번호 검증 (Service에서)
        if (!passwordEncoder.matches(request.getPassword(), table.getPasswordHash())) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다");
        }
        
        // 3. 세션 생성 또는 조회
        TableSession session = sessionService.getOrCreateSession(table.getTableId());
        
        return TableLoginResponseDto.from(table, session);
    }
}
```

**근거**:
- 비즈니스 로직은 Service에서 처리
- Controller는 HTTP 처리만
- 테스트 용이

### 2.4 Authentication Filter Pattern

**구현 방식**: Spring Security Filter Chain

**SecurityConfig.java**:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private SessionService sessionService;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // REST API는 CSRF 비활성화
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(
                new SessionAuthenticationFilter(sessionService),
                UsernamePasswordAuthenticationFilter.class
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/customer/login").permitAll()
                .requestMatchers("/api/customer/**").authenticated()
                .requestMatchers("/api/admin/login").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
}

public class SessionAuthenticationFilter extends OncePerRequestFilter {
    
    private final SessionService sessionService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) {
        String sessionId = request.getHeader("X-Session-Id");
        
        if (sessionId != null && sessionService.isActive(sessionId)) {
            // 인증 성공
            Authentication auth = new PreAuthenticatedAuthenticationToken(
                sessionId, null, Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        
        filterChain.doFilter(request, response);
    }
}
```

**근거**:
- Spring Security의 강력한 보안 기능 활용
- CSRF, CORS 통합 관리
- 향후 JWT 추가 용이

---

## 3. Reliability Patterns

### 3.1 Transaction Management Pattern

**Transaction 범위**: Service 메서드 레벨

**구현**:
```java
@Service
public class OrderService {
    
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) {
        // 1. 주문 생성
        Order order = Order.builder()
            .orderNumber(generateOrderNumber())
            .tableId(request.getTableId())
            .sessionId(request.getSessionId())
            .totalAmount(request.getTotalAmount())
            .status("PENDING")
            .orderTime(LocalDateTime.now())
            .build();
        
        orderMapper.insert(order);
        
        // 2. 주문 아이템 생성
        for (OrderItemDto itemDto : request.getItems()) {
            OrderItem item = OrderItem.builder()
                .orderId(order.getOrderId())
                .menuId(itemDto.getMenuId())
                .quantity(itemDto.getQuantity())
                .unitPrice(itemDto.getUnitPrice())
                .build();
            
            orderItemMapper.insert(item);
        }
        
        // 3. 예외 발생 시 자동 롤백
        return OrderResponseDto.from(order);
    }
    
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderMapper.findById(orderId);
        
        if (!"PENDING".equals(order.getStatus())) {
            throw new InvalidOrderStatusException("대기 중인 주문만 취소할 수 있습니다");
        }
        
        // OrderItem 먼저 삭제
        orderItemMapper.deleteByOrderId(orderId);
        
        // Order 삭제
        orderMapper.delete(orderId);
    }
}
```

**근거**:
- 비즈니스 로직 단위로 Transaction
- 원자성 보장 (Order + OrderItem 동시 저장/삭제)
- RuntimeException 발생 시 자동 롤백


### 3.2 Error Handling Pattern

**전략**: @ControllerAdvice + Custom Exception + 에러 코드 enum

**ErrorCode.java**:
```java
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 인증 에러 (401)
    UNAUTHORIZED("AUTH001", "인증에 실패했습니다"),
    TABLE_NOT_FOUND("AUTH002", "테이블을 찾을 수 없습니다"),
    INVALID_PASSWORD("AUTH003", "비밀번호가 일치하지 않습니다"),
    
    // 비즈니스 에러 (400)
    MENU_NOT_FOUND("MENU001", "메뉴를 찾을 수 없습니다"),
    MENU_NOT_AVAILABLE("MENU002", "품절된 메뉴입니다"),
    PRICE_CHANGED("ORDER001", "메뉴 가격이 변경되었습니다"),
    TOTAL_AMOUNT_MISMATCH("ORDER002", "주문 금액이 일치하지 않습니다"),
    INVALID_ORDER_STATUS("ORDER003", "대기 중인 주문만 취소할 수 있습니다"),
    ORDER_NOT_FOUND("ORDER004", "주문을 찾을 수 없습니다"),
    
    // 서버 에러 (500)
    INTERNAL_SERVER_ERROR("SYS001", "서버 오류가 발생했습니다");
    
    private final String code;
    private final String message;
}
```

**Custom Exceptions**:
```java
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED);
    }
}

public class PriceChangedException extends BusinessException {
    public PriceChangedException() {
        super(ErrorCode.PRICE_CHANGED);
    }
}
```

**GlobalExceptionHandler.java**:
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .code(ex.getErrorCode().getCode())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        log.warn("Unauthorized: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.UNAUTHORIZED.value())
            .code(ex.getErrorCode().getCode())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unexpected error", ex);
        
        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
            .message("서버 오류가 발생했습니다")
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.internalServerError().body(response);
    }
}
```

**ErrorResponse.java**:
```java
@Data
@Builder
public class ErrorResponse {
    private int status;
    private String code;
    private String message;
    private LocalDateTime timestamp;
}
```

**근거**:
- 에러 코드로 Frontend에서 처리 용이
- 일관된 에러 응답 포맷
- 로깅 자동화

### 3.3 Logging Strategy

**전략**: 중요 비즈니스 이벤트만 로깅

**구현**:
```java
@Service
@Slf4j
public class OrderService {
    
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) {
        log.info("주문 생성 시작 - tableId: {}, sessionId: {}, totalAmount: {}", 
            request.getTableId(), request.getSessionId(), request.getTotalAmount());
        
        Order order = // ... 주문 생성 로직
        
        log.info("주문 생성 완료 - orderId: {}, orderNumber: {}", 
            order.getOrderId(), order.getOrderNumber());
        
        return OrderResponseDto.from(order);
    }
    
    @Transactional
    public void cancelOrder(Long orderId) {
        log.info("주문 취소 시작 - orderId: {}", orderId);
        
        // ... 취소 로직
        
        log.info("주문 취소 완료 - orderId: {}", orderId);
    }
}
```

**로깅 레벨**:
- **INFO**: 주문 생성, 주문 취소, 로그인 성공
- **WARN**: 비즈니스 예외 (가격 변경, 품절 등)
- **ERROR**: 시스템 예외, 예상치 못한 오류

**근거**:
- 모든 메서드 로깅은 과도함
- 중요 이벤트만 추적
- 성능 영향 최소화


### 3.4 Logback Configuration

**설정 파일**: logback-spring.xml

**logback-spring.xml**:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Console Appender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- File Appender with Rolling -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 일일 로테이션 -->
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.log</fileNamePattern>
            <!-- 1년 보관 -->
            <maxHistory>365</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Root Logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
    
    <!-- Application Logger -->
    <logger name="com.example" level="INFO" />
    
    <!-- MyBatis Logger (개발 시 DEBUG) -->
    <logger name="com.example.mapper" level="DEBUG" />
    
    <!-- Spring Framework Logger -->
    <logger name="org.springframework" level="WARN" />
    
</configuration>
```

**근거**:
- 일일 로테이션으로 파일 관리
- 1년 보관 후 자동 삭제
- 환경별 로그 레벨 조정 가능

---

## 4. API Design Patterns

### 4.1 RESTful API Design

**원칙**: REST + HTTP 상태 코드 표준화

**API 설계**:
```java
@RestController
@RequestMapping("/api/customer")
public class OrderController {
    
    // 주문 생성 - 201 Created
    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody @Valid OrderRequestDto request) {
        OrderResponseDto response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // 주문 목록 조회 - 200 OK
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrders(
            @RequestHeader("X-Session-Id") String sessionId) {
        List<OrderResponseDto> orders = orderService.getOrders(sessionId);
        return ResponseEntity.ok(orders);
    }
    
    // 주문 취소 - 204 No Content
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
```

**HTTP 상태 코드 표준**:
- **200 OK**: 조회 성공
- **201 Created**: 생성 성공
- **204 No Content**: 삭제 성공
- **400 Bad Request**: 잘못된 요청
- **401 Unauthorized**: 인증 실패
- **404 Not Found**: 리소스 없음
- **409 Conflict**: 중복 요청
- **500 Internal Server Error**: 서버 오류

**근거**:
- 표준 HTTP 상태 코드 사용
- Frontend에서 상태 코드로 결과 판단
- API 문서화 용이

### 4.2 DTO Conversion Pattern

**변환 위치**: Service 레이어

**구현**:
```java
@Service
public class OrderService {
    
    public OrderResponseDto createOrder(OrderRequestDto request) {
        // 1. 비즈니스 로직
        Order order = // ... 주문 생성
        
        // 2. DTO 변환 (Service에서)
        return OrderResponseDto.from(order);
    }
}

@Data
@Builder
public class OrderResponseDto {
    private Long orderId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime orderTime;
    private List<OrderItemDto> items;
    
    public static OrderResponseDto from(Order order) {
        return OrderResponseDto.builder()
            .orderId(order.getOrderId())
            .orderNumber(order.getOrderNumber())
            .totalAmount(order.getTotalAmount())
            .status(order.getStatus())
            .orderTime(order.getOrderTime())
            .items(order.getItems().stream()
                .map(OrderItemDto::from)
                .collect(Collectors.toList()))
            .build();
    }
}
```

**근거**:
- Controller는 HTTP 처리만
- Service가 DTO 반환 → 재사용 가능
- Entity가 Controller에 노출 안 됨

### 4.3 Error Response Format

**포맷**: 표준 포맷 (status, message, timestamp)

**ErrorResponse.java**:
```java
@Data
@Builder
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
```

**응답 예시**:
```json
{
  "status": 400,
  "message": "메뉴 가격이 변경되었습니다",
  "timestamp": "2026-02-09T16:45:00"
}
```

**근거**:
- MVP에 충분한 정보
- 간단하고 명확
- 필요 시 확장 가능 (code, path 추가)

### 4.4 API Versioning

**전략**: URL 경로 버전 관리

**구현**:
```java
@RestController
@RequestMapping("/api/v1/customer")
public class OrderController {
    // ...
}
```

**근거**:
- 명확한 버전 구분
- URL만 보고 버전 파악 가능
- 향후 v2 추가 용이

---

## 5. Data Access Patterns

### 5.1 MyBatis Mapper Configuration

**구성 방식**: XML만 사용

**디렉토리 구조**:
```
src/main/java/
  └── com/example/mapper/
      ├── OrderMapper.java
      ├── OrderItemMapper.java
      ├── MenuMapper.java
      └── TableMapper.java

src/main/resources/
  └── mapper/
      ├── OrderMapper.xml
      ├── OrderItemMapper.xml
      ├── MenuMapper.xml
      └── TableMapper.xml
```

**근거**:
- 복잡한 쿼리 작성 용이
- SQL과 Java 코드 분리
- 유지보수 쉬움


### 5.2 Dynamic SQL Pattern

**처리 방식**: MyBatis `<if>`, `<choose>` 사용

**OrderMapper.xml**:
```xml
<select id="searchOrders" resultType="Order">
    SELECT * FROM orders
    <where>
        <if test="tableId != null">
            AND table_id = #{tableId}
        </if>
        <if test="sessionId != null">
            AND session_id = #{sessionId}
        </if>
        <if test="status != null">
            AND status = #{status}
        </if>
        <if test="startDate != null">
            AND order_time >= #{startDate}
        </if>
        <if test="endDate != null">
            AND order_time <= #{endDate}
        </if>
    </where>
    ORDER BY order_time DESC
</select>
```

**근거**:
- MyBatis의 강력한 Dynamic SQL 기능
- 조건부 WHERE 절 생성
- SQL Injection 방지

---

## 6. Testing Patterns

### 6.1 Unit Test Structure

**테스트 대상**: Service 레이어만

**OrderServiceTest.java**:
```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderMapper orderMapper;
    
    @Mock
    private MenuMapper menuMapper;
    
    @Mock
    private SessionService sessionService;
    
    @InjectMocks
    private OrderService orderService;
    
    @Test
    @DisplayName("주문 생성 - 정상 케이스")
    void createOrder_Success() {
        // Given
        OrderRequestDto request = createOrderRequest();
        Menu menu = createMenu();
        when(menuMapper.findById(1L)).thenReturn(menu);
        when(sessionService.isActive(100L)).thenReturn(true);
        
        // When
        OrderResponseDto result = orderService.createOrder(request);
        
        // Then
        assertThat(result.getTotalAmount()).isEqualTo(new BigDecimal("20000"));
        verify(orderMapper).insert(any(Order.class));
    }
}
```

**근거**:
- 비즈니스 로직 집중 테스트
- 빠른 실행
- 70% 커버리지 목표 달성

### 6.2 Mock Strategy

**전략**: Mockito로 모든 의존성 Mock

**구현**:
```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    // 모든 의존성 Mock
    @Mock
    private OrderMapper orderMapper;
    
    @Mock
    private OrderItemMapper orderItemMapper;
    
    @Mock
    private MenuMapper menuMapper;
    
    @Mock
    private SessionService sessionService;
    
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    
    @InjectMocks
    private OrderService orderService;
}
```

**근거**:
- 순수 Service 로직만 테스트
- DB, Redis 없이 테스트 가능
- 빠른 실행

---

## 7. Validation Patterns

### 7.1 Input Validation

**검증 위치**: Controller에서만 (@Valid)

**구현**:
```java
@Data
public class OrderRequestDto {
    @NotNull(message = "테이블 ID는 필수입니다")
    private Long tableId;
    
    @NotNull(message = "세션 ID는 필수입니다")
    private Long sessionId;
    
    @NotEmpty(message = "주문 항목은 최소 1개 이상이어야 합니다")
    private List<OrderItemDto> items;
    
    @NotNull(message = "총 금액은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false, message = "총 금액은 0보다 커야 합니다")
    private BigDecimal totalAmount;
}

@RestController
public class OrderController {
    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody @Valid OrderRequestDto request) {  // @Valid
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(orderService.createOrder(request));
    }
}
```

**근거**:
- Spring Validation 표준
- 선언적 검증
- 자동 에러 응답

### 7.2 Business Rule Validation

**검증 위치**: Service 레이어

**구현**:
```java
@Service
public class OrderService {
    
    public OrderResponseDto createOrder(OrderRequestDto request) {
        // 1. 메뉴 검증 (비즈니스 규칙)
        for (OrderItemDto item : request.getItems()) {
            Menu menu = menuMapper.findById(item.getMenuId());
            
            if (menu == null) {
                throw new MenuNotFoundException("메뉴를 찾을 수 없습니다");
            }
            
            if (!menu.isAvailable()) {
                throw new MenuNotAvailableException("품절된 메뉴입니다");
            }
            
            // 가격 검증
            if (!menu.getPrice().equals(item.getUnitPrice())) {
                throw new PriceChangedException("메뉴 가격이 변경되었습니다");
            }
        }
        
        // 2. 총액 검증
        BigDecimal calculatedTotal = request.getItems().stream()
            .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (!calculatedTotal.equals(request.getTotalAmount())) {
            throw new TotalAmountMismatchException("주문 금액이 일치하지 않습니다");
        }
        
        // 3. 주문 생성
        // ...
    }
}
```

**근거**:
- 비즈니스 규칙은 Service에서
- DB 조회 필요한 검증
- 복잡한 계산 로직

---

## 8. Idempotency Pattern

### 8.1 Duplicate Request Prevention

**전략**: Idempotency Key + Redis

**구현**:
```java
@Service
public class OrderService {
    
    @Autowired
    private RedisTemplate<String, OrderResponseDto> idempotencyRedisTemplate;
    
    public OrderResponseDto createOrder(OrderRequestDto request, String idempotencyKey) {
        // 1. Idempotency Key 확인
        if (idempotencyKey != null) {
            String cacheKey = "idempotency:" + idempotencyKey;
            OrderResponseDto cached = idempotencyRedisTemplate.opsForValue().get(cacheKey);
            
            if (cached != null) {
                log.info("중복 요청 감지 - idempotencyKey: {}", idempotencyKey);
                return cached;  // 캐시된 응답 반환
            }
        }
        
        // 2. 주문 생성
        OrderResponseDto response = // ... 주문 생성 로직
        
        // 3. Redis에 저장 (24시간 TTL)
        if (idempotencyKey != null) {
            String cacheKey = "idempotency:" + idempotencyKey;
            idempotencyRedisTemplate.opsForValue().set(cacheKey, response, 24, TimeUnit.HOURS);
        }
        
        return response;
    }
}
```

### 8.2 Idempotency Key Transmission

**전달 방식**: Request Body

**구현**:
```java
@Data
public class OrderRequestDto {
    private String idempotencyKey;  // Request Body에 포함
    private Long tableId;
    private Long sessionId;
    private List<OrderItemDto> items;
    private BigDecimal totalAmount;
}

@RestController
public class OrderController {
    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody @Valid OrderRequestDto request) {
        OrderResponseDto response = orderService.createOrder(
            request, 
            request.getIdempotencyKey()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

**근거**:
- Request Body에 포함하여 간단히 전달
- Frontend에서 UUID 생성하여 전송

---

## 9. Monitoring Pattern

### 9.1 Health Check

**확인 항목**: Application + Database 연결

**구현**:
```java
// Spring Boot Actuator 사용
// application.yml
management:
  endpoints:
    web:
      exposure:
        include: health
  endpoint:
    health:
      show-details: always

// Custom Health Indicator
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(1)) {
                return Health.up()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("status", "connected")
                    .build();
            }
        } catch (Exception e) {
            return Health.down()
                .withDetail("database", "PostgreSQL")
                .withDetail("error", e.getMessage())
                .build();
        }
        return Health.down().build();
    }
}
```

**Health Check 응답**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "status": "connected"
      }
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

**근거**:
- Application 상태 모니터링
- Database 연결 확인
- 운영 모니터링 용이

---

## Summary

이 문서는 Unit 2 (Backend Customer API)의 NFR 요구사항을 구현하기 위한 구체적인 설계 패턴을 정의했습니다. 각 패턴은 성능, 보안, 안정성, 유지보수성을 고려하여 설계되었으며, Code Generation 단계에서 이 패턴들을 기반으로 실제 코드를 생성합니다.

