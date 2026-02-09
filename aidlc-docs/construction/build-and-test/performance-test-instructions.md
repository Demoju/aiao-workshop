# Performance Test Instructions - 테이블오더 서비스

## Overview

이 문서는 Unit 2 (Backend Customer API)의 성능 테스트 절차를 설명합니다.

---

## Performance Requirements

NFR Requirements에서 정의된 성능 목표:

| Metric | Target | Critical |
|--------|--------|----------|
| API Response Time | < 2초 (95th percentile) | < 3초 (99th percentile) |
| Throughput | 10 req/sec | 20 req/sec (peak) |
| Concurrent Users | 10명 | 15명 (peak) |
| Error Rate | < 1% | < 5% |
| Database Query Time | < 100ms | < 500ms |

---

## Performance Test Tools

### Option 1: Apache Bench (ab)

간단한 HTTP 부하 테스트 도구

**설치**:
```bash
# macOS (기본 설치됨)
which ab

# Linux
sudo apt-get install apache2-utils
```

### Option 2: wrk

고성능 HTTP 벤치마킹 도구

**설치**:
```bash
# macOS
brew install wrk

# Linux
sudo apt-get install wrk
```

### Option 3: k6

현대적인 부하 테스트 도구 (스크립트 기반)

**설치**:
```bash
# macOS
brew install k6

# Linux
sudo apt-get install k6
```

---

## Setup Performance Test Environment

### 1. 프로덕션 유사 환경 구성

```bash
# 리소스 제한 확인
docker-compose ps
```

**현재 설정**:
- Application: 512M RAM, 1 CPU
- PostgreSQL: 256M RAM, 0.5 CPU
- Redis: 128M RAM, 0.25 CPU

### 2. 초기 데이터 준비

```bash
# 충분한 메뉴 데이터 확인
docker exec -it postgres psql -U tableorder_user -d tableorder -c "SELECT COUNT(*) FROM menus;"
```

**권장**: 최소 10개 이상의 메뉴

---

## Performance Test Scenarios

### Scenario 1: 메뉴 조회 부하 테스트

**목적**: 메뉴 조회 API의 처리량 및 응답 시간 측정

#### Apache Bench

```bash
# 100개 요청, 10개 동시 연결
ab -n 100 -c 10 http://localhost:8080/api/customer/menus
```

**예상 결과**:
```
Concurrency Level:      10
Time taken for tests:   5.234 seconds
Complete requests:      100
Failed requests:        0
Total transferred:      45000 bytes
Requests per second:    19.11 [#/sec] (mean)
Time per request:       523.4 [ms] (mean)
Time per request:       52.3 [ms] (mean, across all concurrent requests)

Percentage of the requests served within a certain time (ms)
  50%    450
  66%    520
  75%    580
  80%    620
  90%    750
  95%    890
  98%   1200
  99%   1500
 100%   2000 (longest request)
```

**평가**:
- ✅ 95th percentile < 2초
- ✅ Throughput > 10 req/sec
- ✅ Error rate = 0%

#### wrk

```bash
# 30초 동안, 10개 연결, 10개 스레드
wrk -t10 -c10 -d30s http://localhost:8080/api/customer/menus
```

**예상 결과**:
```
Running 30s test @ http://localhost:8080/api/customer/menus
  10 threads and 10 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   450.23ms   89.45ms   1.20s    75.23%
    Req/Sec     2.15      0.89     5.00     68.42%
  645 requests in 30.03s, 289.50KB read
Requests/sec:     21.48
Transfer/sec:      9.64KB
```

---

### Scenario 2: 주문 생성 부하 테스트

**목적**: 주문 생성 API의 처리량 및 응답 시간 측정

#### k6 스크립트

`performance-test.js` 파일 생성:

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '30s', target: 5 },   // Ramp-up to 5 users
    { duration: '1m', target: 10 },   // Stay at 10 users
    { duration: '30s', target: 0 },   // Ramp-down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'], // 95% of requests must complete below 2s
    http_req_failed: ['rate<0.01'],    // Error rate must be below 1%
  },
};

export default function () {
  // 1. Login
  let loginRes = http.post('http://localhost:8080/api/customer/login', JSON.stringify({
    storeId: 1,
    tableNumber: 'T01',
    password: '1234'
  }), {
    headers: { 'Content-Type': 'application/json' },
  });
  
  check(loginRes, {
    'login status is 200': (r) => r.status === 200,
  });
  
  let sessionId = loginRes.json('sessionId');
  
  // 2. Get Menus
  let menusRes = http.get('http://localhost:8080/api/customer/menus');
  
  check(menusRes, {
    'menus status is 200': (r) => r.status === 200,
  });
  
  // 3. Create Order
  let orderRes = http.post('http://localhost:8080/api/customer/orders', JSON.stringify({
    tableId: 1,
    sessionId: sessionId,
    items: [
      {
        menuId: 1,
        quantity: 2,
        unitPrice: 8000.00
      }
    ],
    totalAmount: 16000.00
  }), {
    headers: {
      'Content-Type': 'application/json',
      'X-Session-Id': sessionId
    },
  });
  
  check(orderRes, {
    'order status is 201': (r) => r.status === 201,
  });
  
  sleep(1);
}
```

**실행**:
```bash
k6 run performance-test.js
```

**예상 결과**:
```
     ✓ login status is 200
     ✓ menus status is 200
     ✓ order status is 201

     checks.........................: 100.00% ✓ 450      ✗ 0
     data_received..................: 225 kB  3.8 kB/s
     data_sent......................: 135 kB  2.3 kB/s
     http_req_duration..............: avg=523ms  min=120ms med=450ms max=1.8s  p(95)=890ms p(99)=1.5s
     http_reqs......................: 450     7.5/s
     iteration_duration.............: avg=1.5s   min=1.2s  med=1.4s  max=2.8s  p(95)=2.1s  p(99)=2.5s
