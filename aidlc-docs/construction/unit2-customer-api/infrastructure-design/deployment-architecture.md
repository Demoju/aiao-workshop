# Deployment Architecture - Unit 2 (Backend Customer API)

## Overview

이 문서는 Unit 2 (Backend Customer API)의 배포 아키텍처를 시각화하고, 컨테이너 간 통신 흐름, 데이터 흐름, 그리고 운영 절차를 정의합니다.

---

## 1. Deployment Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         Host Machine                            │
│                                                                 │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │              Docker Network (bridge)                      │ │
│  │                                                           │ │
│  │  ┌─────────────────────────────────────────────────────┐ │ │
│  │  │         customer-api Container                      │ │ │
│  │  │                                                     │ │ │
│  │  │  ┌─────────────────────────────────────────────┐   │ │ │
│  │  │  │   Spring Boot Application                   │   │ │ │
│  │  │  │   - Port: 8080                              │   │ │ │
│  │  │  │   - Memory: 512M                            │   │ │ │
│  │  │  │   - CPU: 1.0                                │   │ │ │
│  │  │  └─────────────────────────────────────────────┘   │ │ │
│  │  │                                                     │ │ │
│  │  │  Volumes:                                           │ │ │
│  │  │  - ./logs:/app/logs (Host Mount)                   │ │ │
│  │  └─────────────────────────────────────────────────────┘ │ │
│  │                          │                                │ │
│  │                          │ HTTP                           │ │
│  │                          ▼                                │ │
│  │  ┌──────────────────────────────────────┐                │ │
│  │  │   postgres Container                 │                │ │
│  │  │   - Image: postgres:16-alpine        │                │ │
│  │  │   - Port: 5432 (internal)            │                │ │
│  │  │   - Memory: 256M                     │                │ │
│  │  │   - CPU: 0.5                         │                │ │
│  │  │                                      │                │ │
│  │  │   Volumes:                           │                │ │
│  │  │   - postgres-data (Docker Volume)    │                │ │
│  │  │   - init.sql (Read-only)             │                │ │
│  │  └──────────────────────────────────────┘                │ │
│  │                          │                                │ │
│  │                          │ Redis Protocol                 │ │
│  │                          ▼                                │ │
│  │  ┌──────────────────────────────────────┐                │ │
│  │  │   redis Container                    │                │ │
│  │  │   - Image: redis:7-alpine            │                │ │
│  │  │   - Port: 6379 (internal)            │                │ │
│  │  │   - Memory: 128M                     │                │ │
│  │  │   - CPU: 0.25                        │                │ │
│  │  │   - Max Memory: 128MB (LRU)          │                │ │
│  │  └──────────────────────────────────────┘                │ │
│  │                                                           │ │
│  └───────────────────────────────────────────────────────────┘ │
│                                                                 │
│  Host Volumes:                                                  │
│  - ./logs/                  (Application Logs)                  │
│  - postgres-data (Docker)   (Database Data)                     │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
         │
         │ Port 8080 (Exposed)
         ▼
    ┌─────────┐
    │ Client  │
    │(Browser)│
    └─────────┘
```

---

## 2. Container Communication Flow

### 2.1 External to Application

```
Client (Browser)
    │
    │ HTTP Request (Port 8080)
    │ GET /api/customer/menus
    │
    ▼
customer-api Container
    │
    │ Spring Boot Application
    │ - Controller Layer
    │ - Service Layer
    │ - Repository Layer
    │
    ├──────────────┬─────────────┐
    │              │             │
    ▼              ▼             ▼
postgres      redis         Logs
(5432)        (6379)        (Host)
```

### 2.2 Application to Database

```
customer-api Container
    │
    │ JDBC Connection
    │ jdbc:postgresql://postgres:5432/tableorder
    │
    ▼
postgres Container
    │
    │ PostgreSQL Server
    │ - Connection Pool (HikariCP)
    │ - Query Execution
    │
    ▼
postgres-data Volume
    │
    │ Persistent Storage
    │ - Tables
    │ - Indexes
    │ - Data
