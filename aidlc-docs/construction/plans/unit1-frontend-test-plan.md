# Test Plan - Unit 1 (Frontend)

## Unit Overview
- **Unit**: Unit 1 - Frontend
- **Tech**: React 18, TypeScript, Vitest, React Testing Library
- **Test Runner**: Vitest (Vite 네이티브)

---

## Store Layer Tests

### CartStore

- **TC-STORE-001**: addToCart - 새 메뉴 추가
  - Given: 빈 장바구니
  - When: addToCart(menu, 1) 호출
  - Then: items에 1개 항목, totalAmount = menu.price
  - Status: ⬜ Not Started

- **TC-STORE-002**: addToCart - 동일 메뉴 추가 시 수량 증가
  - Given: menu A가 1개 있는 장바구니
  - When: addToCart(menu A, 1) 호출
  - Then: items에 1개 항목, quantity = 2
  - Status: ⬜ Not Started

- **TC-STORE-003**: addToCart - 수량 99 초과 방지
  - Given: menu A가 99개 있는 장바구니
  - When: addToCart(menu A, 1) 호출
  - Then: quantity = 99 유지
  - Status: ⬜ Not Started

- **TC-STORE-004**: updateQuantity - 수량 변경
  - Given: menu A가 2개 있는 장바구니
  - When: updateQuantity(menuId, 5) 호출
  - Then: quantity = 5, totalAmount 재계산
  - Status: ⬜ Not Started

- **TC-STORE-005**: updateQuantity - 수량 0이면 삭제
  - Given: menu A가 있는 장바구니
  - When: updateQuantity(menuId, 0) 호출
  - Then: items에서 해당 항목 제거
  - Status: ⬜ Not Started

- **TC-STORE-006**: removeItem - 항목 삭제
  - Given: menu A, B가 있는 장바구니
  - When: removeItem(menuA.menuId) 호출
  - Then: menu A 제거, menu B만 남음
  - Status: ⬜ Not Started

- **TC-STORE-007**: clearCart - 장바구니 비우기
  - Given: 여러 항목이 있는 장바구니
  - When: clearCart() 호출
  - Then: items = [], totalAmount = 0
  - Status: ⬜ Not Started

### AuthStore

- **TC-STORE-008**: setAuth - 인증 정보 설정
  - Given: 미인증 상태
  - When: setAuth(user) 호출
  - Then: isAuthenticated = true, user 설정됨
  - Status: ⬜ Not Started

- **TC-STORE-009**: logout - 로그아웃
  - Given: 인증된 상태
  - When: logout() 호출
  - Then: isAuthenticated = false, user = null
  - Status: ⬜ Not Started

### OrderStore

- **TC-STORE-010**: setTables - 테이블 목록 설정
  - Given: 빈 OrderStore
  - When: setTables(tables) 호출
  - Then: tables 배열 설정됨
  - Status: ⬜ Not Started

- **TC-STORE-011**: addOrder - 신규 주문 추가
  - Given: 테이블 1에 주문 1개 있음
  - When: addOrder(newOrder for table 1) 호출
  - Then: 테이블 1의 orders에 추가, totalAmount 업데이트
  - Status: ⬜ Not Started

- **TC-STORE-012**: updateOrderStatus - 주문 상태 변경
  - Given: PENDING 상태 주문 존재
  - When: updateOrderStatus(orderId, PREPARING) 호출
  - Then: 해당 주문 status = PREPARING
  - Status: ⬜ Not Started

- **TC-STORE-013**: removeOrder - 주문 삭제
  - Given: 테이블에 주문 2개 존재
  - When: removeOrder(orderId) 호출
  - Then: 해당 주문 제거, totalAmount 재계산
  - Status: ⬜ Not Started

### LoadingStore

- **TC-STORE-014**: setLoading - 로딩 상태 토글
  - Given: isLoading = false
  - When: setLoading(true) 호출
  - Then: isLoading = true
  - Status: ⬜ Not Started

---

## Service Layer Tests

### customerApi

- **TC-API-001**: getMenus - 메뉴 목록 조회 성공
  - Given: Mock API 설정
  - When: getMenus(storeId) 호출
  - Then: Menu[] 반환
  - Status: ⬜ Not Started

- **TC-API-002**: createOrder - 주문 생성 성공
  - Given: Mock API 설정
  - When: createOrder(dto) 호출
  - Then: Order 반환
  - Status: ⬜ Not Started

- **TC-API-003**: cancelOrder - 주문 취소 성공
  - Given: Mock API 설정
  - When: cancelOrder(orderId) 호출
  - Then: 성공 (void)
  - Status: ⬜ Not Started

- **TC-API-004**: tableLogin - 테이블 로그인 성공
  - Given: Mock API 설정
  - When: tableLogin(dto) 호출
  - Then: TableLoginResponseDto 반환
  - Status: ⬜ Not Started

