# Integration Test Instructions

## Overview
Integration tests verify interactions between components and external systems.

## Test Scenarios

### 1. Database Integration Tests
Test MyBatis mappers with actual PostgreSQL database.

```bash
# Start test database
docker-compose up -d db

# Run integration tests
./mvnw verify -P integration-test
```

### 2. API Integration Tests
Test REST API endpoints with MockMvc.

**Test Cases**:
- POST /api/admin/login → 200 OK with JWT
- GET /api/admin/orders → 200 OK with order list
- PATCH /api/admin/orders/{id}/status → 200 OK
- DELETE /api/admin/orders/{id} → 200 OK

### 3. End-to-End Flow Tests
Test complete user workflows.

**Scenario 1: Admin Login and View Orders**
```
1. POST /api/admin/login (admin/password)
2. Extract JWT token
3. GET /api/admin/orders with JWT
4. Verify orders returned
```

**Scenario 2: Order Status Management**
```
1. Login as admin
2. GET /api/admin/orders
3. PATCH /api/admin/orders/1/status (PENDING → PREPARING)
4. Verify status updated
5. PATCH /api/admin/orders/1/status (PREPARING → COMPLETED)
6. Verify status updated
```

## Test Data Setup

### Sample Data (data.sql)
```sql
-- Insert test store
INSERT INTO store (store_code, name) VALUES ('STORE001', 'Test Store');

-- Insert test admin
INSERT INTO admin (store_id, username, password, login_attempts) 
VALUES (1, 'admin', '$2a$10$...', 0);

-- Insert test table
INSERT INTO "table" (store_id, table_number, password) 
VALUES (1, '1', '$2a$10$...');

-- Insert test session
INSERT INTO table_session (store_id, table_id, is_active) 
VALUES (1, 1, true);

-- Insert test orders
INSERT INTO orders (store_id, table_id, session_id, order_number, status, total_amount) 
VALUES (1, 1, 1, 'ORD001', 'PENDING', 15000.00);
```

## Running Integration Tests

### With Docker Compose
```bash
# Start all services
docker-compose up -d

# Wait for services to be ready
sleep 10

# Run integration tests
./mvnw verify -P integration-test

# Stop services
docker-compose down
```

### Manual Testing with curl

#### 1. Login
```bash
curl -X POST http://localhost:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

#### 2. Get Orders
```bash
curl -X GET "http://localhost:8080/api/admin/orders?storeId=1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 3. Update Order Status
```bash
curl -X PATCH "http://localhost:8080/api/admin/orders/1/status?status=PREPARING" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 4. Delete Order
```bash
curl -X DELETE http://localhost:8080/api/admin/orders/1 \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

## Expected Results

### Success Criteria
- ✅ All API endpoints return expected status codes
- ✅ Database transactions commit successfully
- ✅ JWT authentication works correctly
- ✅ Business rules enforced (status transitions, deletion rules)
- ✅ Error handling returns proper error messages

### Common Issues
- **401 Unauthorized**: JWT token expired or invalid
- **400 Bad Request**: Invalid status transition
- **404 Not Found**: Order not found
- **403 Forbidden**: Account locked

## Performance Baseline
- API response time: < 500ms (95th percentile)
- Database query time: < 100ms
- Concurrent requests: 10 users without errors