```

### 2.3 Application to Cache

```
customer-api Container
    │
    │ Redis Connection
    │ redis://redis:6379
    │
    ▼
redis Container
    │
    │ Redis Server
    │ - Session Storage
    │ - Idempotency Cache
    │ - TTL Management
    │
    ▼
Memory (No Persistence)
```

---

## 3. Data Flow

### 3.1 Order Creation Flow

```
1. Client Request
   POST /api/customer/orders
   Body: { tableId, sessionId, items, totalAmount, idempotencyKey }
   Header: X-Session-Id

2. customer-api Container
   ├─ SessionAuthenticationFilter
   │  └─ Redis: Check session validity
   │
   ├─ OrderController
   │  └─ @Valid: Input validation
   │
   ├─ OrderService
   │  ├─ Redis: Check idempotency key
   │  ├─ PostgreSQL: Validate menu prices
   │  ├─ PostgreSQL: Insert order + order_items (Transaction)
   │  └─ Redis: Store idempotency result
   │
   └─ Response: OrderResponseDto

3. Data Persistence
   ├─ PostgreSQL: orders, order_items tables
   └─ Redis: idempotency:{key} (24h TTL)
```

### 3.2 Session Management Flow

```
1. Login Request
   POST /api/customer/login
   Body: { storeId, tableNumber, password }

2. customer-api Container
   ├─ AuthController
   │
   ├─ AuthService
   │  ├─ PostgreSQL: Find table by storeId + tableNumber
   │  ├─ BCrypt: Verify password
   │  └─ SessionService
   │     ├─ Redis: Check existing session
   │     └─ Redis: Create/update session (24h TTL)
   │
   └─ Response: { sessionId, tableId }

3. Data Storage
   └─ Redis: session:{sessionId} (24h TTL)
```

### 3.3 Menu Query Flow

```
1. Client Request
   GET /api/customer/menus

2. customer-api Container
   ├─ MenuController
   │
   ├─ MenuService
   │  └─ PostgreSQL: SELECT * FROM menus WHERE is_available = true
   │
   └─ Response: List<MenuResponseDto>

3. Data Source
   └─ PostgreSQL: menus table
```

---

## 4. Startup Sequence

```
1. Docker Compose Start
   docker-compose up -d

2. Container Initialization
   ┌─────────────────────────────────────────┐
   │ Step 1: postgres Container              │
   │ - Pull image: postgres:16-alpine        │
   │ - Create volume: postgres-data          │
   │ - Run init.sql                          │
   │ - Health check: pg_isready              │
   │ - Status: HEALTHY                       │
   └─────────────────────────────────────────┘
                    │
                    ▼
   ┌─────────────────────────────────────────┐
   │ Step 2: redis Container                 │
   │ - Pull image: redis:7-alpine            │
   │ - Start Redis server                    │
   │ - Health check: redis-cli ping          │
   │ - Status: HEALTHY                       │
   └─────────────────────────────────────────┘
                    │
                    ▼
   ┌─────────────────────────────────────────┐
   │ Step 3: customer-api Container          │
   │ - Build image (Multi-stage)             │
   │   - Stage 1: Gradle build               │
   │   - Stage 2: Copy JAR + JRE             │
   │ - Wait for postgres HEALTHY             │
   │ - Wait for redis HEALTHY                │
   │ - Start Spring Boot                     │
   │ - Health check: /actuator/health        │
   │ - Status: HEALTHY                       │
   └─────────────────────────────────────────┘
                    │
                    ▼
   ┌─────────────────────────────────────────┐
   │ All Services Ready                      │
   │ - Application: http://localhost:8080    │
   │ - PostgreSQL: localhost:5432 (dev)      │
   │ - Redis: localhost:6379 (dev)           │
   └─────────────────────────────────────────┘
```

---

## 5. Health Check Strategy

### 5.1 Health Check Configuration

| Service | Check Command | Interval | Timeout | Retries | Start Period |
|---------|--------------|----------|---------|---------|--------------|
| customer-api | `wget http://localhost:8080/actuator/health` | 10s | 5s | 3 | 30s |
| postgres | `pg_isready -U ${DB_USERNAME} -d ${DB_NAME}` | 10s | 5s | 5 | - |
| redis | `redis-cli ping` | 10s | 5s | 3 | - |

