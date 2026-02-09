# Test Plan for Unit 3 (Backend Admin API)

## Unit Overview
- **Unit**: unit3-backend-admin-api
- **Stories**: 관리자 인증, 주문 관리, 테이블 세션 관리, SSE 실시간 업데이트
- **Requirements**: FR-2.1, FR-2.2, FR-2.3

---

## Business Logic Layer Tests

### AdminService.login()
- **TC-U3-001**: 정상 로그인 성공
  - Given: 유효한 username과 password
  - When: login() 호출
  - Then: JWT 토큰 반환, loginAttempts = 0
  - Story: FR-2.1
  - Status: ⬜ Not Started

- **TC-U3-002**: 잘못된 비밀번호
  - Given: 유효한 username, 잘못된 password
  - When: login() 호출
  - Then: AuthenticationException, loginAttempts++
  - Story: FR-2.1
  - Status: ⬜ Not Started

- **TC-U3-003**: 5회 실패 후 계정 잠금
  - Given: loginAttempts = 4, 잘못된 password
  - When: login() 호출
  - Then: AccountLockedException, lockedUntil = now + 30분
  - Story: FR-2.1
  - Status: ⬜ Not Started

- **TC-U3-004**: 계정 잠금 상태에서 로그인 시도
  - Given: lockedUntil > now
  - When: login() 호출
  - Then: AccountLockedException
  - Story: FR-2.1
  - Status: ⬜ Not Started

- **TC-U3-005**: 존재하지 않는 사용자
  - Given: 존재하지 않는 username
  - When: login() 호출
  - Then: AuthenticationException
  - Story: FR-2.1
  - Status: ⬜ Not Started

### AdminService.getOrders()
- **TC-U3-006**: 현재 세션 주문 목록 조회
  - Given: 활성 세션 2개, 각 세션에 주문 3개
  - When: getOrders(storeId) 호출
  - Then: 6개 주문 반환 (최신순)
  - Story: FR-2.2
  - Status: ⬜ Not Started

- **TC-U3-007**: 활성 세션 없음
  - Given: 활성 세션 0개
  - When: getOrders(storeId) 호출
  - Then: 빈 리스트 반환
  - Story: FR-2.2
  - Status: ⬜ Not Started

### AdminService.updateOrderStatus()
- **TC-U3-008**: PENDING → PREPARING 전환
  - Given: 주문 상태 = PENDING
  - When: updateOrderStatus(orderId, PREPARING) 호출
  - Then: 상태 업데이트 성공
  - Story: FR-2.2
  - Status: ⬜ Not Started

- **TC-U3-009**: PREPARING → COMPLETED 전환
  - Given: 주문 상태 = PREPARING
  - When: updateOrderStatus(orderId, COMPLETED) 호출
  - Then: 상태 업데이트 성공
  - Story: FR-2.2
  - Status: ⬜ Not Started

- **TC-U3-010**: 잘못된 상태 전환 (COMPLETED → PENDING)
  - Given: 주문 상태 = COMPLETED
  - When: updateOrderStatus(orderId, PENDING) 호출
  - Then: InvalidStatusTransitionException
  - Story: FR-2.2
  - Status: ⬜ Not Started

- **TC-U3-011**: 취소는 언제든 가능
  - Given: 주문 상태 = PREPARING
  - When: updateOrderStatus(orderId, CANCELLED) 호출
  - Then: 상태 업데이트 성공
  - Story: FR-2.2
  - Status: ⬜ Not Started

- **TC-U3-012**: 존재하지 않는 주문
  - Given: 존재하지 않는 orderId
  - When: updateOrderStatus(orderId, PREPARING) 호출
  - Then: OrderNotFoundException
  - Story: FR-2.2
  - Status: ⬜ Not Started

### AdminService.deleteOrder()
- **TC-U3-013**: COMPLETED 상태 주문 삭제
  - Given: 주문 상태 = COMPLETED
  - When: deleteOrder(orderId) 호출
  - Then: 주문 삭제 성공
  - Story: FR-2.3
  - Status: ⬜ Not Started

- **TC-U3-014**: CANCELLED 상태 주문 삭제
  - Given: 주문 상태 = CANCELLED
  - When: deleteOrder(orderId) 호출
  - Then: 주문 삭제 성공
  - Story: FR-2.3
  - Status: ⬜ Not Started

- **TC-U3-015**: PENDING 상태 주문 삭제 불가
  - Given: 주문 상태 = PENDING
  - When: deleteOrder(orderId) 호출
  - Then: InvalidOrderStatusException
  - Story: FR-2.3
  - Status: ⬜ Not Started

- **TC-U3-016**: 존재하지 않는 주문
  - Given: 존재하지 않는 orderId
  - When: deleteOrder(orderId) 호출
  - Then: OrderNotFoundException
  - Story: FR-2.3
  - Status: ⬜ Not Started

### TableSessionService.endSession()
- **TC-U3-017**: 정상 세션 종료
  - Given: 활성 세션, 모든 주문 COMPLETED
  - When: endSession(tableId) 호출
  - Then: endedAt 설정, isActive = false
  - Story: FR-2.3
  - Status: ⬜ Not Started

