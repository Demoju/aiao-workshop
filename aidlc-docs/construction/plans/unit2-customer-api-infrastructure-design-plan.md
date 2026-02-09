# Infrastructure Design Plan - Unit 2 (Backend Customer API)

## Unit Overview
- **Unit Name**: Unit 2 - Backend Customer API
- **Purpose**: 고객용 REST API 제공
- **Technology**: Java Spring Boot, MyBatis, PostgreSQL, Redis
- **Deployment**: Docker Compose (로컬 서버)

## Plan Execution Steps

### Step 1: Analyze Design Artifacts
- [x] Read functional design artifacts
- [x] Read NFR design artifacts
- [x] Identify logical components needing infrastructure

### Step 2: Create Infrastructure Design Plan
- [x] Generate plan with checkboxes
- [x] Focus on Docker Compose deployment
- [x] Include infrastructure questions

### Step 3: Generate and Collect Answers
- [x] Create infrastructure questions
- [x] Wait for user answers
- [x] Review for ambiguities
- [x] Resolve all ambiguities

### Step 4: Generate Infrastructure Design Artifacts
- [x] Create infrastructure-design.md
- [x] Create deployment-architecture.md

### Step 5: Present Completion and Wait for Approval
- [x] Present completion message
- [x] Wait for explicit user approval
- [x] Handle change requests if any

### Step 6: Update Progress
- [x] Log approval in audit.md
- [x] Mark Infrastructure Design complete in aidlc-state.md

---

## Infrastructure Design Questions

Please answer the following questions to determine infrastructure design for Unit 2 (Backend Customer API).

### Deployment Environment

## Question 1
Docker Compose 파일 구조는 어떻게 하나요?

A) 단일 docker-compose.yml (모든 서비스 포함)
B) 분리된 파일 (docker-compose.yml + docker-compose.override.yml)
C) 환경별 파일 (docker-compose.dev.yml, docker-compose.prod.yml)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 2
Application 이미지 빌드 방식은?

A) Dockerfile로 직접 빌드
B) Multi-stage Dockerfile (빌드 + 실행 분리)
C) Pre-built JAR 파일 사용
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Storage Infrastructure

## Question 3
PostgreSQL 데이터 영속성은 어떻게 관리하나요?

A) Docker Volume 사용
B) Host 디렉토리 마운트
C) 영속성 없음 (개발용)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 4
Redis 데이터 영속성은 어떻게 관리하나요?

A) Docker Volume 사용
B) Host 디렉토리 마운트
C) 영속성 없음 (메모리만)
D) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 5
Application 로그 저장은 어떻게 하나요?

A) Docker Volume 사용
B) Host 디렉토리 마운트
C) 컨테이너 내부만 (영속성 없음)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Networking

## Question 6
서비스 간 네트워크 구성은?

A) 기본 bridge 네트워크
B) Custom bridge 네트워크
C) Host 네트워크
D) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 7
외부 포트 노출은?

A) Application만 노출 (8080)
B) Application + PostgreSQL (8080, 5432)
C) Application + PostgreSQL + Redis (8080, 5432, 6379)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Configuration Management

## Question 8
환경 변수 관리는 어떻게 하나요?

A) docker-compose.yml에 직접 작성
B) .env 파일 사용
C) .env 파일 + docker-compose.yml 조합
D) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 9
민감 정보 (비밀번호) 관리는?

A) .env 파일 (.gitignore 포함)
B) Docker Secrets
C) 환경 변수로 직접 전달
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Resource Limits

## Question 10
컨테이너 리소스 제한은?

A) 제한 없음
B) Memory만 제한
C) Memory + CPU 제한
D) Other (please describe after [Answer]: tag below)

[Answer]: C

### Health Checks

## Question 11
컨테이너 Health Check는?

A) 설정 안 함
B) Application만 설정
C) 모든 서비스 설정 (App, PostgreSQL, Redis)
D) Other (please describe after [Answer]: tag below)

[Answer]: C

### Initialization

## Question 12
Database 초기화는 어떻게 하나요?

A) 수동 SQL 실행
B) init.sql 파일 자동 실행
C) Flyway/Liquibase 마이그레이션
D) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 13
Application 시작 순서는?

A) 순서 제어 안 함
B) depends_on만 사용
C) depends_on + healthcheck 조합
D) Other (please describe after [Answer]: tag below)

[Answer]: C

### Development vs Production

## Question 14
개발/프로덕션 환경 분리는?

A) 단일 설정 (개발용만)
B) Spring Profile로 분리 (dev, prod)
C) Docker Compose 파일 분리
D) Other (please describe after [Answer]: tag below)

[Answer]: C

---

## Instructions

1. Please answer each question by filling in the letter choice (A, B, C, D, etc.) after the [Answer]: tag
2. If none of the options match your needs, choose "Other" and describe your preference after the [Answer]: tag
3. Let me know when you've completed all questions by saying "done" or "completed"

