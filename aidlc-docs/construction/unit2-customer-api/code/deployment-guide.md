# Deployment Guide - Unit 2 (Backend Customer API)

## Overview

이 문서는 Backend Customer API를 로컬 환경에 배포하는 방법을 설명합니다.

---

## Prerequisites

### Required Software

1. **Docker** (version 20.10 이상)
   - [Docker 설치 가이드](https://docs.docker.com/get-docker/)

2. **Docker Compose** (version 2.0 이상)
   - Docker Desktop에 포함됨

3. **Git** (선택사항)
   - 소스 코드 클론용

### System Requirements

- **OS**: macOS, Linux, Windows (WSL2)
- **RAM**: 최소 2GB (권장 4GB)
- **Disk**: 최소 5GB 여유 공간
- **CPU**: 2 cores 이상

---

## Quick Start

### 1. 환경 변수 설정

`.env.example` 파일을 복사하여 `.env` 파일을 생성합니다:

```bash
cp .env.example .env
```

`.env` 파일을 편집하여 환경 변수를 설정합니다:

```bash
# Application Configuration
SPRING_PROFILES_ACTIVE=dev

# Database Configuration
DB_NAME=tableorder
DB_USERNAME=tableorder_user
DB_PASSWORD=your_secure_password_here  # 변경 필요!

# Redis Configuration
# Redis does not require password in development environment
```

**중요**: `DB_PASSWORD`를 안전한 비밀번호로 변경하세요!

---

### 2. 서비스 시작

Docker Compose를 사용하여 모든 서비스를 시작합니다:

```bash
docker-compose up -d
```

**실행 결과**:
```
[+] Running 4/4
 ✔ Network tableorder-network  Created
 ✔ Container postgres           Started
 ✔ Container redis              Started
 ✔ Container customer-api       Started
```

---

### 3. 서비스 상태 확인

모든 컨테이너가 정상적으로 실행 중인지 확인합니다:

```bash
docker-compose ps
```

**예상 출력**:
```
NAME           STATUS                    PORTS
customer-api   Up 30 seconds (healthy)   0.0.0.0:8080->8080/tcp
postgres       Up 30 seconds (healthy)   0.0.0.0:5432->5432/tcp
redis          Up 30 seconds (healthy)   0.0.0.0:6379->6379/tcp
```

모든 컨테이너가 `(healthy)` 상태여야 합니다.

---

### 4. API 테스트

Health Check 엔드포인트를 호출하여 API가 정상 작동하는지 확인합니다:

```bash
curl http://localhost:8080/actuator/health
```

**예상 응답**:
```json
{
  "status": "UP"
}
```

---

## Detailed Deployment Steps

### Step 1: 소스 코드 준비

프로젝트 디렉토리로 이동합니다:

```bash
cd /path/to/aiao-workshop
```

디렉토리 구조 확인:
```
aiao-workshop/
├── backend/
├── docker-compose.yml
├── .env.example
└── logs/
```

---

### Step 2: 환경 변수 설정

#### 개발 환경 (.env)

```bash
SPRING_PROFILES_ACTIVE=dev
DB_NAME=tableorder
DB_USERNAME=tableorder_user
DB_PASSWORD=dev_password_123
```

#### 프로덕션 환경 (.env.prod)

```bash
SPRING_PROFILES_ACTIVE=prod
DB_NAME=tableorder
DB_USERNAME=tableorder_user
DB_PASSWORD=strong_production_password_here
```

---

### Step 3: Docker 이미지 빌드

Docker Compose가 자동으로 이미지를 빌드하지만, 수동으로 빌드할 수도 있습니다:

```bash
docker-compose build
```

**빌드 과정**:
1. Gradle로 JAR 파일 빌드
2. JRE 기반 런타임 이미지 생성
3. 최종 이미지 크기: ~200MB

---

### Step 4: 서비스 시작

#### 개발 환경

```bash
docker-compose up -d
```

#### 프로덕션 환경

```bash
docker-compose -f docker-compose.prod.yml up -d
```

---

### Step 5: 로그 확인

#### 모든 서비스 로그

```bash
docker-compose logs -f
```

#### 특정 서비스 로그

```bash
# Application 로그
docker-compose logs -f customer-api

# PostgreSQL 로그
docker-compose logs -f postgres

# Redis 로그
docker-compose logs -f redis
```

#### 호스트 로그 파일

Application 로그는 `./logs/` 디렉토리에도 저장됩니다:

```bash
tail -f logs/application.log
```

---

## Database Initialization

### 자동 초기화

PostgreSQL 컨테이너가 처음 시작될 때 `init.sql` 파일이 자동으로 실행됩니다:

- 테이블 생성 (tables, menus, categories, orders, order_items, table_sessions)
- 인덱스 생성
- 샘플 데이터 삽입 (테이블 3개, 메뉴 5개)

### 수동 초기화

필요한 경우 수동으로 SQL 파일을 실행할 수 있습니다:

```bash
docker exec -i postgres psql -U tableorder_user -d tableorder < backend/src/main/resources/db/init.sql
```

### 데이터베이스 접속

```bash
docker exec -it postgres psql -U tableorder_user -d tableorder
```

**샘플 쿼리**:
```sql
-- 테이블 목록 확인
SELECT * FROM tables;

-- 메뉴 목록 확인
SELECT * FROM menus;

-- 주문 목록 확인
SELECT * FROM orders;
```

---

## API Testing

### 1. 테이블 로그인

```bash
curl -X POST http://localhost:8080/api/customer/login \
  -H "Content-Type: application/json" \
  -d '{
    "storeId": 1,
    "tableNumber": "T01",
    "password": "1234"
  }'
```

**응답**:
```json
{
  "tableId": 1,
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "storeId": 1,
  "tableNumber": "T01"
}
```

### 2. 메뉴 조회

```bash
curl -X GET http://localhost:8080/api/customer/menus
```

### 3. 주문 생성

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

### 4. 주문 내역 조회

```bash
curl -X GET "http://localhost:8080/api/customer/orders?tableId=1&sessionId=550e8400-e29b-41d4-a716-446655440000"
```

---

## Troubleshooting

### Issue 1: 컨테이너가 시작되지 않음

**증상**:
```
Error response from daemon: driver failed programming external connectivity
```

**해결 방법**:
1. 포트 충돌 확인:
   ```bash
   lsof -i :8080
   lsof -i :5432
   lsof -i :6379
   ```

2. 사용 중인 프로세스 종료 또는 포트 변경

---

### Issue 2: Database 연결 실패

**증상**:
```
customer-api | Connection refused: postgres:5432
```

**해결 방법**:
1. PostgreSQL Health Check 확인:
   ```bash
   docker exec postgres pg_isready -U tableorder_user -d tableorder
   ```

2. 컨테이너 재시작:
   ```bash
   docker-compose restart postgres
   docker-compose restart customer-api
   ```

---

### Issue 3: Redis 연결 실패

**증상**:
```
customer-api | Unable to connect to Redis
```

**해결 방법**:
1. Redis Health Check 확인:
   ```bash
   docker exec redis redis-cli ping
   ```

2. 컨테이너 재시작:
   ```bash
   docker-compose restart redis
   docker-compose restart customer-api
   ```

---

### Issue 4: Application이 HEALTHY 상태가 안 됨

**증상**:
```
customer-api   Up 2 minutes (unhealthy)
```

**해결 방법**:
1. Application 로그 확인:
   ```bash
   docker-compose logs customer-api
   ```

2. Health Check 엔드포인트 수동 확인:
   ```bash
   docker exec customer-api wget -O- http://localhost:8080/actuator/health
   ```

3. 컨테이너 재시작:
   ```bash
   docker-compose restart customer-api
   ```

---

## Maintenance

### 서비스 중지

```bash
docker-compose down
```

### 서비스 재시작

```bash
docker-compose restart
```

### 특정 서비스 재시작

```bash
docker-compose restart customer-api
```

### 로그 삭제

```bash
rm -rf logs/*
```

### 데이터베이스 초기화 (주의: 모든 데이터 삭제)

```bash
docker-compose down -v
docker-compose up -d
```

---

## Backup and Restore

### Database Backup

```bash
docker exec postgres pg_dump -U tableorder_user tableorder > backup_$(date +%Y%m%d).sql
```

### Database Restore

```bash
docker exec -i postgres psql -U tableorder_user tableorder < backup_20260209.sql
```

### Logs Backup

```bash
tar -czf logs_backup_$(date +%Y%m%d).tar.gz logs/
```

---

## Monitoring

### Resource Usage

```bash
docker stats
```

**예상 출력**:
```
CONTAINER     CPU %   MEM USAGE / LIMIT   MEM %
customer-api  5.0%    256M / 512M         50%
postgres      2.0%    128M / 256M         50%
redis         1.0%    64M / 128M          50%
```

### Health Check Status

```bash
docker ps --format "table {{.Names}}\t{{.Status}}"
```

---

## Production Deployment

### 1. 환경 변수 설정

`.env.prod` 파일 생성:

```bash
SPRING_PROFILES_ACTIVE=prod
DB_NAME=tableorder
DB_USERNAME=tableorder_user
DB_PASSWORD=strong_production_password
```

### 2. Docker Compose 프로덕션 파일 사용

```bash
docker-compose -f docker-compose.prod.yml up -d
```

### 3. 보안 강화

- PostgreSQL, Redis 포트 외부 노출 제거
- HTTPS 설정 (Nginx 리버스 프록시)
- 방화벽 설정
- 정기적인 백업 설정

---

## Uninstallation

### 모든 컨테이너 및 볼륨 삭제

```bash
docker-compose down -v
```

### 이미지 삭제

```bash
docker rmi customer-api:latest
docker rmi postgres:16-alpine
docker rmi redis:7-alpine
```

---

## Support

문제가 발생하면 다음을 확인하세요:

1. Docker 및 Docker Compose 버전
2. 시스템 리소스 (RAM, Disk)
3. 포트 충돌
4. 로그 파일 (`logs/application.log`)
5. 컨테이너 상태 (`docker-compose ps`)

---

## Notes

- 개발 환경에서는 PostgreSQL과 Redis 포트가 외부에 노출됩니다
- 프로덕션 환경에서는 보안을 위해 포트 노출을 제거하세요
- 정기적으로 데이터베이스 백업을 수행하세요
- 로그 파일은 자동으로 로테이션됩니다 (일일 단위)
