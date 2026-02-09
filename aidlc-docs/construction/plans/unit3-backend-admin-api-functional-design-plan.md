# Functional Design Plan - Unit 3 (Backend Admin API)

## Overview
이 계획은 Unit 3 (Backend Admin API)의 상세 비즈니스 로직을 설계합니다.

## Unit 3 Responsibilities
- 관리자 로그인 API (JWT 발급)
- 주문 목록 조회 API
- 주문 상태 변경 API
- 주문 삭제 API
- 테이블 세션 종료 API
- 과거 주문 내역 조회 API
- 실시간 주문 업데이트 SSE 엔드포인트

## Design Questions

아래 질문들에 답변해주세요. 각 질문의 [Answer]: 태그 뒤에 선택한 옵션(A, B, C 등)을 입력하거나 "Other"를 선택하고 설명을 추가해주세요.

---

### Question 1: Admin Authentication Strategy
관리자 인증 시 비밀번호 검증 실패 처리는?

A) 즉시 에러 반환 (보안 위험: 계정 존재 여부 노출)
B) 고정 시간 대기 후 에러 반환 (타이밍 공격 방지)
C) 로그인 시도 횟수 제한 (5회 실패 시 계정 잠금)
D) Hybrid (고정 시간 대기 + 시도 횟수 제한)
E) Other (please describe after [Answer]: tag below)

[Answer]: C, 5회

---

### Question 2: JWT Token Expiration Strategy
JWT 토큰 만료 시간 및 갱신 전략은?

A) 16시간 만료, 갱신 없음 (요구사항)
B) 16시간 만료, Refresh Token 제공
C) 짧은 Access Token (1시간) + Refresh Token (16시간)
D) Sliding expiration (활동 시 자동 연장)
E) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 3: Order Status Transition Rules
주문 상태 변경 시 허용되는 전환 규칙은?

A) 모든 상태 간 자유 전환 가능
B) 순차적 전환만 허용 (대기중 → 준비중 → 완료)
C) 일부 역방향 허용 (준비중 → 대기중 가능, 완료 → 준비중 불가)
D) 상태별 세부 규칙 정의 필요
E) Other (please describe after [Answer]: tag below)

[Answer]: B, 취소는 언제든 가능.

---

### Question 4: Order Deletion Business Rules
주문 삭제 시 비즈니스 규칙은?

A) 모든 상태의 주문 삭제 가능
B) 완료된 주문만 삭제 불가
C) 대기중 주문만 삭제 가능
D) Soft delete (논리 삭제, 데이터 보존)
E) Hard delete (물리 삭제, 데이터 영구 삭제)
F) Other (please describe after [Answer]: tag below)

[Answer]: F, 취소, 완료 상태만 주문 삭제 가능.

---

### Question 5: Table Session End Logic
테이블 세션 종료 시 처리 로직은?

A) 세션 종료만 처리 (주문은 그대로)
B) 세션 종료 + 미완료 주문 자동 완료 처리
C) 세션 종료 + 모든 주문 과거 이력으로 이동
D) 세션 종료 전 미완료 주문 확인 및 경고
E) Other (please describe after [Answer]: tag below)

[Answer]: D

---

### Question 6: Past Orders Query Strategy
과거 주문 내역 조회 시 데이터 범위는?

A) 모든 과거 주문 (페이지네이션)
B) 최근 30일 주문만
C) 날짜 범위 필터 필수 (시작일, 종료일)
D) 테이블별 최근 N개 세션
E) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 7: SSE Connection Management
SSE 연결 관리 전략은?

A) 무제한 연결 유지
B) 타임아웃 설정 (30분 후 자동 종료)
C) Heartbeat 전송 (30초마다 keep-alive)
D) 연결 수 제한 (매장당 최대 N개)
E) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 8: SSE Event Publishing Strategy
SSE 이벤트 발행 시점은?

A) 새 주문 생성 시만
B) 주문 생성 + 상태 변경 시
C) 주문 생성 + 상태 변경 + 삭제 시
D) 모든 주문 관련 변경 시 (생성, 수정, 삭제, 취소)
E) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 9: Concurrent Order Modification Handling
동시에 같은 주문을 수정하려는 경우 처리는?

A) Last-write-wins (마지막 요청 승리)
B) Optimistic locking (버전 체크)
C) Pessimistic locking (DB row lock)
D) 동시 수정 불가 (에러 반환)
E) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 10: Admin Authorization Model
관리자 권한 모델은?

A) 단일 관리자 (권한 구분 없음)
B) 역할 기반 (ROLE_ADMIN, ROLE_MANAGER)
C) 매장별 관리자 (매장 ID로 데이터 격리)
D) 세밀한 권한 (주문 조회, 수정, 삭제 각각 권한)
E) Other (please describe after [Answer]: tag below)

[Answer]: A

---

### Question 11: Order List Query Optimization
주문 목록 조회 시 성능 최적화 전략은?

A) 전체 주문 조회 (페이지네이션)
B) 현재 세션 주문만 조회 (완료된 세션 제외)
C) 테이블별 그룹화 + 최근 N개 주문만
D) 인덱스 최적화 + 캐싱
E) Other (please describe after [Answer]: tag below)

[Answer]: B

---

### Question 12: Error Response Format
API 에러 응답 형식은?

A) 간단한 메시지 (String)
B) 구조화된 에러 (code, message)
C) 상세 에러 (code, message, timestamp, path)
D) RFC 7807 Problem Details
E) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## Execution Checklist

### Phase 1: Analyze Context
- [x] Read unit definition from unit-of-work.md
- [x] Read assigned requirements from unit-of-work-story-map.md
- [x] Understand unit responsibilities

### Phase 2: Collect Answers
- [x] Wait for user to complete all [Answer]: tags
- [x] Review answers for ambiguities
- [x] Create clarification questions if needed

### Phase 3: Design Domain Model
- [x] Define Admin entity
- [x] Define Order entity (shared)
- [x] Define TableSession entity (shared)
- [x] Define entity relationships
- [x] Create domain-entities.md

### Phase 4: Design Business Logic
- [x] Admin authentication flow
- [x] Order management logic
- [x] Table session management logic
- [x] SSE event publishing logic
- [x] Create business-logic-model.md

### Phase 5: Define Business Rules
- [x] Authentication rules
- [x] Order state transition rules
- [x] Order deletion rules
- [x] Session end rules
- [x] Authorization rules
- [x] Create business-rules.md

### Phase 6: Approval
- [x] Present completion message
- [x] Wait for user approval
- [x] Update aidlc-state.md

---

**다음 단계**: 모든 질문에 답변을 완료하시면 알려주세요. 답변을 기반으로 Functional Design 문서를 생성하겠습니다.