- **TC-U3-018**: 미완료 주문 존재
  - Given: 활성 세션, PENDING 주문 1개
  - When: endSession(tableId) 호출
  - Then: UncompletedOrdersException
  - Story: FR-2.3
  - Status: ⬜ Not Started

- **TC-U3-019**: 활성 세션 없음
  - Given: 활성 세션 없음
  - When: endSession(tableId) 호출
  - Then: SessionNotFoundException
  - Story: FR-2.3
  - Status: ⬜ Not Started

### TableSessionService.getPastOrders()
- **TC-U3-020**: 과거 주문 조회 (페이지네이션)
  - Given: 종료된 세션 3개, 각 세션에 주문 5개
  - When: getPastOrders(tableId, page=0, size=10) 호출
  - Then: 10개 주문 반환 (최신순)
  - Story: FR-2.3
  - Status: ⬜ Not Started

- **TC-U3-021**: 과거 주문 없음
  - Given: 종료된 세션 없음
  - When: getPastOrders(tableId, page=0, size=10) 호출
  - Then: 빈 페이지 반환
  - Story: FR-2.3
  - Status: ⬜ Not Started

### SseEmitterManager.createEmitter()
- **TC-U3-022**: SSE 연결 생성
  - Given: adminId = "admin1"
  - When: createEmitter("admin1") 호출
  - Then: SseEmitter 반환, emitters에 저장
  - Story: FR-2.2
  - Status: ⬜ Not Started

### SseEmitterManager.broadcast()
- **TC-U3-023**: 이벤트 브로드캐스트
  - Given: 연결된 emitter 2개
  - When: broadcast(orderEvent) 호출
  - Then: 모든 emitter에 이벤트 전송
  - Story: FR-2.2
  - Status: ⬜ Not Started

- **TC-U3-024**: 전송 실패 시 emitter 제거
  - Given: 연결된 emitter 1개 (전송 실패)
  - When: broadcast(orderEvent) 호출
  - Then: 실패한 emitter 제거
  - Story: FR-2.2
  - Status: ⬜ Not Started

### SseEmitterManager.removeEmitter()
- **TC-U3-025**: SSE 연결 제거
  - Given: 연결된 emitter 1개
  - When: removeEmitter("admin1") 호출
  - Then: emitter 제거됨
  - Story: FR-2.2
  - Status: ⬜ Not Started

---

## API Layer Tests

### POST /api/admin/login
- **TC-U3-026**: 정상 로그인 API
  - Given: 유효한 요청 body
  - When: POST /api/admin/login
  - Then: 200 OK, JWT 토큰 반환
  - Story: FR-2.1
  - Status: ⬜ Not Started

- **TC-U3-027**: 인증 실패 API
  - Given: 잘못된 비밀번호
  - When: POST /api/admin/login
  - Then: 401 Unauthorized
  - Story: FR-2.1
  - Status: ⬜ Not Started

- **TC-U3-028**: 계정 잠금 API
  - Given: 계정 잠금 상태
  - When: POST /api/admin/login
  - Then: 403 Forbidden
  - Story: FR-2.1
  - Status: ⬜ Not Started

### GET /api/admin/orders
- **TC-U3-029**: 주문 목록 조회 API
  - Given: 유효한 JWT 토큰
  - When: GET /api/admin/orders
  - Then: 200 OK, 주문 목록 반환
  - Story: FR-2.2
  - Status: ⬜ Not Started

- **TC-U3-030**: JWT 없이 요청
  - Given: Authorization 헤더 없음
  - When: GET /api/admin/orders
  - Then: 401 Unauthorized
  - Story: FR-2.2
  - Status: ⬜ Not Started

### PATCH /api/admin/orders/{orderId}/status
- **TC-U3-031**: 주문 상태 변경 API
  - Given: 유효한 JWT, 유효한 상태 전환
  - When: PATCH /api/admin/orders/1/status
  - Then: 200 OK
  - Story: FR-2.2
  - Status: ⬜ Not Started

- **TC-U3-032**: 잘못된 상태 전환 API
  - Given: 유효한 JWT, 잘못된 상태 전환
  - When: PATCH /api/admin/orders/1/status
  - Then: 400 Bad Request
  - Story: FR-2.2
  - Status: ⬜ Not Started

### DELETE /api/admin/orders/{orderId}
- **TC-U3-033**: 주문 삭제 API
  - Given: 유효한 JWT, COMPLETED 주문
  - When: DELETE /api/admin/orders/1
  - Then: 200 OK
  - Story: FR-2.3
  - Status: ⬜ Not Started

- **TC-U3-034**: 삭제 불가 상태 API
  - Given: 유효한 JWT, PENDING 주문
  - When: DELETE /api/admin/orders/1
  - Then: 400 Bad Request
  - Story: FR-2.3
  - Status: ⬜ Not Started

### POST /api/admin/tables/{tableId}/end-session
- **TC-U3-035**: 세션 종료 API
  - Given: 유효한 JWT, 완료된 주문만 존재
  - When: POST /api/admin/tables/1/end-session
  - Then: 200 OK
  - Story: FR-2.3
  - Status: ⬜ Not Started

