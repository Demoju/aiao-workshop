# TDD Code Generation Plan for Unit 3 (Backend Admin API)

## Unit Context
- **Workspace Root**: /home/taeby/aidlc-workshop/aiao-workshop
- **Project Type**: Greenfield
- **Code Location**: backend/ (NOT in aidlc-docs/)
- **Stories**: FR-2.1 (관리자 인증), FR-2.2 (주문 모니터링), FR-2.3 (테이블 관리)
- **Test Cases**: 50개 (TC-U3-001 ~ TC-U3-050)

---

## Plan Step 0: Project Structure Setup
- [ ] Create backend/ directory structure
- [ ] Generate pom.xml with dependencies
- [ ] Generate application.yml configuration
- [ ] Create package structure (domain, service, controller, mapper, security, dto, exception, config)
- [ ] Verify project compiles

---

## Plan Step 1: Domain Entities (No TDD - Simple POJOs)
- [ ] Create Admin entity
- [ ] Create Order entity
- [ ] Create OrderItem entity
- [ ] Create TableSession entity
- [ ] Create Table entity
- [ ] Create Store entity
- [ ] Create OrderStatus enum

---

## Plan Step 2: DTOs (No TDD - Simple POJOs)
- [ ] Create AdminLoginRequestDto
- [ ] Create AdminLoginResponseDto
- [ ] Create OrderResponseDto
- [ ] Create OrderStatusUpdateDto
- [ ] Create ErrorResponse

---

## Plan Step 3: Custom Exceptions (No TDD - Simple Classes)
- [ ] Create AuthenticationException
- [ ] Create AccountLockedException
- [ ] Create OrderNotFoundException
- [ ] Create InvalidStatusTransitionException
- [ ] Create InvalidOrderStatusException
- [ ] Create SessionNotFoundException
- [ ] Create UncompletedOrdersException

---

## Plan Step 4: JwtTokenProvider (TDD)

### Step 4.1: generateToken() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing test (TC-U3-047)
  - Test: generateToken()은 유효한 JWT 반환
  - Expected: NotImplementedError
- [ ] **GREEN**: Minimal implementation
  - JWT 생성 로직 구현 (jjwt 사용)
  - adminId, storeId claims 포함
  - 16시간 만료
- [ ] **REFACTOR**: Improve code quality
  - Extract secret key to config
  - Add logging
- [ ] **VERIFY**: TC-U3-047 passes
- Story: FR-2.1

### Step 4.2: validateToken() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing tests (TC-U3-048, TC-U3-049)
  - Test 1: 유효한 토큰 → true
  - Test 2: 만료된 토큰 → false
- [ ] **GREEN**: Minimal implementation
  - JWT 검증 로직
  - 만료 체크
- [ ] **REFACTOR**: Improve error handling
- [ ] **VERIFY**: TC-U3-048, TC-U3-049 pass
- Story: FR-2.1

### Step 4.3: getAdminIdFromToken() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing test
  - Test: 토큰에서 adminId 추출
- [ ] **GREEN**: Minimal implementation
  - Claims 파싱
  - adminId 추출
- [ ] **REFACTOR**: Add null checks
- [ ] **VERIFY**: Test passes
- Story: FR-2.1

---

## Plan Step 5: AdminMapper (TDD with MyBatis)

### Step 5.1: findByUsername() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing tests (TC-U3-039, TC-U3-040)
  - Test 1: 존재하는 사용자 조회
  - Test 2: 존재하지 않는 사용자
- [ ] **GREEN**: Minimal implementation
  - Create AdminMapper.xml
  - SELECT query
- [ ] **REFACTOR**: Optimize query
- [ ] **VERIFY**: TC-U3-039, TC-U3-040 pass
- Story: FR-2.1

### Step 5.2: updateLoginAttempts() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing test (TC-U3-041)
- [ ] **GREEN**: Minimal implementation
  - UPDATE query
- [ ] **REFACTOR**: Add transaction
- [ ] **VERIFY**: TC-U3-041 passes
- Story: FR-2.1

---

## Plan Step 6: AdminService (TDD)

