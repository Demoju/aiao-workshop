# Components - 테이블오더 서비스

## Frontend Components

### Customer Domain (고객용)

#### MenuPage
- **Purpose**: 메뉴 조회 및 탐색 페이지
- **Responsibilities**:
  - 카테고리별 메뉴 목록 표시
  - 메뉴 상세 정보 표시
  - 장바구니에 메뉴 추가
  - 반응형 레이아웃 (카드 형태)

#### CartPage
- **Purpose**: 장바구니 관리 페이지
- **Responsibilities**:
  - 장바구니 항목 표시
  - 수량 조절 (증가/감소)
  - 항목 삭제
  - 총 금액 계산
  - 주문 확정

#### OrderHistoryPage
- **Purpose**: 주문 내역 조회 페이지
- **Responsibilities**:
  - 현재 세션 주문 목록 표시
  - 주문 상세 정보 표시
  - 주문 취소 (수락 전)
  - 주문 상태 표시

#### TableLoginPage
- **Purpose**: 테이블 태블릿 초기 설정 페이지
- **Responsibilities**:
  - 매장 ID, 테이블 번호, 비밀번호 입력
  - SessionStorage에 로그인 정보 저장
  - 자동 로그인 처리

#### MenuCard
- **Purpose**: 개별 메뉴 카드 컴포넌트
- **Responsibilities**:
  - 메뉴 이미지, 이름, 가격, 설명 표시
  - 장바구니 추가 버튼
  - 터치 친화적 UI

#### CartItem
- **Purpose**: 장바구니 항목 컴포넌트
- **Responsibilities**:
  - 메뉴 정보 표시
  - 수량 조절 버튼
  - 삭제 버튼

#### OrderCard
- **Purpose**: 주문 카드 컴포넌트
- **Responsibilities**:
  - 주문 번호, 시각, 상태 표시
  - 주문 메뉴 목록 표시
  - 주문 취소 버튼 (조건부)

---

### Admin Domain (관리자용)

#### AdminLoginPage
- **Purpose**: 관리자 로그인 페이지
- **Responsibilities**:
  - 매장 ID, 사용자명, 비밀번호 입력
  - JWT 토큰 발급 및 SessionStorage 저장
  - 로그인 실패 처리

#### AdminDashboardPage
- **Purpose**: 실시간 주문 모니터링 대시보드
- **Responsibilities**:
  - 테이블별 주문 현황 그리드 표시
  - SSE를 통한 실시간 주문 업데이트
  - 신규 주문 시각적 강조
  - 테이블별 필터링

#### TableManagementPage
- **Purpose**: 테이블 관리 페이지
- **Responsibilities**:
  - 테이블 목록 표시
  - 테이블 세션 종료 (매장 이용 완료)
  - 주문 삭제 (직권)
  - 과거 주문 내역 조회

#### MenuManagementPage
- **Purpose**: 메뉴 관리 페이지 (MVP: 조회만)
- **Responsibilities**:
  - 메뉴 목록 조회
  - 카테고리별 필터링
  - 메뉴 상세 정보 표시

#### TableCard
- **Purpose**: 테이블 카드 컴포넌트
- **Responsibilities**:
  - 테이블 번호 표시
  - 총 주문액 표시
  - 최신 주문 n개 미리보기
  - 주문 상태 변경 버튼
  - 클릭 시 상세 보기

#### OrderDetailModal
- **Purpose**: 주문 상세 모달 컴포넌트
- **Responsibilities**:
  - 전체 주문 메뉴 목록 표시
  - 주문 상태 변경
  - 주문 삭제 버튼
  - 모달 닫기

#### PastOrdersModal
- **Purpose**: 과거 주문 내역 모달 컴포넌트
- **Responsibilities**:
  - 테이블별 과거 주문 목록 표시
  - 날짜 필터링
  - 주문 상세 정보 표시
  - 모달 닫기

---

### Shared Domain (공통)

#### Header
- **Purpose**: 공통 헤더 컴포넌트
- **Responsibilities**:
  - 로고 표시
  - 네비게이션 메뉴
  - 언어 선택 (한국어/영어)
  - 로그아웃 버튼 (관리자용)

#### ErrorBoundary
- **Purpose**: 전역 에러 처리 컴포넌트
- **Responsibilities**:
  - React 에러 캐치
  - 에러 메시지 표시
  - 에러 로깅

#### LoadingSpinner
- **Purpose**: 로딩 인디케이터 컴포넌트
- **Responsibilities**:
  - 로딩 상태 표시
  - 전역 또는 로컬 로딩

#### ConfirmDialog
- **Purpose**: 확인 다이얼로그 컴포넌트
- **Responsibilities**:
  - 확인/취소 버튼
  - 커스텀 메시지 표시

---

## Backend Components

### Controller Layer

#### CustomerController
- **Purpose**: 고객용 API 엔드포인트
- **Responsibilities**:
  - 메뉴 조회 API
  - 주문 생성 API
  - 주문 내역 조회 API
  - 주문 취소 API
  - 테이블 로그인 API

