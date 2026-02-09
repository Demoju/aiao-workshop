# Functional Design Plan - Unit 1 (Frontend)

## Unit Context

**Unit Name**: Unit 1 - Frontend (Customer + Admin UI)

**Responsibilities**:
- Customer domain: 메뉴 조회, 장바구니 관리, 주문 생성/내역/취소, 테이블 로그인
- Admin domain: 관리자 로그인, 실시간 주문 모니터링 (SSE), 테이블 관리, 메뉴 조회
- Shared: 공통 컴포넌트, Custom hooks, API 서비스, 상태 관리

**Technology Stack**: React, TypeScript, Zustand, Axios

---

## Functional Design Questions

다음 질문에 답변하여 Frontend의 상세 비즈니스 로직을 설계해주세요.

### Business Logic Modeling

#### Question 1
장바구니 데이터 구조는 어떻게 설계하시겠습니까?

A) Array of CartItem (menuId, quantity만 저장)
B) Array of CartItem (전체 Menu 객체 + quantity 저장)
C) Map<menuId, CartItem> (빠른 조회를 위해)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

#### Question 2
장바구니에 동일한 메뉴를 추가할 때 동작은?

A) 수량 증가 (기존 항목의 quantity++)
B) 별도 항목 추가 (중복 허용)
C) 사용자에게 선택 옵션 제공
D) Other (please describe after [Answer]: tag below)

[Answer]: A

#### Question 3
주문 생성 시 장바구니 검증 로직은?

A) Frontend에서만 검증 (빈 장바구니, 수량 등)
B) Backend에서만 검증
C) Frontend + Backend 모두 검증
D) Other (please describe after [Answer]: tag below)

[Answer]: A

#### Question 4
SSE 연결이 끊어졌을 때 재연결 전략은?

A) 자동 재연결 (exponential backoff)
B) 사용자에게 알림 후 수동 재연결
C) 페이지 새로고침 유도
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Domain Model

#### Question 5
Frontend 상태 관리에서 Order 객체는 어떻게 저장하시겠습니까?

A) Array of Order (전체 주문 목록)
B) Map<orderId, Order> (빠른 조회)
C) Normalized state (orders, orderItems 분리)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

#### Question 6
Admin 대시보드에서 Table별 주문 그룹핑은 어디서 처리하시겠습니까?

A) Backend에서 그룹핑된 데이터 제공
B) Frontend에서 받은 주문 목록을 그룹핑
C) SSE 이벤트마다 Frontend에서 재그룹핑
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Business Rules

#### Question 7
주문 취소 가능 조건은 Frontend에서 어떻게 확인하시겠습니까?

A) Order 객체의 status 필드만 확인 (PENDING만 취소 가능)
B) Backend API 호출하여 확인
C) Order 객체에 canCancel 플래그 포함
D) Other (please describe after [Answer]: tag below)

[Answer]: B

#### Question 8
메뉴 이미지 로딩 실패 시 처리는?

A) Placeholder 이미지 표시
B) 이미지 없이 텍스트만 표시
C) 재시도 버튼 제공
D) Other (please describe after [Answer]: tag below)

[Answer]: A

#### Question 9
장바구니 최대 수량 제한은?

A) 제한 없음
B) 메뉴당 최대 수량 제한 (예: 99개)
C) 장바구니 전체 항목 수 제한
D) Other (please describe after [Answer]: tag below)

[Answer]: B, 99개

### Data Flow

#### Question 10
주문 생성 성공 후 UI 플로우는?

A) 주문 번호 표시 → 5초 후 자동 메뉴 화면 이동
B) 주문 번호 표시 → 사용자가 "확인" 버튼 클릭 시 이동
C) 즉시 메뉴 화면 이동 + Toast 알림
D) Other (please describe after [Answer]: tag below)

[Answer]: C, 주문이 완료되었습니다 라는 Toast 2초 띄워줘

#### Question 11
SSE로 받은 신규 주문을 Admin 대시보드에 어떻게 표시하시겠습니까?

