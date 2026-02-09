# Integration Test Instructions

## Purpose
Frontend ↔ Backend 간 API 통합 테스트. 전체 Unit 완료 후 실행.

## Prerequisites
- Docker Compose로 전체 시스템 실행 가능
- Unit 1 (Frontend), Unit 2 (Backend Customer API), Unit 3 (Backend Admin API) 모두 완료

## Setup
```bash
docker-compose up --build
# frontend: http://localhost:3000
# backend: http://localhost:8080
# postgres: localhost:5432
```

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

### Scenario 3: 실시간 연동 (Frontend ↔ Backend)
1. Customer가 주문 생성
2. Admin SSE로 신규 주문 수신 확인
3. Admin이 주문 상태 변경
4. Customer 주문 내역에서 상태 변경 확인

### Scenario 4: Admin Manual API Testing (Unit 3)
```bash
# Login
curl -X POST http://localhost:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# Get Orders
curl -X GET "http://localhost:8080/api/admin/orders?storeId=1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

## Cleanup
```bash
docker-compose down -v
```
