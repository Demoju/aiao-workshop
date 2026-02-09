# Integration Test Instructions - Unit 1 (Frontend)

## Purpose
Frontend와 Backend 간 API 통합 테스트. Unit 2, 3 (Backend) 완료 후 실행.

## Prerequisites
- Backend (Unit 2, 3) 개발 완료
- Docker Compose로 전체 시스템 실행 가능

## Test Scenarios

### Scenario 1: Customer 주문 플로우
1. 테이블 로그인 (POST /api/customer/login)
2. 메뉴 조회 (GET /api/customer/menus)
3. 주문 생성 (POST /api/customer/orders)
4. 주문 내역 조회 (GET /api/customer/orders)
5. 주문 취소 (DELETE /api/customer/orders/{id})

### Scenario 2: Admin 주문 관리 플로우
1. 관리자 로그인 (POST /api/admin/login)
2. 주문 목록 조회 (GET /api/admin/orders)
3. SSE 연결 (GET /api/admin/orders/stream)
4. 주문 상태 변경 (PATCH /api/admin/orders/{id}/status)
5. 테이블 세션 종료 (POST /api/admin/tables/{id}/end-session)

### Scenario 3: 실시간 연동
1. Customer가 주문 생성
2. Admin SSE로 신규 주문 수신 확인
3. Admin이 주문 상태 변경
4. Customer 주문 내역에서 상태 변경 확인

## Setup
```bash
docker-compose up --build
# frontend: http://localhost:3000
# backend: http://localhost:8080
# postgres: localhost:5432
```

## Execution
Backend 완료 후 수동 통합 테스트 또는 Cypress/Playwright E2E 테스트 추가 가능.

## Note
현재 Unit 1 (Frontend)만 완료. Unit 2, 3 완료 후 통합 테스트 실행 예정.
