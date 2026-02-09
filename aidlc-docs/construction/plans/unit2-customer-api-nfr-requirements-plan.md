# NFR Requirements Plan - Unit 2 (Backend Customer API)

## Unit Overview
- **Unit Name**: Unit 2 - Backend Customer API
- **Purpose**: 고객용 REST API 제공
- **Technology**: Java Spring Boot, MyBatis, PostgreSQL

## Plan Execution Steps

### Step 1: Analyze Functional Design
- [x] Read functional design artifacts
- [x] Understand business logic complexity
- [x] Identify NFR requirements areas

### Step 2: Create NFR Requirements Plan
- [x] Generate plan with checkboxes
- [x] Focus on scalability, performance, security
- [x] Include tech stack questions

### Step 3: Generate and Collect Answers
- [x] Create NFR questions
- [x] Wait for user answers
- [x] Review for ambiguities
- [x] Resolve all ambiguities

### Step 4: Generate NFR Requirements Artifacts
- [x] Create nfr-requirements.md
- [x] Create tech-stack-decisions.md

### Step 5: Present Completion and Wait for Approval
- [ ] Present completion message
- [ ] Wait for explicit user approval
- [ ] Handle change requests if any

### Step 6: Update Progress
- [ ] Log approval in audit.md
- [ ] Mark NFR Requirements complete in aidlc-state.md

---

## NFR Requirements Questions

Please answer the following questions to determine non-functional requirements for Unit 2 (Backend Customer API).

### Performance Requirements

## Question 1
API 응답 시간 목표는 어떻게 설정해야 하나요?

A) 1초 이내 (매우 빠름)
B) 3초 이내 (일반적)
C) 5초 이내 (여유)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 2
Database 쿼리 최적화가 필요한가요?

A) 필요 없음 (단순 쿼리만)
B) 필요함 (인덱스 설계)
C) 필요함 (인덱스 + 쿼리 튜닝)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 3
Connection Pool 설정은 어떻게 해야 하나요?

A) 기본 설정 사용 (HikariCP 기본값)
B) 커스텀 설정 (최소/최대 연결 수 지정)
C) 동적 조정 (부하에 따라 자동 조정)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Scalability Requirements

## Question 4
예상 동시 요청 수는 얼마나 되나요?

A) 10개 이하 (소규모)
B) 10-50개 (중소규모)
C) 50-100개 (중규모)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 5
수평 확장(Scale-out)이 필요한가요?

A) 필요 없음 (단일 서버)
B) 필요함 (로드 밸런서 + 다중 서버)
C) 미래 고려 (현재는 단일, 나중에 확장)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Availability Requirements

## Question 6
서비스 가용성 목표는 어떻게 설정해야 하나요?

A) Best effort (로컬 배포, 가용성 보장 안 함)
B) 99% (연간 3.65일 다운타임 허용)
C) 99.9% (연간 8.76시간 다운타임 허용)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 7
Health Check 엔드포인트가 필요한가요?

A) 필요 없음
B) 필요함 (기본 health check)
C) 필요함 (상세 health check - DB, 외부 서비스)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Security Requirements

## Question 8
API 인증 방식은 무엇을 사용하나요?

A) 인증 없음 (테이블 로그인만)
B) JWT (관리자용만)
C) JWT (관리자) + Session (고객용)
D) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 9
HTTPS는 어떻게 적용하나요?

A) 개발 환경에서는 HTTP, 프로덕션에서만 HTTPS
B) 모든 환경에서 HTTPS
C) Reverse Proxy (Nginx)에서 HTTPS 처리
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 10
SQL Injection 방지는 어떻게 하나요?

A) MyBatis Prepared Statement 사용
B) MyBatis + 입력 검증
C) MyBatis + 입력 검증 + ORM 레벨 보호
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 11
비밀번호 해싱 알고리즘은 무엇을 사용하나요?

A) bcrypt (권장)
B) SHA-256
C) PBKDF2
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Reliability Requirements

## Question 12
Transaction 관리는 어떻게 하나요?

A) @Transactional 어노테이션 사용
B) 수동 Transaction 관리
C) @Transactional + 격리 수준 설정
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 13
에러 로깅은 어떻게 하나요?

A) System.out.println (개발용)
B) SLF4J + Logback
C) SLF4J + Logback + 파일 로테이션
D) Other (please describe after [Answer]: tag below)

[Answer]: C, 1년 마다 삭제

## Question 14
Database 백업 전략은 무엇인가요?

A) 백업 없음 (로컬 개발)
B) 수동 백업
C) 자동 백업 (일일)
D) Other (please describe after [Answer]: tag below)

[Answer]: C

### Maintainability Requirements

## Question 15
코드 품질 도구는 무엇을 사용하나요?

A) 없음
B) Checkstyle
C) Checkstyle + SpotBugs
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 16
API 문서화는 어떻게 하나요?

A) 수동 문서 (Markdown)
B) Swagger/OpenAPI
C) Swagger + Postman Collection
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 17
테스트 전략은 무엇인가요?

A) Unit 테스트만
B) Unit + Integration 테스트
C) Unit + Integration + E2E 테스트
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Tech Stack Selection

## Question 18
Java 버전은 무엇을 사용하나요?

A) Java 11 (LTS)
B) Java 17 (LTS, 권장)
C) Java 21 (최신 LTS)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 19
Spring Boot 버전은 무엇을 사용하나요?

A) Spring Boot 2.x
B) Spring Boot 3.x (권장)
C) 최신 버전
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 20
Build Tool은 무엇을 사용하나요?

A) Maven
B) Gradle
C) Gradle (Kotlin DSL)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 21
Database Connection Pool은 무엇을 사용하나요?

A) HikariCP (Spring Boot 기본)
B) Apache DBCP
C) Tomcat JDBC Pool
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 22
Validation 라이브러리는 무엇을 사용하나요?

A) Spring Validation (javax.validation)
B) Hibernate Validator
C) Custom Validation
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 23
JSON 라이브러리는 무엇을 사용하나요?

A) Jackson (Spring Boot 기본)
B) Gson
C) JSON-B
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 24
Lombok 사용 여부는?

A) 사용함 (권장)
B) 사용 안 함
C) 일부만 사용 (@Data, @Builder 등)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Instructions

1. Please answer each question by filling in the letter choice (A, B, C, D, etc.) after the [Answer]: tag
2. If none of the options match your needs, choose "Other" and describe your preference after the [Answer]: tag
3. Let me know when you've completed all questions by saying "done" or "completed"