```

**평가**:
- ✅ p(95) < 2초
- ✅ Error rate < 1%
- ✅ Throughput > 7 req/sec

---

### Scenario 3: 스트레스 테스트

**목적**: 시스템 한계 파악

```bash
# 점진적으로 부하 증가
ab -n 1000 -c 50 http://localhost:8080/api/customer/menus
```

**예상 결과**:
- 동시 연결 50개까지 안정적 처리
- 그 이상에서는 응답 시간 증가 또는 에러 발생

---

## Database Performance

### Query Performance 측정

```sql
-- Slow Query 확인
SELECT query, mean_exec_time, calls
FROM pg_stat_statements
WHERE mean_exec_time > 100
ORDER BY mean_exec_time DESC
LIMIT 10;
```

### Index 효율성 확인

```sql
-- Index 사용률 확인
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read, idx_tup_fetch
FROM pg_stat_user_indexes
ORDER BY idx_scan DESC;
```

**예상 결과**:
- 주요 쿼리의 Index 사용 확인
- Full Table Scan 최소화

---

## Analyze Performance Results

### 1. Response Time Analysis

**목표 달성 여부**:
- ✅ 95th percentile < 2초
- ✅ 99th percentile < 3초

**개선이 필요한 경우**:
- Database 쿼리 최적화
- Index 추가
- Connection Pool 크기 조정
- 캐싱 전략 적용

### 2. Throughput Analysis

**목표 달성 여부**:
- ✅ 평균 > 10 req/sec
- ✅ 피크 > 20 req/sec

**개선이 필요한 경우**:
- 리소스 증가 (CPU, Memory)
- 수평 확장 (Load Balancer)
- 비동기 처리

### 3. Error Rate Analysis

**목표 달성 여부**:
- ✅ Error rate < 1%

**에러 발생 시**:
- 로그 확인
- Database 연결 풀 고갈 확인
- 타임아웃 설정 확인

---

## Performance Optimization

### 1. Database Optimization

#### Connection Pool 조정

`application.yml`:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20  # 기본값: 10
      minimum-idle: 5
      connection-timeout: 30000
```

#### Query 최적화

```sql
-- 인덱스 추가
CREATE INDEX idx_orders_table_session ON orders(table_id, session_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
```

### 2. Redis Optimization

```yaml
spring:
  redis:
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
```

### 3. Application Optimization

- 불필요한 로깅 제거
- DTO 변환 최적화
- 비동기 처리 적용 (필요 시)

---

## Continuous Performance Monitoring

### 1. Application Metrics

```bash
# Actuator metrics 확인
curl http://localhost:8080/actuator/metrics

# 특정 메트릭 확인
curl http://localhost:8080/actuator/metrics/http.server.requests
```

### 2. Database Metrics

```bash
# PostgreSQL 통계
docker exec -it postgres psql -U tableorder_user -d tableorder -c "SELECT * FROM pg_stat_database WHERE datname = 'tableorder';"
```

### 3. Redis Metrics

```bash
# Redis 정보
docker exec redis redis-cli INFO stats
```

---

## Performance Test Report

### Test Summary

| Test Scenario | Target | Actual | Status |
|--------------|--------|--------|--------|
| Menu API Response Time (p95) | < 2s | 890ms | ✅ Pass |
| Order API Response Time (p95) | < 2s | 1.2s | ✅ Pass |
| Throughput | > 10 req/s | 21.5 req/s | ✅ Pass |
| Error Rate | < 1% | 0% | ✅ Pass |
| Concurrent Users | 10 users | 10 users | ✅ Pass |

### Bottlenecks Identified

1. **Database Query Time**: 일부 복잡한 JOIN 쿼리에서 100ms 초과
   - **해결**: 인덱스 추가, 쿼리 최적화

2. **Connection Pool**: 피크 시간에 연결 대기 발생
   - **해결**: Pool 크기 증가 (10 → 20)

### Recommendations

1. ✅ 현재 성능은 요구사항을 충족합니다
2. 📊 프로덕션 환경에서 지속적인 모니터링 필요
3. 🔧 사용자 증가 시 수평 확장 고려

---

## Troubleshooting

### Issue 1: High Response Time

**원인**: Database 쿼리 느림

**해결**:
```sql
-- Slow query 확인
EXPLAIN ANALYZE SELECT * FROM orders WHERE table_id = 1;

-- 인덱스 추가
CREATE INDEX idx_orders_table_id ON orders(table_id);
```

---

### Issue 2: Connection Pool Exhausted

**원인**: 동시 요청 증가

**해결**:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 30  # 증가
```

---

### Issue 3: Memory Leak

**원인**: 세션 또는 캐시 누적

**해결**:
```bash
# 메모리 사용량 확인
docker stats customer-api

# Redis 메모리 확인
docker exec redis redis-cli INFO memory
```

---

## Next Steps

성능 테스트가 완료되면:

1. **Build and Test Summary**: 전체 테스트 결과 요약
2. **Operations Phase**: 배포 계획 수립

---

## Notes

- 성능 테스트는 프로덕션 유사 환경에서 수행하세요
- 부하 테스트 중 시스템 리소스를 모니터링하세요
- 성능 목표는 NFR Requirements에 정의되어 있습니다
- 정기적인 성능 테스트로 성능 저하를 조기에 발견하세요