A) 기존 목록 맨 위에 추가 + 애니메이션
B) 기존 목록 맨 아래에 추가
C) 해당 테이블 카드만 업데이트
D) Other (please describe after [Answer]: tag below)

[Answer]: A

#### Question 12
페이지 새로고침 시 복구해야 할 상태는?

A) 장바구니만 (LocalStorage)
B) 장바구니 + 로그인 정보 (SessionStorage)
C) 모든 상태 (Zustand persist)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Integration Points

#### Question 13
Backend API 호출 실패 시 재시도 로직은?

A) 재시도 없음 (에러 메시지만 표시)
B) 자동 재시도 (최대 3회)
C) 사용자에게 재시도 버튼 제공
D) Other (please describe after [Answer]: tag below)

[Answer]: B, 단, 3회까지 처리후 실패시 toast show

#### Question 14
JWT 토큰 만료 시 처리는?

A) 자동 로그아웃 + 로그인 페이지 이동
B) Refresh token으로 갱신
C) 사용자에게 알림 후 재로그인 유도
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Error Handling

#### Question 15
네트워크 에러 발생 시 사용자 피드백은?

A) Toast 알림
B) Modal 다이얼로그
C) 페이지 상단 배너
D) Other (please describe after [Answer]: tag below)

[Answer]: B

#### Question 16
ErrorBoundary에서 캐치한 에러 처리는?

A) 에러 페이지 표시 + 홈으로 이동 버튼
B) 에러 메시지만 표시
C) 페이지 새로고침 유도
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Business Scenarios

#### Question 17
고객이 주문 생성 중 네트워크가 끊어진 경우?

A) 장바구니 유지 + 에러 메시지 + 재시도 버튼
B) 장바구니 초기화 + 에러 메시지
C) 자동 재시도 (백그라운드)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

#### Question 18
Admin이 주문 상태를 변경하는 동안 다른 Admin이 같은 주문을 삭제한 경우?

A) 409 Conflict 에러 표시 + 목록 새로고침
B) 낙관적 업데이트 (UI 먼저 변경 후 에러 시 롤백)
C) 비관적 업데이트 (Backend 응답 후 UI 변경)
D) Other (please describe after [Answer]: tag below)

[Answer]: D, 어드민은 단 한 명뿐이므로 고려대상 아님

#### Question 19
고객이 주문 내역 조회 시 주문이 없는 경우?

A) "주문 내역이 없습니다" 메시지 표시
B) 빈 화면 (메시지 없음)
C) 메뉴 화면으로 리다이렉트
D) Other (please describe after [Answer]: tag below)

[Answer]: A

#### Question 20
Admin 대시보드에서 SSE 연결 전 기존 주문 목록은?

A) SSE 연결 전 REST API로 초기 데이터 로드
B) SSE 연결 후 이벤트로만 데이터 수신
C) 페이지 로드 시 빈 화면 (SSE 이벤트 대기)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Functional Design Artifacts to Generate

계획 승인 후 다음 아티팩트를 생성합니다:

### Mandatory Artifacts

- [ ] **business-logic-model.md**: 비즈니스 로직 모델
  - 장바구니 로직
  - 주문 생성 플로우
  - 주문 취소 로직
  - SSE 실시간 업데이트 로직
  - 상태 관리 로직

- [ ] **business-rules.md**: 비즈니스 규칙 및 검증
  - 장바구니 검증 규칙
  - 주문 검증 규칙
  - 주문 취소 가능 조건
  - 입력 검증 규칙

- [ ] **domain-entities.md**: 도메인 엔티티 (TypeScript interfaces)
  - Menu, Category
  - CartItem
  - Order, OrderItem
  - Table, TableSession
  - Admin, User

---

## Instructions

1. 위의 모든 질문에 [Answer]: 태그 뒤에 선택한 옵션의 문자(A, B, C 등)를 입력해주세요.
2. 제공된 옵션이 맞지 않으면 마지막 옵션(Other)을 선택하고 설명을 추가해주세요.
3. 모든 질문에 답변 완료 후 알려주시면 Functional Design 아티팩트를 생성하겠습니다.
