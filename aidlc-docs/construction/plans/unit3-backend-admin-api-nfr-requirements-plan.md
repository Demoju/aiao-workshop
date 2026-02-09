# NFR Requirements Plan - Unit 3 (Backend Admin API)

## Overview
이 계획은 Unit 3 (Backend Admin API)의 비기능 요구사항을 정의하고 기술 스택을 결정합니다.

## Functional Design Summary
- 관리자 인증 (JWT, 계정 잠금)
- 주문 관리 (조회, 상태 변경, 삭제)
- 테이블 세션 관리 (종료, 과거 내역)
- SSE 실시간 주문 업데이트

## NFR Assessment Questions

아래 질문들에 답변해주세요. 각 질문의 [Answer]: 태그 뒤에 선택한 옵션(A, B, C 등)을 입력하거나 "Other"를 선택하고 설명을 추가해주세요.

---

### Question 1: Expected Concurrent Users
예상되는 동시 관리자 사용자 수는?

A) 1-5명 (소규모 매장 1-2개)
B) 5-20명 (중규모 매장 여러 개)
C) 20-50명 (대규모 체인점)
D) 50명 이상
E) Other (please describe after [Answer]: tag below)

[Answer]: E, Only 1

---

### Question 2: API Response Time Target
API 응답 시간 목표는?

A) 100ms 이하 (매우 빠름)
B) 500ms 이하 (빠름)
C) 1초 이하 (보통)
D) 3초 이하 (느림)
E) 특별한 요구사항 없음
F) Other (please describe after [Answer]: tag below)

[Answer]: B

---

### Question 3: Database Connection Pool Size
데이터베이스 연결 풀 크기는?

A) 5-10 (소규모)
B) 10-20 (중규모)
C) 20-50 (대규모)
D) Auto-scaling (동적 조정)
E) Other (please describe after [Answer]: tag below)

[Answer]: E, 11

---

### Question 4: SSE Connection Limit
SSE 동시 연결 수 제한은?

A) 제한 없음
B) 매장당 10개
C) 매장당 50개
D) 전체 100개
E) Other (please describe after [Answer]: tag below)

[Answer]: E, 1개

---

### Question 5: Logging Level
로깅 레벨 및 전략은?

A) ERROR only (최소)
B) WARN + ERROR (기본)
C) INFO + WARN + ERROR (표준)
D) DEBUG + INFO + WARN + ERROR (상세)
E) Other (please describe after [Answer]: tag below)

[Answer]: D, 개발단계이기 때문.

---

### Question 6: Monitoring and Alerting
모니터링 및 알림 요구사항은?

A) 없음 (로그만)
B) 기본 헬스체크만
C) 메트릭 수집 (Prometheus, Grafana)
D) 풀스택 모니터링 (APM, 알림)
E) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 7: Data Backup Strategy
데이터 백업 전략은?

A) 백업 없음
B) 일일 백업
C) 실시간 복제 (Replication)
D) 일일 백업 + 실시간 복제
E) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 8: API Rate Limiting
API 요청 제한은?

A) 제한 없음
B) IP당 100 req/min
C) 사용자당 100 req/min
D) 전역 1000 req/min
E) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 9: CORS Configuration
CORS 설정은?

A) 모든 origin 허용 (개발용)
B) 특정 도메인만 허용
C) 동일 origin만 허용
D) CORS 비활성화
E) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 10: SSL/TLS Requirement
SSL/TLS 요구사항은?

A) 프로덕션 필수, 개발 선택
B) 모든 환경 필수
C) 선택 사항
D) 불필요
E) Other (please describe after [Answer]: tag below)

[Answer]: D

---

### Question 11: Error Handling Strategy
에러 처리 전략은?

A) 기본 Spring Boot 에러 핸들러
B) GlobalExceptionHandler (커스텀)
C) 에러 코드 체계 정의
D) Hybrid (GlobalExceptionHandler + 에러 코드)
E) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 12: Testing Requirements
테스트 요구사항은?

A) 테스트 없음
B) Unit 테스트만
C) Unit + Integration 테스트
D) Unit + Integration + E2E 테스트
E) Other (please describe after [Answer]: tag below)

[Answer]: B

---

## Execution Checklist

### Phase 1: Analyze Context
- [x] Read functional design artifacts
- [x] Understand business logic complexity

### Phase 2: Collect Answers
- [x] Wait for user to complete all [Answer]: tags
- [x] Review answers for ambiguities
- [x] Create clarification questions if needed

### Phase 3: Define NFR Requirements
- [x] Scalability requirements
- [x] Performance requirements
- [x] Availability requirements
- [x] Security requirements
- [x] Reliability requirements
- [x] Maintainability requirements
- [x] Create nfr-requirements.md

### Phase 4: Tech Stack Decisions
- [x] Backend framework selection
- [x] Database selection
- [x] Security framework
- [x] Testing framework
- [x] Build tool
- [x] Create tech-stack-decisions.md

### Phase 5: Approval
- [x] Present completion message
- [x] Wait for user approval
- [x] Update aidlc-state.md

---

**다음 단계**: 모든 질문에 답변을 완료하시면 알려주세요. 답변을 기반으로 NFR Requirements 문서를 생성하겠습니다.