### Step 6.1: login() - Success Case - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing test (TC-U3-001)
  - Test: 정상 로그인 → JWT 반환
- [ ] **GREEN**: Minimal implementation
  - findByUsername() 호출
  - 비밀번호 검증 (BCrypt)
  - JWT 생성
  - loginAttempts = 0
- [ ] **REFACTOR**: Extract methods
- [ ] **VERIFY**: TC-U3-001 passes
- Story: FR-2.1

### Step 6.2: login() - Wrong Password - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing test (TC-U3-002)
  - Test: 잘못된 비밀번호 → AuthenticationException
- [ ] **GREEN**: Minimal implementation
  - 비밀번호 불일치 체크
  - loginAttempts++
  - throw AuthenticationException
- [ ] **REFACTOR**: Add logging
- [ ] **VERIFY**: TC-U3-002 passes
- Story: FR-2.1

### Step 6.3: login() - Account Locking - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing tests (TC-U3-003, TC-U3-004)
  - Test 1: 5회 실패 → 계정 잠금
  - Test 2: 잠금 상태 → AccountLockedException
- [ ] **GREEN**: Minimal implementation
  - loginAttempts >= 5 체크
  - lockedUntil 설정
  - throw AccountLockedException
- [ ] **REFACTOR**: Extract locking logic
- [ ] **VERIFY**: TC-U3-003, TC-U3-004 pass
- Story: FR-2.1

### Step 6.4: login() - User Not Found - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing test (TC-U3-005)
- [ ] **GREEN**: Minimal implementation
- [ ] **REFACTOR**: Consistent error messages
- [ ] **VERIFY**: TC-U3-005 passes
- Story: FR-2.1

### Step 6.5: getOrders() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing tests (TC-U3-006, TC-U3-007)
  - Test 1: 활성 세션 주문 조회
  - Test 2: 활성 세션 없음
- [ ] **GREEN**: Minimal implementation
  - 활성 세션 조회
  - 주문 조회
  - DTO 변환
- [ ] **REFACTOR**: Optimize queries
- [ ] **VERIFY**: TC-U3-006, TC-U3-007 pass
- Story: FR-2.2

### Step 6.6: updateOrderStatus() - Valid Transitions - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing tests (TC-U3-008, TC-U3-009, TC-U3-011)
  - Test 1: PENDING → PREPARING
  - Test 2: PREPARING → COMPLETED
  - Test 3: ANY → CANCELLED
- [ ] **GREEN**: Minimal implementation
  - 주문 조회
  - 상태 전환 검증
  - 상태 업데이트
- [ ] **REFACTOR**: Extract validation logic
- [ ] **VERIFY**: TC-U3-008, TC-U3-009, TC-U3-011 pass
- Story: FR-2.2

### Step 6.7: updateOrderStatus() - Invalid Transitions - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing tests (TC-U3-010, TC-U3-012)
  - Test 1: 잘못된 전환 → InvalidStatusTransitionException
  - Test 2: 주문 없음 → OrderNotFoundException
- [ ] **GREEN**: Minimal implementation
  - 전환 규칙 검증
  - throw exceptions
- [ ] **REFACTOR**: Create transition validator
- [ ] **VERIFY**: TC-U3-010, TC-U3-012 pass
- Story: FR-2.2

### Step 6.8: deleteOrder() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing tests (TC-U3-013, TC-U3-014, TC-U3-015, TC-U3-016)
  - Test 1: COMPLETED 삭제 성공
  - Test 2: CANCELLED 삭제 성공
  - Test 3: PENDING 삭제 불가
  - Test 4: 주문 없음
- [ ] **GREEN**: Minimal implementation
  - 주문 조회
  - 상태 검증
  - 삭제
- [ ] **REFACTOR**: Extract validation
- [ ] **VERIFY**: All tests pass
- Story: FR-2.3

---

## Plan Step 7: TableSessionService (TDD)

### Step 7.1: endSession() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing tests (TC-U3-017, TC-U3-018, TC-U3-019)
  - Test 1: 정상 종료
  - Test 2: 미완료 주문 존재
  - Test 3: 세션 없음
