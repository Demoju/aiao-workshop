# API Documentation - Unit 2 (Backend Customer API)

## Overview

이 문서는 Backend Customer API의 모든 REST API 엔드포인트를 정의합니다.

**Base URL**: `http://localhost:8080`

**API Version**: v1

---

## Authentication

### Table Login

테이블 로그인을 수행하고 세션 ID를 발급받습니다.

**Endpoint**: `POST /api/customer/login`

**Request Body**:
```json
{
  "storeId": 1,
  "tableNumber": "T01",
  "password": "1234"
}
```

**Response** (200 OK):
```json
{
  "tableId": 1,
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "storeId": 1,
  "tableNumber": "T01"
}
```

**Error Responses**:
- `401 Unauthorized`: 테이블을 찾을 수 없거나 비밀번호가 일치하지 않음
- `400 Bad Request`: 필수 필드 누락

---

## Menu APIs

### Get Menus

메뉴 목록을 조회합니다. 카테고리 ID를 제공하면 해당 카테고리의 메뉴만 조회합니다.

**Endpoint**: `GET /api/customer/menus`

**Query Parameters**:
- `categoryId` (optional): 카테고리 ID

**Response** (200 OK):
```json
[
  {
    "menuId": 1,
    "menuName": "김치찌개",
    "price": 8000.00,
    "description": "얼큰한 김치찌개",
    "imageUrl": "https://example.com/kimchi.jpg",
    "categoryId": 1,
    "isAvailable": true
  },
  {
    "menuId": 2,
    "menuName": "된장찌개",
    "price": 7000.00,
    "description": "구수한 된장찌개",
    "imageUrl": "https://example.com/doenjang.jpg",
    "categoryId": 1,
    "isAvailable": true
  }
]
```

### Get Categories

카테고리 목록을 조회합니다.

**Endpoint**: `GET /api/customer/categories`

**Response** (200 OK):
```json
[
  {
    "categoryId": 1,
    "categoryName": "찌개류",
    "storeId": 1
  },
  {
    "categoryId": 2,
    "categoryName": "고기류",
    "storeId": 1
  }
]
```

---

## Order APIs

### Create Order

주문을 생성합니다. 세션이 없으면 자동으로 생성됩니다.

**Endpoint**: `POST /api/customer/orders`

**Request Headers**:
- `X-Session-Id` (required): 세션 ID

**Request Body**:
```json
{
  "tableId": 1,
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "items": [
    {
      "menuId": 1,
      "quantity": 2,
      "unitPrice": 8000.00
    },
    {
      "menuId": 2,
      "quantity": 1,
      "unitPrice": 7000.00
    }
  ],
  "totalAmount": 23000.00
}
```

**Response** (201 Created):
```json
{
  "orderId": 1,
  "orderNumber": "ORD-20260209-001",
  "tableId": 1,
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "totalAmount": 23000.00,
  "status": "PENDING",
  "orderTime": "2026-02-09T14:30:00",
  "items": [
    {
      "orderItemId": 1,
      "menuId": 1,
      "menuName": "김치찌개",
      "quantity": 2,
      "unitPrice": 8000.00
    },
    {
      "orderItemId": 2,
      "menuId": 2,
      "menuName": "된장찌개",
      "quantity": 1,
      "unitPrice": 7000.00
    }
  ]
}
```

**Error Responses**:
- `400 Bad Request`: 필수 필드 누락, 가격 불일치, 총액 불일치
- `404 Not Found`: 메뉴를 찾을 수 없음
- `400 Bad Request`: 품절된 메뉴

### Get Orders

현재 세션의 주문 내역을 조회합니다.

**Endpoint**: `GET /api/customer/orders`

**Query Parameters**:
- `tableId` (required): 테이블 ID
- `sessionId` (required): 세션 ID

**Response** (200 OK):
```json
[
  {
    "orderId": 1,
    "orderNumber": "ORD-20260209-001",
    "tableId": 1,
    "sessionId": "550e8400-e29b-41d4-a716-446655440000",
    "totalAmount": 23000.00,
    "status": "PENDING",
    "orderTime": "2026-02-09T14:30:00",
    "items": [
      {
        "orderItemId": 1,
        "menuId": 1,
        "menuName": "김치찌개",
        "quantity": 2,
        "unitPrice": 8000.00
      }
    ]
  }
]
```

### Cancel Order

주문을 취소합니다. PENDING 상태의 주문만 취소 가능합니다.

**Endpoint**: `DELETE /api/customer/orders/{orderId}`

**Path Parameters**:
- `orderId`: 주문 ID

**Response** (204 No Content)

**Error Responses**:
- `404 Not Found`: 주문을 찾을 수 없음
- `400 Bad Request`: 대기 중인 주문만 취소 가능

---

## Error Response Format

모든 에러 응답은 다음 형식을 따릅니다:

```json
{
  "status": 400,
  "message": "메뉴 가격이 변경되었습니다. 다시 시도해주세요",
  "timestamp": "2026-02-09T14:30:00",
  "path": "/api/customer/orders"
}
```

---

## Status Codes

| Status Code | Description |
|------------|-------------|
| 200 OK | 요청 성공 |
| 201 Created | 리소스 생성 성공 |
| 204 No Content | 요청 성공 (응답 본문 없음) |
| 400 Bad Request | 잘못된 요청 (검증 실패, 비즈니스 규칙 위반) |
| 401 Unauthorized | 인증 실패 |
| 404 Not Found | 리소스를 찾을 수 없음 |
| 500 Internal Server Error | 서버 내부 오류 |

---

## Order Status

| Status | Description |
|--------|-------------|
| PENDING | 주문 대기 중 (취소 가능) |
| ACCEPTED | 주문 수락됨 (취소 불가) |
| PREPARING | 조리 중 |
| COMPLETED | 조리 완료 |
| DELIVERED | 서빙 완료 |

---

## Sample Requests

### cURL Examples

**Login**:
```bash
curl -X POST http://localhost:8080/api/customer/login \
  -H "Content-Type: application/json" \
  -d '{
    "storeId": 1,
    "tableNumber": "T01",
    "password": "1234"
  }'
```

**Get Menus**:
```bash
curl -X GET http://localhost:8080/api/customer/menus
```

**Create Order**:
```bash
curl -X POST http://localhost:8080/api/customer/orders \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: 550e8400-e29b-41d4-a716-446655440000" \
  -d '{
    "tableId": 1,
    "sessionId": "550e8400-e29b-41d4-a716-446655440000",
    "items": [
      {
        "menuId": 1,
        "quantity": 2,
        "unitPrice": 8000.00
      }
    ],
    "totalAmount": 16000.00
  }'
```

**Get Orders**:
```bash
curl -X GET "http://localhost:8080/api/customer/orders?tableId=1&sessionId=550e8400-e29b-41d4-a716-446655440000"
```

**Cancel Order**:
```bash
curl -X DELETE http://localhost:8080/api/customer/orders/1
```

---

## Notes

- 모든 날짜/시간은 ISO 8601 형식 사용
- 모든 금액은 BigDecimal 타입 (소수점 2자리)
- Session ID는 UUID 형식
- Order Number는 "ORD-YYYYMMDD-NNN" 형식