### 5.2 Health Check Flow

```
1. Container Start
   │
   ▼
2. Initial Delay (start_period)
   │ customer-api: 30s
   │ postgres: 0s
   │ redis: 0s
   │
   ▼
3. Health Check Execution
   │ Every 10 seconds
   │
   ├─ Success → Status: HEALTHY
   │
   └─ Failure → Retry (max 3-5 times)
      │
      ├─ Success → Status: HEALTHY
      │
      └─ All Failed → Status: UNHEALTHY
         │
         └─ Docker restarts container
```

---

## 6. Resource Allocation

### 6.1 Memory Allocation

```
Total Memory: 896M

┌─────────────────────────────────────────┐
│ customer-api: 512M (57%)                │
│ ┌─────────────────────────────────────┐ │
│ │ JVM Heap: ~256M                     │ │
│ │ JVM Non-Heap: ~128M                 │ │
│ │ Native Memory: ~128M                │ │
│ └─────────────────────────────────────┘ │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│ postgres: 256M (29%)                    │
│ ┌─────────────────────────────────────┐ │
│ │ Shared Buffers: ~64M                │ │
│ │ Work Memory: ~4M per connection     │ │
│ │ Maintenance: ~16M                   │ │
│ └─────────────────────────────────────┘ │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│ redis: 128M (14%)                       │
│ ┌─────────────────────────────────────┐ │
│ │ Max Memory: 128M (LRU eviction)     │ │
│ │ Session Data: ~50M (estimated)      │ │
│ │ Idempotency Cache: ~30M (estimated) │ │
│ └─────────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

### 6.2 CPU Allocation

```
Total CPU: 1.75 cores

customer-api: 1.0 core (57%)
postgres:     0.5 core (29%)
redis:        0.25 core (14%)
```

---

## 7. Volume Management

### 7.1 Volume Structure

```
Host Machine
│
├── postgres-data (Docker Volume)
│   └── /var/lib/postgresql/data
│       ├── base/
│       ├── global/
│       ├── pg_wal/
│       └── ...
│
├── ./logs/ (Host Directory)
│   ├── application.log
│   ├── application.2026-02-09.log
│   ├── application.2026-02-08.log
│   └── ...
│
└── ./backend/src/main/resources/db/init.sql (Read-only)
    └── Mounted to: /docker-entrypoint-initdb.d/init.sql
```

### 7.2 Volume Backup Strategy

**PostgreSQL Data Backup**:
```bash
# Backup
docker exec postgres pg_dump -U tableorder_user tableorder > backup.sql

# Restore
docker exec -i postgres psql -U tableorder_user tableorder < backup.sql
```

**Logs Backup**:
```bash
# Logs are already on host (./logs/)
# Archive old logs
tar -czf logs-backup-$(date +%Y%m%d).tar.gz logs/
```

---

## 8. Network Security

### 8.1 Port Exposure Strategy

**Development Environment**:
```
External Access:
- 8080 → customer-api (Application)
- 5432 → postgres (Database - for debugging)
- 6379 → redis (Cache - for debugging)

Internal Network:
- All services communicate via Docker network
```

**Production Environment**:
```
External Access:
- 8080 → customer-api (Application ONLY)

Internal Network:
- postgres: No external access
- redis: No external access
- All services communicate via Docker network
```

### 8.2 Network Isolation

```
┌─────────────────────────────────────────┐
│         tableorder-network              │
│         (bridge)                        │
│                                         │
│  ┌──────────────┐                       │
│  │ customer-api │                       │
│  │ (8080)       │                       │
│  └──────┬───────┘                       │
│         │                               │
│         ├──────────┬──────────┐         │
│         │          │          │         │
│  ┌──────▼──────┐ ┌─▼────────┐ │         │
│  │  postgres   │ │  redis   │ │         │
│  │  (5432)     │ │  (6379)  │ │         │
│  └─────────────┘ └──────────┘ │         │
│                                │         │
└────────────────────────────────┼─────────┘
                                 │
                                 │ Port 8080 Only
                                 ▼
                            External Access