- [ ] **GREEN**: Minimal implementation
  - 활성 세션 조회
  - 미완료 주문 체크
  - 세션 종료
- [ ] **REFACTOR**: Extract validation
- [ ] **VERIFY**: All tests pass
- Story: FR-2.3

### Step 7.2: getPastOrders() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing tests (TC-U3-020, TC-U3-021)
  - Test 1: 과거 주문 조회
  - Test 2: 과거 주문 없음
- [ ] **GREEN**: Minimal implementation
  - 종료된 세션 조회
  - 주문 조회
  - 페이지네이션
- [ ] **REFACTOR**: Optimize pagination
- [ ] **VERIFY**: TC-U3-020, TC-U3-021 pass
- Story: FR-2.3

---

## Plan Step 8: SseEmitterManager (TDD)

### Step 8.1: createEmitter() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing test (TC-U3-022)
- [ ] **GREEN**: Minimal implementation
  - SseEmitter 생성
  - Map에 저장
  - Callbacks 설정
- [ ] **REFACTOR**: Extract callback logic
- [ ] **VERIFY**: TC-U3-022 passes
- Story: FR-2.2

### Step 8.2: broadcast() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing tests (TC-U3-023, TC-U3-024)
  - Test 1: 이벤트 브로드캐스트
  - Test 2: 전송 실패 시 제거
- [ ] **GREEN**: Minimal implementation
  - 모든 emitter에 전송
  - 실패 시 제거
- [ ] **REFACTOR**: Add error handling
- [ ] **VERIFY**: TC-U3-023, TC-U3-024 pass
- Story: FR-2.2

### Step 8.3: removeEmitter() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing test (TC-U3-025)
- [ ] **GREEN**: Minimal implementation
- [ ] **REFACTOR**: Add logging
- [ ] **VERIFY**: TC-U3-025 passes
- Story: FR-2.2

---

## Plan Step 9: AdminController (TDD with MockMvc)

### Step 9.1: POST /api/admin/login - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing tests (TC-U3-026, TC-U3-027, TC-U3-028)
  - Test 1: 정상 로그인 → 200
  - Test 2: 인증 실패 → 401
  - Test 3: 계정 잠금 → 403
- [ ] **GREEN**: Minimal implementation
  - @PostMapping
  - AdminService.login() 호출
  - DTO 반환
- [ ] **REFACTOR**: Add validation
- [ ] **VERIFY**: All tests pass
- Story: FR-2.1

### Step 9.2: GET /api/admin/orders - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing tests (TC-U3-029, TC-U3-030)
  - Test 1: 주문 조회 → 200
  - Test 2: JWT 없음 → 401
- [ ] **GREEN**: Minimal implementation
  - @GetMapping
  - JWT 검증
  - AdminService.getOrders() 호출
- [ ] **REFACTOR**: Extract JWT logic
- [ ] **VERIFY**: TC-U3-029, TC-U3-030 pass
- Story: FR-2.2

### Step 9.3: PATCH /api/admin/orders/{orderId}/status - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing tests (TC-U3-031, TC-U3-032)
- [ ] **GREEN**: Minimal implementation
- [ ] **REFACTOR**: Add validation
- [ ] **VERIFY**: Tests pass
- Story: FR-2.2

### Step 9.4: DELETE /api/admin/orders/{orderId} - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing tests (TC-U3-033, TC-U3-034)
- [ ] **GREEN**: Minimal implementation
- [ ] **REFACTOR**: Add validation
- [ ] **VERIFY**: Tests pass
- Story: FR-2.3

### Step 9.5: POST /api/admin/tables/{tableId}/end-session - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing tests (TC-U3-035, TC-U3-036)
- [ ] **GREEN**: Minimal implementation
- [ ] **REFACTOR**: Add validation
- [ ] **VERIFY**: Tests pass
- Story: FR-2.3

### Step 9.6: GET /api/admin/tables/{tableId}/past-orders - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing test (TC-U3-037)
- [ ] **GREEN**: Minimal implementation
- [ ] **REFACTOR**: Add pagination
- [ ] **VERIFY**: TC-U3-037 passes
- Story: FR-2.3

