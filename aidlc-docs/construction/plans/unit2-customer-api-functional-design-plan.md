# Functional Design Plan - Unit 2 (Backend Customer API)

## Unit Overview
- **Unit Name**: Unit 2 - Backend Customer API
- **Purpose**: 고객용 REST API 제공
- **Technology**: Java Spring Boot, MyBatis, PostgreSQL

## Plan Execution Steps

### Step 1: Analyze Unit Context
- [x] Read unit definition from unit-of-work.md
- [x] Read component definitions from components.md
- [x] Read component methods from component-methods.md
- [x] Understand Unit 2 responsibilities and boundaries

### Step 2: Generate Context-Appropriate Questions
- [x] Create functional design questions file
- [x] Focus on business logic, domain models, business rules
- [x] Use multiple choice format with [Answer]: tags

### Step 3: Collect and Analyze User Answers
- [x] Wait for user to complete all questions
- [x] Review answers for ambiguities
- [x] Create clarification questions if needed
- [x] Resolve all ambiguities before proceeding

### Step 4: Generate Functional Design Artifacts
- [x] Create business-logic-model.md
- [x] Create business-rules.md
- [x] Create domain-entities.md

### Step 5: Present Completion and Wait for Approval
- [ ] Present completion message
- [ ] Wait for explicit user approval
- [ ] Handle change requests if any

### Step 6: Update Progress
- [ ] Log approval in audit.md
- [ ] Mark Functional Design complete in aidlc-state.md

---

## Functional Design Questions

Please answer the following questions to help design the functional aspects of Unit 2 (Backend Customer API).

### Business Logic Modeling

## Question 1
테이블 로그인 시 비밀번호 검증은 어떻게 처리해야 하나요?

A) 평문 비교 (테스트 환경용)
B) bcrypt 해싱 비교
C) 해싱 불필요 (테이블 비밀번호는 단순 PIN)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 2
주문 생성 시 테이블 세션이 없으면 어떻게 처리해야 하나요?

A) 에러 반환 (세션 먼저 생성 필요)
B) 자동으로 새 세션 생성 후 주문 생성
C) 세션 없이 주문만 생성
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 3
주문 생성 시 장바구니 데이터는 어떻게 전달되나요?

A) 메뉴 ID + 수량 배열만 전달
B) 전체 메뉴 객체 + 수량 배열 전달
C) 메뉴 ID + 수량 + 단가 배열 전달 (가격 검증용)
D) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 4
주문 번호(orderNumber)는 어떻게 생성해야 하나요?

A) 자동 증가 숫자 (1, 2, 3, ...)
B) UUID
C) 날짜 + 순번 (ORD-20260209-001)
D) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 5
주문 취소 시 어떤 검증이 필요한가요?

A) 주문 존재 여부만 확인
B) 주문 존재 + 주문 상태가 PENDING인지 확인
C) 주문 존재 + 주문 상태 + 테이블 세션 일치 확인
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Domain Model

## Question 6
Order 엔티티에 tableId와 sessionId를 모두 저장해야 하나요?

A) tableId만 저장 (sessionId 불필요)
B) sessionId만 저장 (tableId는 session에서 조회)
C) tableId와 sessionId 모두 저장
D) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 7
OrderItem 엔티티에 unitPrice (단가)를 저장해야 하나요?

A) 저장 필요 (주문 시점 가격 기록)
B) 저장 불필요 (Menu 테이블에서 조회)
C) 저장 필요 (메뉴 가격 변경 시 과거 주문 보호)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 8
Menu 엔티티에 isAvailable (판매 가능 여부) 필드가 필요한가요?

A) 필요 없음 (모든 메뉴 항상 판매)
B) 필요함 (품절 처리용)
C) 필요함 (영업 시간 외 판매 제한용)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Business Rules

## Question 9
주문 생성 시 최소 주문 금액 제한이 있나요?

A) 제한 없음
B) 최소 5,000원
C) 최소 10,000원
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 10
주문 생성 시 메뉴 수량 제한이 있나요?

A) 제한 없음
B) 메뉴당 최대 99개
C) 주문당 총 수량 최대 100개
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 11
주문 취소 가능한 상태는 어떻게 정의해야 하나요?

A) PENDING 상태만 취소 가능
B) PENDING, PREPARING 상태 취소 가능
C) COMPLETED 제외 모든 상태 취소 가능
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 12
동일한 메뉴를 여러 번 장바구니에 추가할 때 어떻게 처리해야 하나요?

A) 별도 OrderItem으로 생성 (메뉴 ID 중복 허용)
B) 수량 합산 (메뉴 ID 중복 불가)
C) Frontend에서 처리 (Backend는 관여 안 함)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Data Flow

## Question 13
메뉴 조회 API는 어떤 정보를 반환해야 하나요?

A) 메뉴 기본 정보만 (ID, 이름, 가격)
B) 메뉴 기본 정보 + 이미지 URL
C) 메뉴 기본 정보 + 이미지 URL + 카테고리 정보
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 14
주문 내역 조회 시 OrderItem 정보를 어떻게 반환해야 하나요?

A) OrderItem ID + 메뉴 ID + 수량만
B) OrderItem ID + 메뉴 ID + 수량 + 단가
C) OrderItem ID + 전체 메뉴 정보 + 수량 + 단가
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 15
주문 생성 API 응답에 어떤 정보를 포함해야 하나요?

A) 주문 ID만
B) 주문 ID + 주문 번호
C) 주문 ID + 주문 번호 + 주문 시각 + 총 금액
D) Other (please describe after [Answer]: tag below)

[Answer]: C

### Integration Points

## Question 16
CustomerService에서 OrderService의 메서드를 호출해야 하나요?

A) 호출 필요 없음 (CustomerService에서 모든 로직 처리)
B) 호출 필요 (주문 생성 로직 공유)
C) 호출 필요 (주문 검증 로직 공유)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 17
MenuService는 CustomerService와 AdminService에서 공유되나요?

A) 공유됨 (동일한 MenuService 사용)
B) 공유 안 됨 (각자 별도 Service)
C) 일부만 공유 (조회 로직만 공유)
D) Other (please describe after [Answer]: tag below)

[Answer]: C

### Error Handling

## Question 18
존재하지 않는 메뉴 ID로 주문 생성 시 어떻게 처리해야 하나요?

A) 400 Bad Request + 에러 메시지
B) 404 Not Found + 에러 메시지
C) 해당 메뉴만 제외하고 주문 생성
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 19
테이블 로그인 실패 시 어떤 에러 정보를 반환해야 하나요?

A) 401 Unauthorized만
B) 401 Unauthorized + "로그인 실패" 메시지
C) 401 Unauthorized + 구체적 실패 이유 (테이블 없음 / 비밀번호 틀림)
D) Other (please describe after [Answer]: tag below)

[Answer]: C

### Business Scenarios

## Question 20
주문 생성 중 네트워크 오류로 중복 요청이 발생할 수 있습니다. 어떻게 처리해야 하나요?

A) 중복 요청 모두 처리 (중복 주문 생성)
B) Idempotency Key 사용하여 중복 방지
C) 중복 요청 감지 및 에러 반환
D) Other (please describe after [Answer]: tag below)

[Answer]: B

---

## Instructions

1. Please answer each question by filling in the letter choice (A, B, C, D, etc.) after the [Answer]: tag
2. If none of the options match your needs, choose "Other" and describe your preference after the [Answer]: tag
3. Let me know when you've completed all questions by saying "done" or "completed"