```

---

## 9. Operational Procedures

### 9.1 Start Services

```bash
# Start all services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f customer-api
```

### 9.2 Stop Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (CAUTION: Data loss)
docker-compose down -v
```

### 9.3 Restart Services

```bash
# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart customer-api
```

### 9.4 View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f customer-api

# Last 100 lines
docker-compose logs --tail=100 customer-api
```

### 9.5 Database Access

```bash
# Connect to PostgreSQL
docker exec -it postgres psql -U tableorder_user -d tableorder

# Run SQL file
docker exec -i postgres psql -U tableorder_user -d tableorder < query.sql
```

### 9.6 Redis Access

```bash
# Connect to Redis CLI
docker exec -it redis redis-cli

# Check session
docker exec redis redis-cli GET "session:12345"

# Check all keys
docker exec redis redis-cli KEYS "*"
```

### 9.7 Application Debugging

```bash
# Check application health
curl http://localhost:8080/actuator/health

# Check application logs
tail -f logs/application.log

# Enter container
docker exec -it customer-api sh
```

---

## 10. Monitoring and Troubleshooting

### 10.1 Health Check Monitoring

```bash
# Check all container health
docker ps --format "table {{.Names}}\t{{.Status}}"

# Expected output:
# NAMES           STATUS
# customer-api    Up 5 minutes (healthy)
# postgres        Up 5 minutes (healthy)
# redis           Up 5 minutes (healthy)
```

### 10.2 Resource Monitoring

```bash
# Check resource usage
docker stats

# Expected output:
# CONTAINER     CPU %   MEM USAGE / LIMIT   MEM %
# customer-api  5.0%    256M / 512M         50%
# postgres      2.0%    128M / 256M         50%
# redis         1.0%    64M / 128M          50%
```

### 10.3 Common Issues

**Issue 1: Application fails to start**
```bash
# Check logs
docker-compose logs customer-api

# Common causes:
# - Database not ready → Check postgres health
# - Redis not ready → Check redis health
# - Port conflict → Check port 8080 availability
```

**Issue 2: Database connection error**
```bash
# Check postgres health
docker exec postgres pg_isready -U tableorder_user -d tableorder

# Check network connectivity
docker exec customer-api ping postgres
```

**Issue 3: Redis connection error**
```bash
# Check redis health
docker exec redis redis-cli ping

# Check network connectivity
docker exec customer-api ping redis
```

---

## 11. Deployment Checklist

### 11.1 Pre-Deployment

- [ ] .env 파일 생성 (.env.example 복사)
- [ ] 환경 변수 설정 (DB_PASSWORD 등)
- [ ] Docker 및 Docker Compose 설치 확인
- [ ] 포트 8080, 5432, 6379 사용 가능 확인

### 11.2 Deployment

- [ ] `docker-compose up -d` 실행
- [ ] 모든 컨테이너 HEALTHY 상태 확인
- [ ] Application health check 확인 (`curl http://localhost:8080/actuator/health`)
- [ ] 샘플 데이터 확인 (메뉴 조회 API 테스트)

### 11.3 Post-Deployment

- [ ] 로그 파일 생성 확인 (`./logs/application.log`)
- [ ] Database 초기화 확인 (테이블 생성, 샘플 데이터)
- [ ] Redis 연결 확인 (Session 저장 테스트)
- [ ] API 엔드포인트 테스트 (Postman/curl)

---

## Summary

이 문서는 Unit 2 (Backend Customer API)의 배포 아키텍처를 정의했습니다. Docker Compose 기반 3-tier 아키텍처로, Application, Database, Cache 컨테이너가 독립적으로 실행되며, Health Check와 의존성 관리를 통해 안정적인 시작 순서를 보장합니다. 운영 절차와 트러블슈팅 가이드를 포함하여 개발자가 쉽게 배포하고 관리할 수 있도록 구성했습니다.