### adminApi

- **TC-API-005**: adminLogin - 관리자 로그인 성공
  - Given: Mock API 설정
  - When: adminLogin(dto) 호출
  - Then: AdminLoginResponseDto 반환
  - Status: ⬜ Not Started

- **TC-API-006**: updateOrderStatus - 주문 상태 변경 성공
  - Given: Mock API 설정
  - When: updateOrderStatus(orderId, dto) 호출
  - Then: 성공 (void)
  - Status: ⬜ Not Started

### api (Axios interceptor)

- **TC-API-007**: Request interceptor - JWT 토큰 첨부
  - Given: AuthStore에 token 존재
  - When: API 요청 발생
  - Then: Authorization 헤더에 Bearer token 포함
  - Status: ⬜ Not Started

- **TC-API-008**: Response interceptor - 401 에러 시 logout
  - Given: API 응답 401
  - When: interceptor 처리
  - Then: logout 호출
  - Status: ⬜ Not Started

---

## Hook Layer Tests

### useCart

- **TC-HOOK-001**: useCart - CartStore 연동 확인
  - Given: CartStore에 items 존재
  - When: useCart() 호출
  - Then: items, totalAmount 반환
  - Status: ⬜ Not Started

### useAuth

- **TC-HOOK-002**: useAuth - 테이블 로그인 플로우
  - Given: 미인증 상태
  - When: login(tableLoginDto) 호출
  - Then: API 호출 → AuthStore 업데이트
  - Status: ⬜ Not Started

- **TC-HOOK-003**: useAuth - 관리자 로그인 플로우
  - Given: 미인증 상태
  - When: login(adminLoginDto) 호출
  - Then: API 호출 → AuthStore 업데이트 (token 포함)
  - Status: ⬜ Not Started

### useOrderStream

- **TC-HOOK-004**: useOrderStream - SSE 연결 및 이벤트 처리
  - Given: SSE Mock 설정
  - When: useOrderStream(token) 호출
  - Then: isConnected = true
  - Status: ⬜ Not Started

- **TC-HOOK-005**: useOrderStream - 재연결 (max 3회)
  - Given: SSE 연결 실패
  - When: 자동 재연결 시도
  - Then: 최대 3회 시도 후 error 상태
  - Status: ⬜ Not Started

---

## Component Layer Tests

### MenuCard

- **TC-COMP-001**: MenuCard - 메뉴 정보 렌더링
  - Given: menu props
  - When: 렌더링
  - Then: menuName, price, description 표시
  - Status: ⬜ Not Started

- **TC-COMP-002**: MenuCard - 장바구니 추가 버튼 클릭
  - Given: menu props, onAddToCart mock
  - When: 추가 버튼 클릭
  - Then: onAddToCart(menu, 1) 호출
  - Status: ⬜ Not Started

### CartItem Component

- **TC-COMP-003**: CartItem - 수량 증가/감소 버튼
  - Given: cartItem props
  - When: +/- 버튼 클릭
  - Then: onUpdateQuantity 호출
  - Status: ⬜ Not Started

### OrderCard

- **TC-COMP-004**: OrderCard - 주문 정보 렌더링
  - Given: order props
  - When: 렌더링
  - Then: orderNumber, status, totalAmount 표시
  - Status: ⬜ Not Started

---

## Validation Tests

### Zod Schemas

- **TC-VAL-001**: tableLoginSchema - 유효한 입력 통과
  - Given: { storeId: 1, tableNumber: "1", password: "pass" }
  - When: parse
  - Then: 성공
  - Status: ⬜ Not Started

- **TC-VAL-002**: tableLoginSchema - 빈 tableNumber 실패
  - Given: { storeId: 1, tableNumber: "", password: "pass" }
  - When: parse
  - Then: ZodError
  - Status: ⬜ Not Started

- **TC-VAL-003**: adminLoginSchema - 유효한 입력 통과
  - Given: { storeId: 1, username: "admin", password: "pass" }
  - When: parse
  - Then: 성공
  - Status: ⬜ Not Started

---

## Requirements Coverage

| Requirement | Test Cases | Status |
|------------|------------|--------|
| BR-CART-001~005 | TC-STORE-001~007 | ⬜ Pending |
| BR-ORDER-001 | TC-API-002 | ⬜ Pending |
| BR-AUTH-001~004 | TC-STORE-008~009, TC-HOOK-002~003, TC-API-007~008 | ⬜ Pending |
| BR-SSE-001~003 | TC-HOOK-004~005 | ⬜ Pending |
| BR-INPUT-001~003 | TC-VAL-001~003 | ⬜ Pending |

**Total Test Cases**: 30