- **TC-U3-036**: 미완료 주문 존재 API
  - Given: 유효한 JWT, PENDING 주문 존재
  - When: POST /api/admin/tables/1/end-session
  - Then: 400 Bad Request
  - Story: FR-2.3
  - Status: ⬜ Not Started

### GET /api/admin/tables/{tableId}/past-orders
- **TC-U3-037**: 과거 주문 조회 API
  - Given: 유효한 JWT
  - When: GET /api/admin/tables/1/past-orders?page=0&size=10
  - Then: 200 OK, 페이지네이션된 주문 목록
  - Story: FR-2.3
  - Status: ⬜ Not Started

### GET /api/admin/orders/stream
- **TC-U3-038**: SSE 연결 API
  - Given: 유효한 JWT
  - When: GET /api/admin/orders/stream
  - Then: 200 OK, SSE 연결 수립
  - Story: FR-2.2
  - Status: ⬜ Not Started

---

## Repository Layer Tests

### AdminMapper.findByUsername()
- **TC-U3-039**: 사용자명으로 관리자 조회
  - Given: DB에 admin 존재
  - When: findByUsername("admin") 호출
  - Then: Admin 엔티티 반환
  - Story: FR-2.1
  - Status: ⬜ Not Started

- **TC-U3-040**: 존재하지 않는 사용자
  - Given: DB에 admin 없음
  - When: findByUsername("unknown") 호출
  - Then: Optional.empty() 반환
  - Story: FR-2.1
  - Status: ⬜ Not Started

### AdminMapper.updateLoginAttempts()
- **TC-U3-041**: 로그인 시도 횟수 업데이트
  - Given: admin 존재
  - When: updateLoginAttempts(1, 5, lockedUntil) 호출
  - Then: loginAttempts = 5, lockedUntil 업데이트
  - Story: FR-2.1
  - Status: ⬜ Not Started

### OrderMapper.findBySessionIdIn()
- **TC-U3-042**: 세션 ID 목록으로 주문 조회
  - Given: 세션 1, 2에 각각 주문 3개
  - When: findBySessionIdIn([1, 2]) 호출
  - Then: 6개 주문 반환
  - Story: FR-2.2
  - Status: ⬜ Not Started

### OrderMapper.updateStatus()
- **TC-U3-043**: 주문 상태 업데이트
  - Given: 주문 존재
  - When: updateStatus(1, PREPARING) 호출
  - Then: 상태 업데이트 성공
  - Story: FR-2.2
  - Status: ⬜ Not Started

### OrderMapper.deleteById()
- **TC-U3-044**: 주문 삭제
  - Given: 주문 존재
  - When: deleteById(1) 호출
  - Then: 주문 삭제 성공
  - Story: FR-2.3
  - Status: ⬜ Not Started

### TableSessionMapper.findActiveByTableId()
- **TC-U3-045**: 활성 세션 조회
  - Given: 테이블 1에 활성 세션 존재
  - When: findActiveByTableId(1) 호출
  - Then: TableSession 반환
  - Story: FR-2.3
  - Status: ⬜ Not Started

### TableSessionMapper.endSession()
- **TC-U3-046**: 세션 종료
  - Given: 세션 존재
  - When: endSession(1, now) 호출
  - Then: endedAt 설정, isActive = false
  - Story: FR-2.3
  - Status: ⬜ Not Started

---

## Security Component Tests

### JwtTokenProvider.generateToken()
- **TC-U3-047**: JWT 토큰 생성
  - Given: Admin 엔티티
  - When: generateToken(admin) 호출
  - Then: 유효한 JWT 토큰 반환
  - Story: FR-2.1
  - Status: ⬜ Not Started

### JwtTokenProvider.validateToken()
- **TC-U3-048**: 유효한 토큰 검증
  - Given: 유효한 JWT 토큰
  - When: validateToken(token) 호출
  - Then: true 반환
  - Story: FR-2.1
  - Status: ⬜ Not Started

- **TC-U3-049**: 만료된 토큰 검증
  - Given: 만료된 JWT 토큰
  - When: validateToken(token) 호출
  - Then: false 반환
  - Story: FR-2.1
  - Status: ⬜ Not Started

### JwtAuthenticationFilter.doFilterInternal()
- **TC-U3-050**: JWT 필터 - 유효한 토큰
  - Given: Authorization 헤더에 유효한 JWT
  - When: doFilterInternal() 호출
  - Then: SecurityContext에 인증 정보 설정
  - Story: FR-2.1
  - Status: ⬜ Not Started

---

## Requirements Coverage

| Requirement ID | Test Cases | Status |
|---------------|------------|--------|
| FR-2.1 (관리자 인증) | TC-U3-001~005, 026~028, 039~041, 047~050 | ⬜ Pending |
| FR-2.2 (주문 모니터링) | TC-U3-006~012, 022~025, 029~032, 038, 042~043 | ⬜ Pending |
| FR-2.3 (테이블 관리) | TC-U3-013~021, 033~037, 044~046 | ⬜ Pending |

**Total Test Cases**: 50
