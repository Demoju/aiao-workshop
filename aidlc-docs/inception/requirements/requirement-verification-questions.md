# Requirements Verification Questions

요구사항을 명확히 하기 위한 검증 질문입니다. 각 질문에 대해 [Answer]: 태그 뒤에 선택한 옵션의 문자(A, B, C 등)를 입력해주세요. 제공된 옵션이 맞지 않으면 마지막 옵션(Other)을 선택하고 설명을 추가해주세요.

---

## 기술 스택 및 아키텍처

### Question 1
Backend 프레임워크는 무엇을 사용하시겠습니까?

A) Node.js (Express, Fastify, NestJS 등)
B) Python (FastAPI, Django, Flask 등)
C) Java (Spring Boot)
D) Go
E) Other (please describe after [Answer]: tag below)

[Answer]: C

### Question 2
Frontend 프레임워크는 무엇을 사용하시겠습니까?

A) React
B) Vue.js
C) Angular
D) Vanilla JavaScript (프레임워크 없음)
E) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 3
데이터베이스는 무엇을 사용하시겠습니까?

A) Relational DB (PostgreSQL, MySQL)
B) NoSQL Document DB (MongoDB, DynamoDB)
C) NoSQL Key-Value (Redis)
D) SQLite (간단한 로컬 DB)
E) Other (please describe after [Answer]: tag below)

[Answer]: A, PostGreSql 사용해줘.

### Question 4
실시간 주문 업데이트를 위한 통신 방식은?

A) Server-Sent Events (SSE) - 요구사항에 명시됨
B) WebSocket
C) Polling (주기적 요청)
D) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## 배포 및 인프라

### Question 5
배포 환경은 어디입니까?

A) AWS (EC2, ECS, Lambda 등)
B) 로컬 서버 (Docker Compose)
C) 다른 클라우드 (Azure, GCP)
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Question 6
컨테이너화를 사용하시겠습니까?

A) Yes - Docker 사용
B) No - 직접 설치
C) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## 인증 및 보안

### Question 7
관리자 인증 방식의 세부 구현은?

A) JWT 토큰 (요구사항에 명시됨)
B) Session Cookie
C) OAuth 2.0
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 8
테이블 태블릿 자동 로그인 정보 저장 방식은?

A) LocalStorage
B) SessionStorage
C) Cookie
D) IndexedDB
E) Other (please describe after [Answer]: tag below)

[Answer]: B

### Question 9
비밀번호 해싱 알고리즘은?

A) bcrypt (요구사항에 명시됨)
B) Argon2
C) PBKDF2
D) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## 데이터 모델 및 비즈니스 로직

### Question 10
메뉴 이미지는 어떻게 관리하시겠습니까?

A) 이미지 URL만 저장 (외부 호스팅)
B) 서버에 파일 업로드 및 저장
C) AWS S3 등 클라우드 스토리지
D) 이미지 기능 제외
E) Other (please describe after [Answer]: tag below)

[Answer]: C

### Question 11
주문 상태 변경은 누가 수행합니까?

A) 관리자만 수행 (요구사항 기준)
B) 자동 상태 변경 (타이머 기반)
C) 고객도 일부 변경 가능 (취소 등)
D) Other (please describe after [Answer]: tag below)

[Answer]: C, 주문 수락 전인 경우에는 고객이 주문 취소가 가능함.

### Question 12
테이블 세션 시작 시점은?

A) 첫 주문 생성 시 자동 시작 (요구사항 기준)
B) 관리자가 수동으로 시작
C) 고객이 로그인 시 시작
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 13
과거 주문 내역은 얼마나 보관하시겠습니까?

A) 영구 보관
B) 30일
C) 90일
D) 1년
E) Other (please describe after [Answer]: tag below)

[Answer]: D

---

## 성능 및 확장성

### Question 14
예상 동시 접속 테이블 수는?

A) 10개 이하 (소규모 매장)
B) 10-50개 (중규모 매장)
C) 50-100개 (대규모 매장)
D) 100개 이상 (체인점)
E) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 15
주문 처리 응답 시간 목표는?

A) 1초 이내
B) 2초 이내 (요구사항에 명시됨)
C) 5초 이내
D) 특별한 요구사항 없음
E) Other (please describe after [Answer]: tag below)

[Answer]: E, 3초 이내

---

## UI/UX 세부사항

### Question 16
고객용 UI는 어떤 디바이스를 주로 타겟으로 하나요?

A) 태블릿 (iPad 등)
B) 스마트폰
C) 데스크톱 브라우저
D) 모든 디바이스 (반응형)
E) Other (please describe after [Answer]: tag below)

[Answer]: D

### Question 17
관리자용 UI는 어떤 디바이스를 주로 타겟으로 하나요?

A) 데스크톱 브라우저
B) 태블릿
C) 스마트폰
D) 모든 디바이스 (반응형)
E) Other (please describe after [Answer]: tag below)

[Answer]: D

### Question 18
다국어 지원이 필요한가요?

A) No - 한국어만
B) Yes - 한국어 + 영어
C) Yes - 한국어 + 영어 + 기타
D) Other (please describe after [Answer]: tag below)

[Answer]: B

---

## 테스트 및 품질

### Question 19
테스트 코드 작성 방식은?

A) TDD (Test-Driven Development) - 테스트 우선
B) 표준 방식 - 코드 후 테스트 작성
C) 테스트 코드 작성 안 함
D) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 20
어떤 수준의 테스트가 필요한가요?

A) Unit 테스트만
B) Unit + Integration 테스트
C) Unit + Integration + E2E 테스트
D) 테스트 불필요
E) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## 추가 기능 및 우선순위

### Question 21
MVP에서 메뉴 관리 기능을 포함하시겠습니까?

A) Yes - 전체 CRUD 포함
B) Yes - 조회만 포함 (등록/수정/삭제는 나중에)
C) No - 하드코딩된 샘플 메뉴만
D) Other (please describe after [Answer]: tag below)

[Answer]: B

### Question 22
장바구니 데이터 지속성 요구사항은?

A) 페이지 새로고침 시에도 유지 (요구사항 명시)
B) 세션 동안만 유지
C) 브라우저 종료 후에도 유지
D) Other (please describe after [Answer]: tag below)

[Answer]: A

---

모든 질문에 답변해주시면 요구사항 문서를 생성하겠습니다.
