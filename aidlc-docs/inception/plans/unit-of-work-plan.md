# Unit of Work Plan - 테이블오더 서비스

## Decomposition Scope

테이블오더 서비스를 개발 가능한 작업 단위로 분해:
- Frontend (React): Customer domain + Admin domain
- Backend (Spring Boot): API + Business logic + Data access
- 각 Unit의 책임, 의존성, 개발 순서 정의

---

## Decomposition Questions

다음 질문에 답변하여 Unit of Work 분해 전략을 결정해주세요.

### Deployment Model

#### Question 1
Frontend와 Backend의 배포 모델은?

A) Monolithic - 단일 애플리케이션 (Frontend + Backend 통합)
B) Separated - Frontend와 Backend 별도 배포
C) Microservices - 여러 개의 독립적인 서비스
D) Other (please describe after [Answer]: tag below)

[Answer]: B

#### Question 2
Frontend를 여러 Unit으로 분리하시겠습니까?

A) Single Unit - 전체 Frontend를 하나의 Unit으로
B) Domain-based Units - Customer domain과 Admin domain을 별도 Unit으로
C) Feature-based Units - 기능별로 Unit 분리 (Menu, Cart, Order, Admin 등)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

#### Question 3
Backend를 여러 Unit으로 분리하시겠습니까?

A) Single Unit - 전체 Backend를 하나의 Unit으로
B) Domain-based Units - Customer API와 Admin API를 별도 Unit으로
C) Layer-based Units - Controller, Service, Repository를 별도 Unit으로
D) Feature-based Units - 기능별로 Unit 분리 (Menu, Order, Table, Admin 등)
E) Other (please describe after [Answer]: tag below)

[Answer]: B

### Development Strategy

#### Question 4
Unit 개발 순서는 어떻게 하시겠습니까?

A) Frontend-first - Frontend Unit 먼저 개발 후 Backend
B) Backend-first - Backend Unit 먼저 개발 후 Frontend
C) Parallel - Frontend와 Backend 동시 개발
D) Vertical slice - 기능별로 Frontend + Backend 함께 개발
E) Other (please describe after [Answer]: tag below)

[Answer]: C

#### Question 5
Unit 간 의존성 관리 전략은?

A) Sequential - 의존성 순서대로 순차 개발
B) Parallel with mocking - Mock 사용하여 병렬 개발
C) Contract-first - API 계약 먼저 정의 후 병렬 개발
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Code Organization (Greenfield)

#### Question 6
프로젝트 디렉토리 구조는?

A) Monorepo - 단일 저장소에 Frontend + Backend
B) Multi-repo - Frontend와 Backend 별도 저장소
C) Other (please describe after [Answer]: tag below)

[Answer]: A

#### Question 7
Monorepo를 선택한 경우, 디렉토리 구조는?

A) Root-level separation (frontend/, backend/)
B) Packages structure (packages/frontend, packages/backend)
C) Apps structure (apps/customer-ui, apps/admin-ui, apps/api)
D) Not applicable (Multi-repo 선택)
E) Other (please describe after [Answer]: tag below)

[Answer]: A

### Testing Strategy

#### Question 8
Unit별 테스트 범위는?

A) Unit tests only - 각 Unit의 Unit 테스트만
B) Unit + Integration tests - Unit 테스트 + Unit 간 통합 테스트
C) Full E2E tests - 전체 시스템 E2E 테스트 포함
D) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Unit Artifacts to Generate

계획 승인 후 다음 아티팩트를 생성합니다:

### Mandatory Artifacts

- [ ] **unit-of-work.md**: Unit 정의 및 책임
  - 각 Unit의 이름, 목적, 책임
  - Unit별 포함 컴포넌트
  - 개발 우선순위
  - Code organization strategy (Greenfield)

- [ ] **unit-of-work-dependency.md**: Unit 간 의존성
  - 의존성 매트릭스
  - 통합 포인트
  - 개발 순서

- [ ] **unit-of-work-story-map.md**: Requirements → Unit 매핑
  - 각 요구사항이 어느 Unit에 속하는지
  - Unit별 요구사항 목록

- [ ] **Validate unit boundaries and dependencies**

- [ ] **Ensure all requirements are assigned to units**

---

## Instructions

1. 위의 모든 질문에 [Answer]: 태그 뒤에 선택한 옵션의 문자(A, B, C 등)를 입력해주세요.
2. 제공된 옵션이 맞지 않으면 마지막 옵션(Other)을 선택하고 설명을 추가해주세요.
3. 모든 질문에 답변 완료 후 알려주시면 Unit of Work 아티팩트를 생성하겠습니다.
