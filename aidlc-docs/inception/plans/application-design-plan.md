# Application Design Plan - 테이블오더 서비스

## Design Scope

테이블오더 서비스의 고수준 컴포넌트 식별 및 서비스 레이어 설계:
- Frontend (React): 고객용 UI + 관리자용 UI
- Backend (Spring Boot): REST API + SSE + 비즈니스 로직
- 컴포넌트 간 의존성 및 통신 패턴

---

## Design Questions

다음 질문에 답변하여 애플리케이션 설계 방향을 결정해주세요.

### Component Organization

#### Question 1
Frontend 컴포넌트 구조는 어떻게 구성하시겠습니까?

A) Feature-based (기능별 폴더 구조: /features/menu, /features/cart, /features/order)
B) Type-based (타입별 폴더 구조: /components, /pages, /services, /hooks)
C) Domain-based (도메인별 폴더 구조: /customer, /admin, /shared)
D) Other (please describe after [Answer]: tag below)

[Answer]: C

#### Question 2
Backend 패키지 구조는 어떻게 구성하시겠습니까?

A) Layered Architecture (controller, service, repository, domain 레이어 분리)
B) Feature-based (기능별 패키지: menu, order, table, admin)
C) Hexagonal Architecture (ports and adapters)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Service Layer Design

#### Question 3
Backend 서비스 레이어의 책임 범위는?

A) Thin services (단순 CRUD, 비즈니스 로직 최소화)
B) Rich services (복잡한 비즈니스 로직 포함, 트랜잭션 관리)
C) Domain-driven services (도메인 모델 중심, 풍부한 도메인 로직)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

#### Question 4
실시간 주문 업데이트 (SSE)는 어디에서 처리하시겠습니까?

A) 별도의 SSE 전용 컨트롤러 및 서비스
B) 기존 Order 서비스에 통합
C) 독립적인 Notification 서비스
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Component Dependencies

#### Question 5
Frontend에서 Backend API 호출은 어떻게 관리하시겠습니까?

A) 각 컴포넌트에서 직접 API 호출
B) 중앙화된 API 서비스 레이어 (예: /services/api/)
C) Custom hooks (예: useMenu, useOrder)
D) Redux/Context API + Thunk/Saga
E) Other (please describe after [Answer]: tag below)

[Answer]: C

#### Question 6
Frontend 상태 관리는 어떻게 하시겠습니까?

A) React Context API
B) Redux
C) Zustand
D) 로컬 상태만 사용 (useState, useReducer)
E) Other (please describe after [Answer]: tag below)

[Answer]: C

### Authentication & Authorization

#### Question 7
JWT 토큰 관리는 어디에서 하시겠습니까?

A) Frontend: LocalStorage, Backend: JWT 검증 필터
B) Frontend: SessionStorage, Backend: JWT 검증 필터
C) Frontend: Cookie (HttpOnly), Backend: JWT 검증 필터
D) Other (please describe after [Answer]: tag below)

[Answer]: B

#### Question 8
인증이 필요한 API 호출 시 JWT 토큰 첨부는?

A) API 서비스 레이어에서 자동으로 헤더에 추가
B) Axios/Fetch interceptor 사용
C) 각 API 호출마다 수동으로 추가
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Data Access Layer

#### Question 9
MyBatis Mapper 구조는 어떻게 구성하시겠습니까?

A) Entity별 Mapper (MenuMapper, OrderMapper, TableMapper 등)
B) 기능별 Mapper (CustomerMapper, AdminMapper)
C) 통합 Mapper (단일 Mapper에 모든 쿼리)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

#### Question 10
MyBatis XML 파일 위치는?

A) resources/mappers/ 디렉토리
B) 각 도메인 패키지 내부 (예: domain/menu/MenuMapper.xml)
C) resources/mybatis/ 디렉토리
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Error Handling

#### Question 11
Backend 에러 처리 전략은?

A) @ControllerAdvice + 전역 예외 핸들러
B) 각 컨트롤러에서 try-catch
C) Custom Exception + 전역 핸들러
D) Other (please describe after [Answer]: tag below)

[Answer]: A

#### Question 12
Frontend 에러 처리 전략은?

A) Error Boundary + 전역 에러 핸들러
B) 각 컴포넌트에서 try-catch
C) Custom hook (useErrorHandler)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Design Patterns

#### Question 13
Backend에서 DTO (Data Transfer Object) 사용 여부는?

A) Yes - Entity와 DTO 분리 (Request DTO, Response DTO)
B) No - Entity를 직접 반환
C) Partial - 일부 API만 DTO 사용
D) Other (please describe after [Answer]: tag below)

[Answer]: A

#### Question 14
Frontend에서 API 응답 데이터 타입 정의는?

A) TypeScript interface 사용
B) PropTypes 사용
C) 타입 정의 없음 (JavaScript)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Design Artifacts to Generate

설계 계획 승인 후 다음 아티팩트를 생성합니다:

### Mandatory Artifacts

- [ ] **components.md**: 컴포넌트 정의 및 책임
  - Frontend 컴포넌트 (고객용, 관리자용)
  - Backend 컴포넌트 (Controller, Service, Repository, Domain)
  - 각 컴포넌트의 목적 및 책임

- [ ] **component-methods.md**: 메서드 시그니처
  - 각 컴포넌트의 주요 메서드
  - 메서드 목적 및 입출력 타입
  - 상세 비즈니스 로직은 Functional Design에서 정의

- [ ] **services.md**: 서비스 정의 및 오케스트레이션
  - Backend 서비스 레이어 설계
  - 서비스 간 상호작용 패턴
  - 트랜잭션 경계

- [ ] **component-dependency.md**: 의존성 관계 및 통신 패턴
  - 컴포넌트 간 의존성 매트릭스
  - 데이터 흐름 다이어그램
  - API 엔드포인트 매핑

- [ ] **Validate design completeness and consistency**

---

## Instructions

1. 위의 모든 질문에 [Answer]: 태그 뒤에 선택한 옵션의 문자(A, B, C 등)를 입력해주세요.
2. 제공된 옵션이 맞지 않으면 마지막 옵션(Other)을 선택하고 설명을 추가해주세요.
3. 모든 질문에 답변 완료 후 알려주시면 설계 아티팩트를 생성하겠습니다.