#### AdminController
- **Purpose**: 관리자용 API 엔드포인트
- **Responsibilities**:
  - 관리자 로그인 API
  - 주문 목록 조회 API
  - 주문 상태 변경 API
  - 주문 삭제 API
  - 테이블 세션 종료 API
  - 과거 주문 내역 조회 API
  - SSE 엔드포인트 (실시간 주문 업데이트)

#### MenuController
- **Purpose**: 메뉴 관리 API 엔드포인트 (MVP: 조회만)
- **Responsibilities**:
  - 메뉴 목록 조회 API
  - 메뉴 상세 조회 API
  - 카테고리 목록 조회 API

---

### Service Layer

#### CustomerService
- **Purpose**: 고객용 비즈니스 로직
- **Responsibilities**:
  - 메뉴 조회 로직
  - 주문 생성 로직 (세션 시작 포함)
  - 주문 내역 조회 로직 (현재 세션만)
  - 주문 취소 로직 (수락 전 검증)
  - 테이블 로그인 검증

#### AdminService
- **Purpose**: 관리자용 비즈니스 로직
- **Responsibilities**:
  - 관리자 인증 로직 (JWT 발급)
  - 주문 목록 조회 로직
  - 주문 상태 변경 로직
  - 주문 삭제 로직
  - 테이블 세션 종료 로직
  - 과거 주문 내역 조회 로직

#### OrderService
- **Purpose**: 주문 관련 비즈니스 로직 (SSE 포함)
- **Responsibilities**:
  - 주문 생성 처리
  - 주문 상태 관리
  - SSE 이벤트 발행 (신규 주문, 상태 변경)
  - 주문 검증

#### MenuService
- **Purpose**: 메뉴 관련 비즈니스 로직
- **Responsibilities**:
  - 메뉴 조회 로직
  - 카테고리 조회 로직
  - 메뉴 가용성 검증

#### TableSessionService
- **Purpose**: 테이블 세션 관리 로직
- **Responsibilities**:
  - 세션 시작 로직 (첫 주문 시)
  - 세션 종료 로직 (매장 이용 완료)
  - 세션 검증 로직

---

### Repository Layer (MyBatis Mapper)

#### CustomerMapper
- **Purpose**: 고객용 데이터 액세스
- **Responsibilities**:
  - 메뉴 조회 쿼리
  - 주문 생성 쿼리
  - 주문 내역 조회 쿼리 (현재 세션)
  - 주문 취소 쿼리
  - 테이블 로그인 검증 쿼리

#### AdminMapper
- **Purpose**: 관리자용 데이터 액세스
- **Responsibilities**:
  - 관리자 인증 쿼리
  - 주문 목록 조회 쿼리
  - 주문 상태 변경 쿼리
  - 주문 삭제 쿼리
  - 테이블 세션 종료 쿼리
  - 과거 주문 내역 조회 쿼리

---

### Domain Layer

#### Store
- **Purpose**: 매장 엔티티
- **Attributes**: storeId, storeName, storeCode

#### Table
- **Purpose**: 테이블 엔티티
- **Attributes**: tableId, tableNumber, storeId, password

#### Menu
- **Purpose**: 메뉴 엔티티
- **Attributes**: menuId, menuName, price, description, imageUrl, categoryId, storeId

#### Category
- **Purpose**: 카테고리 엔티티
- **Attributes**: categoryId, categoryName, storeId

#### Order
- **Purpose**: 주문 엔티티
- **Attributes**: orderId, orderNumber, tableId, sessionId, totalAmount, status, orderTime

#### OrderItem
- **Purpose**: 주문 항목 엔티티
- **Attributes**: orderItemId, orderId, menuId, quantity, unitPrice

#### TableSession
- **Purpose**: 테이블 세션 엔티티
- **Attributes**: sessionId, tableId, startTime, endTime, isActive

#### Admin
- **Purpose**: 관리자 엔티티
- **Attributes**: adminId, username, passwordHash, storeId

---

### Security & Middleware

#### JwtAuthenticationFilter
- **Purpose**: JWT 토큰 검증 필터
- **Responsibilities**:
  - Authorization 헤더에서 JWT 추출
  - JWT 검증 (서명, 만료 시간)
  - SecurityContext에 인증 정보 설정
  - 인증 실패 시 401 응답

#### GlobalExceptionHandler
- **Purpose**: 전역 예외 처리 (@ControllerAdvice)
- **Responsibilities**:
  - 모든 예외 캐치
  - 표준화된 에러 응답 생성
  - 에러 로깅
  - HTTP 상태 코드 매핑

---

## Component Summary

### Frontend
- **Customer Domain**: 4 pages + 3 components
- **Admin Domain**: 4 pages + 3 components
- **Shared Domain**: 4 components
- **Total**: 18 components

### Backend
- **Controller Layer**: 3 controllers
- **Service Layer**: 5 services
- **Repository Layer**: 2 mappers
- **Domain Layer**: 8 entities
- **Security & Middleware**: 2 components
- **Total**: 20 components