### Step 9.7: GET /api/admin/orders/stream - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing test (TC-U3-038)
- [ ] **GREEN**: Minimal implementation
  - SseEmitterManager.createEmitter() 호출
  - SseEmitter 반환
- [ ] **REFACTOR**: Add error handling
- [ ] **VERIFY**: TC-U3-038 passes
- Story: FR-2.2

---

## Plan Step 10: JwtAuthenticationFilter (TDD)
- [ ] **RED**: Write failing test (TC-U3-050)
- [ ] **GREEN**: Minimal implementation
  - JWT 추출
  - 검증
  - SecurityContext 설정
- [ ] **REFACTOR**: Extract methods
- [ ] **VERIFY**: TC-U3-050 passes
- Story: FR-2.1

---

## Plan Step 11: OrderMapper (TDD with MyBatis)

### Step 11.1: findBySessionIdIn() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing test (TC-U3-042)
- [ ] **GREEN**: Minimal implementation (MyBatis XML)
- [ ] **REFACTOR**: Optimize query
- [ ] **VERIFY**: TC-U3-042 passes
- Story: FR-2.2

### Step 11.2: updateStatus() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing test (TC-U3-043)
- [ ] **GREEN**: Minimal implementation
- [ ] **REFACTOR**: Add transaction
- [ ] **VERIFY**: TC-U3-043 passes
- Story: FR-2.2

### Step 11.3: deleteById() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing test (TC-U3-044)
- [ ] **GREEN**: Minimal implementation
- [ ] **REFACTOR**: Add cascade delete
- [ ] **VERIFY**: TC-U3-044 passes
- Story: FR-2.3

---

## Plan Step 12: TableSessionMapper (TDD with MyBatis)

### Step 12.1: findActiveByTableId() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing test (TC-U3-045)
- [ ] **GREEN**: Minimal implementation
- [ ] **REFACTOR**: Add index hint
- [ ] **VERIFY**: TC-U3-045 passes
- Story: FR-2.3

### Step 12.2: endSession() - RED-GREEN-REFACTOR
- [ ] **RED**: Write failing test (TC-U3-046)
- [ ] **GREEN**: Minimal implementation
- [ ] **REFACTOR**: Add transaction
- [ ] **VERIFY**: TC-U3-046 passes
- Story: FR-2.3

---

## Plan Step 13: GlobalExceptionHandler
- [ ] Create @ControllerAdvice
- [ ] Handle AuthenticationException → 401
- [ ] Handle AccountLockedException → 403
- [ ] Handle OrderNotFoundException → 404
- [ ] Handle InvalidStatusTransitionException → 400
- [ ] Handle InvalidOrderStatusException → 400
- [ ] Handle SessionNotFoundException → 404
- [ ] Handle UncompletedOrdersException → 400
- [ ] Handle generic Exception → 500

---

## Plan Step 14: Security Configuration
- [ ] Create SecurityConfig
- [ ] Configure JWT filter
- [ ] Configure CORS
- [ ] Configure password encoder
- [ ] Disable CSRF (REST API)
- [ ] Configure public endpoints (/api/admin/login)

---

## Plan Step 15: Database Migration Scripts
- [ ] Create schema.sql
  - admin table
  - orders table
  - order_item table
  - table_session table
  - table table
  - store table
- [ ] Create indexes
- [ ] Create sample data (data.sql)

---

## Plan Step 16: Docker Configuration
- [ ] Create Dockerfile
- [ ] Create docker-compose.yml
- [ ] Configure PostgreSQL service
- [ ] Configure backend service
- [ ] Configure volumes and networks

---

## Plan Step 17: Documentation
- [ ] Update README.md
  - Project setup
  - Build instructions
  - Run instructions
  - API documentation
- [ ] Generate API docs (Swagger - optional)

---

## Plan Summary

**Total Steps**: 17 major steps
**Total RED-GREEN-REFACTOR Cycles**: ~40 cycles
**Total Test Cases**: 50
**Estimated Files**: ~50 files
**Stories Covered**: FR-2.1, FR-2.2, FR-2.3

**Next Action**: Execute Step 0 (Project Structure Setup)
