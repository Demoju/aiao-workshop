# NFR Design Plan - Unit 2 (Backend Customer API)

## Unit Overview
- **Unit Name**: Unit 2 - Backend Customer API
- **Purpose**: 고객용 REST API 제공
- **Technology**: Java Spring Boot, MyBatis, PostgreSQL

## Plan Execution Steps

### Step 1: Analyze NFR Requirements
- [x] Read NFR requirements artifacts
- [x] Understand performance, security, reliability needs
- [x] Identify design patterns needed

### Step 2: Create NFR Design Plan
- [x] Generate plan with checkboxes
- [x] Focus on design patterns and logical components
- [x] Include tech stack questions

### Step 3: Generate and Collect Answers
- [x] Create NFR design questions
- [x] Wait for user answers
- [x] Review for ambiguities
- [x] Resolve all ambiguities

### Step 4: Generate NFR Design Artifacts
- [x] Create nfr-design-patterns.md
- [x] Create logical-components.md

### Step 5: Present Completion and Wait for Approval
- [ ] Present completion message
- [ ] Wait for explicit user approval
- [ ] Handle change requests if any

### Step 6: Update Progress
- [ ] Log approval in audit.md
- [ ] Mark NFR Design complete in aidlc-state.md

---

## NFR Design Questions

Please answer the following questions to determine NFR design patterns and logical components for Unit 2 (Backend Customer API).

### Performance Patterns

## Question 1
Database 인덱스는 어떤 컬럼에 생성해야 하나요?

A) Primary Key만 (자동 생성)
B) Primary Key + Foreign Key (tableId, sessionId, menuId)
C) Primary Key + Foreign Key + 자주 조회되는 컬럼 (status, orderTime)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 2
주문 조회 시 성능 최적화는 어떻게 하나요?

A) 단순 쿼리 (인덱스만 사용)
B) JOIN 최적화 (OrderItem + Menu 한 번에 조회)
C) Pagination 추가 (LIMIT, OFFSET)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 3
Connection Pool 설정은 어떻게 하나요?

A) HikariCP 기본값 사용 (변경 없음)
B) 커스텀 설정 (minimum-idle=5, maximum-pool-size=10)
C) 환경별 설정 (개발/프로덕션 다르게)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Security Patterns

## Question 4
Session 저장소는 어디에 두나요?

A) In-Memory (서버 재시작 시 세션 소멸)
B) Database (TableSession 테이블)
C) Redis (빠른 조회, 확장 가능)
D) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 5
Session ID는 어떻게 전달하나요?

A) HTTP Header (X-Session-Id)
B) Cookie (HttpOnly, Secure)
C) Request Body (JSON)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 6
비밀번호 검증은 어디서 하나요?

A) Controller에서 직접
B) Service 레이어에서
C) Spring Security Filter에서
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 7
API 인증 필터는 어떻게 구현하나요?

A) Spring Security Filter Chain 사용
B) Interceptor 사용
C) @Aspect (AOP) 사용
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Reliability Patterns

## Question 8
Transaction 범위는 어떻게 설정하나요?

A) Service 메서드 레벨 (@Transactional)
B) Repository 메서드 레벨
C) Controller 메서드 레벨
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 9
에러 처리는 어떻게 하나요?

A) try-catch만 사용
B) @ControllerAdvice + Custom Exception
C) @ControllerAdvice + Custom Exception + 에러 코드 enum
D) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 10
로깅 전략은 어떻게 하나요?

A) 모든 메서드 진입/종료 로깅
B) 중요 비즈니스 이벤트만 로깅 (주문 생성, 취소)
C) 에러만 로깅
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 11
Logback 설정은 어떻게 하나요?

A) application.yml에 간단히 설정
B) logback-spring.xml로 상세 설정 (로테이션, 레벨)
C) 환경별 설정 파일 (logback-dev.xml, logback-prod.xml)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### API Design Patterns

## Question 12
RESTful API 설계 원칙은?

A) 기본 REST (GET, POST, DELETE)
B) REST + HTTP 상태 코드 표준화
C) REST + HTTP 상태 코드 + HATEOAS
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 13
DTO 변환은 어디서 하나요?

A) Controller에서 직접
B) Service에서
C) Mapper 클래스 (별도 레이어)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 14
에러 응답 포맷은?

A) 간단한 메시지만 (String)
B) 표준 포맷 (status, message, timestamp)
C) 표준 포맷 + 에러 코드 + path
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 15
API 버전 관리는?

A) 버전 관리 안 함 (MVP)
B) URL 경로 (/api/v1/...)
C) HTTP Header (Accept: application/vnd.api.v1+json)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Data Access Patterns

## Question 16
MyBatis Mapper는 어떻게 구성하나요?

A) XML만 사용
B) Annotation만 사용
C) XML + Annotation 혼용 (복잡한 쿼리는 XML)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 17
Dynamic SQL은 어떻게 처리하나요?

A) MyBatis <if>, <choose> 사용
B) Java 코드에서 동적 쿼리 생성
C) 사용 안 함 (정적 쿼리만)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 18
N+1 문제는 어떻게 해결하나요?

A) JOIN으로 한 번에 조회
B) Batch 쿼리 사용
C) 문제 없음 (단순 쿼리만 사용)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Testing Patterns

## Question 19
Unit 테스트 구조는?

A) Service 레이어만 테스트
B) Service + Repository 테스트
C) Service + Controller 테스트
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 20
Mock 전략은?

A) Mockito로 모든 의존성 Mock
B) 일부만 Mock (외부 서비스만)
C) Mock 최소화 (실제 객체 사용)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Validation Patterns

## Question 21
입력 검증은 어디서 하나요?

A) Controller에서만 (@Valid)
B) Controller + Service 레이어
C) Controller (@Valid) + Custom Validator
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 22
비즈니스 규칙 검증은 어디서 하나요?

A) Controller에서
B) Service 레이어에서
C) Domain 엔티티에서
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Idempotency Pattern

## Question 23
중복 요청 방지는 어떻게 하나요?

A) Idempotency Key + Redis
B) Idempotency Key + Database
C) 구현 안 함 (MVP)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 24
Idempotency Key는 어떻게 전달하나요?

A) HTTP Header (Idempotency-Key)
B) Request Body
C) Query Parameter
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Monitoring Patterns

## Question 25
Health Check는 무엇을 확인하나요?

A) Application 상태만
B) Application + Database 연결
C) Application + Database + 외부 서비스
D) Other (please describe after [Answer]: tag below)

[Answer]: B

---

## Instructions

1. Please answer each question by filling in the letter choice (A, B, C, D, etc.) after the [Answer]: tag
2. If none of the options match your needs, choose "Other" and describe your preference after the [Answer]: tag
3. Let me know when you've completed all questions by saying "done" or "completed"

