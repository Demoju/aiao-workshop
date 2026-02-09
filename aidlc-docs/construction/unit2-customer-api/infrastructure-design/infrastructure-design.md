# Infrastructure Design - Unit 2 (Backend Customer API)

## Overview

이 문서는 Unit 2 (Backend Customer API)의 논리적 컴포넌트를 실제 인프라 서비스에 매핑하고, Docker Compose 기반 로컬 배포 환경을 정의합니다.

---

## 1. Deployment Environment

### 1.1 Container Orchestration

**선택**: Docker Compose

**구조**: 단일 docker-compose.yml (모든 서비스 포함)

**근거**:
- 로컬 개발 환경에 적합
- 간단한 구성 관리
- 모든 서비스를 한 파일에서 관리

---

## 2. Infrastructure Services Mapping

### 2.1 Application Service

**논리적 컴포넌트**: Spring Boot Application

**인프라 서비스**: Docker Container (Custom Image)

**이미지 빌드**: Multi-stage Dockerfile (빌드 + 실행 분리)

**Dockerfile**:
```dockerfile
# Stage 1: Build
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle build --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**컨테이너 설정**:
```yaml
customer-api:
  build:
    context: ./backend
    dockerfile: Dockerfile
  container_name: customer-api
  ports:
    - "8080:8080"
  environment:
    - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-dev}
    - DB_HOST=postgres
    - DB_PORT=5432
    - DB_NAME=${DB_NAME}
    - DB_USERNAME=${DB_USERNAME}
    - DB_PASSWORD=${DB_PASSWORD}
    - REDIS_HOST=redis
    - REDIS_PORT=6379
  depends_on:
    postgres:
      condition: service_healthy
    redis:
      condition: service_healthy
  healthcheck:
    test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
    interval: 10s
    timeout: 5s
    retries: 3
    start_period: 30s
  deploy:
    resources:
      limits:
        memory: 512M
        cpus: '1.0'
      reservations:
        memory: 256M
        cpus: '0.5'
  volumes:
    - ./logs:/app/logs
  networks:
    - tableorder-network
```

**근거**:
- Multi-stage 빌드로 이미지 크기 최소화
- 빌드 의존성과 런타임 분리
- 보안 강화 (JRE만 포함)

---

### 2.2 Database Service

**논리적 컴포넌트**: PostgreSQL Database

**인프라 서비스**: PostgreSQL Docker Container

**영속성**: Docker Volume

**컨테이너 설정**:
```yaml
postgres:
  image: postgres:16-alpine
  container_name: postgres
  environment:
    - POSTGRES_DB=${DB_NAME}
    - POSTGRES_USER=${DB_USERNAME}
    - POSTGRES_PASSWORD=${DB_PASSWORD}
  ports:
    - "5432:5432"  # 개발 시 외부 접근 허용 (프로덕션에서는 제거)
  volumes:
    - postgres-data:/var/lib/postgresql/data
    - ./backend/src/main/resources/db/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U ${DB_USERNAME} -d ${DB_NAME}"]
    interval: 10s
    timeout: 5s
    retries: 5
  deploy:
    resources:
      limits:
        memory: 256M
        cpus: '0.5'
  networks:
    - tableorder-network
```

**Volume 정의**:
```yaml
volumes:
  postgres-data:
    driver: local
```

**근거**:
- Docker Volume으로 데이터 영속성 보장
- 컨테이너 재시작 시에도 데이터 유지
- init.sql로 자동 초기화

---

### 2.3 Cache Service

**논리적 컴포넌트**: Redis (Session & Idempotency Cache)

**인프라 서비스**: Redis Docker Container

**영속성**: 없음 (메모리만)

**컨테이너 설정**:
```yaml
redis:
  image: redis:7-alpine
  container_name: redis
  ports:
    - "6379:6379"  # 개발 시 외부 접근 허용 (프로덕션에서는 제거)
  command: redis-server --maxmemory 128mb --maxmemory-policy allkeys-lru
  healthcheck:
    test: ["CMD", "redis-cli", "ping"]
    interval: 10s
    timeout: 5s
    retries: 3
  deploy:
    resources:
      limits:
        memory: 128M
        cpus: '0.25'
  networks:
    - tableorder-network
```

**근거**:
- Session은 24시간 TTL로 자동 만료
- 영속성 불필요 (재시작 시 재로그인)
- 메모리 효율적 운영

---

## 3. Storage Infrastructure

### 3.1 Database Storage

**서비스**: PostgreSQL

**영속성 전략**: Docker Volume

**Volume 이름**: `postgres-data`

**마운트 경로**: `/var/lib/postgresql/data`

**근거**:
- Docker가 관리하는 Volume
- 호스트 독립적
- 백업 용이

---

### 3.2 Cache Storage

**서비스**: Redis

**영속성 전략**: 없음 (메모리만)

**근거**:
- Session은 임시 데이터
- 재시작 시 재로그인으로 해결
- 단순한 운영

---

### 3.3 Application Logs

**로그 저장 위치**: Host 디렉토리 마운트

**마운트 설정**:
```yaml
volumes:
  - ./logs:/app/logs
```

**로그 파일 구조**:
```
logs/
├── application.log              # 현재 로그
├── application.2026-02-09.log   # 일일 로테이션
├── application.2026-02-08.log
└── ...
```

**근거**:
- 컨테이너 재시작 시에도 로그 유지
- 호스트에서 직접 로그 확인 가능
- 로그 분석 도구 사용 용이

---

## 4. Networking

### 4.1 Network Configuration

**네트워크 타입**: 기본 bridge 네트워크

**네트워크 정의**:
```yaml
networks:
  tableorder-network:
    driver: bridge
```

**근거**:
- 간단한 구성
- 서비스 간 이름으로 통신 가능
- 로컬 개발에 충분

---

### 4.2 Port Mapping

**외부 포트 노출**: Application만 노출 (8080)

**포트 매핑**:
```yaml
customer-api:
  ports:
    - "8080:8080"  # Application

postgres:
  ports:
    - "5432:5432"  # 개발 시에만 (프로덕션에서는 제거)

redis:
  ports:
    - "6379:6379"  # 개발 시에만 (프로덕션에서는 제거)
```

**근거**:
- Application만 외부 접근 허용
- Database와 Redis는 내부 통신만
- 보안 강화

**개발 환경 예외**:
- PostgreSQL, Redis 포트도 노출 (디버깅 용이)
- 프로덕션에서는 제거 필요

---

## 5. Configuration Management

### 5.1 Environment Variables

**관리 방식**: .env 파일 + docker-compose.yml 조합

**.env 파일**:
```bash
# Application
SPRING_PROFILES_ACTIVE=dev

# Database
DB_NAME=tableorder
DB_USERNAME=tableorder_user
DB_PASSWORD=your_secure_password_here

# Redis (비밀번호 없음)
```

**docker-compose.yml에서 사용**:
```yaml
environment:
  - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-dev}
  - DB_NAME=${DB_NAME}
  - DB_USERNAME=${DB_USERNAME}
  - DB_PASSWORD=${DB_PASSWORD}
```

**근거**:
- .env 파일로 환경별 설정 분리
- docker-compose.yml은 구조만 정의
- 민감 정보 분리

---

### 5.2 Sensitive Information Management

**관리 방식**: .env 파일 (.gitignore 포함)

**.gitignore**:
```
.env
logs/
```

**.env.example** (템플릿):
```bash
# Application
SPRING_PROFILES_ACTIVE=dev

# Database
DB_NAME=tableorder
DB_USERNAME=tableorder_user
DB_PASSWORD=change_me

# Redis (비밀번호 없음)
```

**근거**:
- .env 파일은 Git에 커밋 안 됨
- .env.example로 템플릿 제공
- 팀원이 복사하여 사용

---

## 6. Resource Limits

### 6.1 Container Resource Limits

**설정**: Memory + CPU 제한

**리소스 할당**:
```yaml
customer-api:
  deploy:
    resources:
      limits:
        memory: 512M
        cpus: '1.0'
      reservations:
        memory: 256M
        cpus: '0.5'

postgres:
  deploy:
    resources:
      limits:
        memory: 256M
        cpus: '0.5'

redis:
  deploy:
    resources:
      limits:
        memory: 128M
        cpus: '0.25'
```

**근거**:
- 로컬 개발 환경에서 리소스 과다 사용 방지
- 프로덕션 환경 시뮬레이션
- OOM 방지

---

## 7. Health Checks

### 7.1 Health Check Configuration

**확인 항목**: 모든 서비스 (App, PostgreSQL, Redis)

**Health Check 설정**:
```yaml
customer-api:
  healthcheck:
    test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
    interval: 10s
    timeout: 5s
    retries: 3
    start_period: 30s

postgres:
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U ${DB_USERNAME} -d ${DB_NAME}"]
    interval: 10s
    timeout: 5s
    retries: 5

redis:
  healthcheck:
    test: ["CMD", "redis-cli", "ping"]
    interval: 10s
    timeout: 5s
    retries: 3
```

**근거**:
- 서비스 상태 자동 확인
- depends_on과 조합하여 시작 순서 제어
- 장애 감지 및 자동 재시작

---

## 8. Initialization

### 8.1 Database Initialization

**초기화 방식**: init.sql 파일 자동 실행

**init.sql 위치**: `backend/src/main/resources/db/init.sql`

**마운트 설정**:
```yaml
postgres:
  volumes:
    - ./backend/src/main/resources/db/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
```

**init.sql 내용**:
```sql
-- Tables
CREATE TABLE IF NOT EXISTS tables (
    table_id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL,
    table_number VARCHAR(10) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    UNIQUE(store_id, table_number)
);

-- Menus
CREATE TABLE IF NOT EXISTS menus (
    menu_id BIGSERIAL PRIMARY KEY,
    menu_name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    category_id BIGINT,
    is_available BOOLEAN DEFAULT true
);

-- Orders
CREATE TABLE IF NOT EXISTS orders (
    order_id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    table_id BIGINT NOT NULL,
    session_id BIGINT,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    order_time TIMESTAMP NOT NULL,
    FOREIGN KEY (table_id) REFERENCES tables(table_id)
);

-- Order Items
CREATE TABLE IF NOT EXISTS order_items (
    order_item_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (menu_id) REFERENCES menus(menu_id)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_orders_table_id ON orders(table_id);
CREATE INDEX IF NOT EXISTS idx_orders_session_id ON orders(session_id);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_menu_id ON order_items(menu_id);

-- Sample Data (개발용)
INSERT INTO tables (store_id, table_number, password_hash) VALUES
(1, 'T01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'), -- password: 1234
(1, 'T02', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
(1, 'T03', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy')
ON CONFLICT DO NOTHING;

INSERT INTO menus (menu_name, price, description, image_url, category_id, is_available) VALUES
('김치찌개', 8000.00, '얼큰한 김치찌개', 'https://example.com/kimchi.jpg', 1, true),
('된장찌개', 7000.00, '구수한 된장찌개', 'https://example.com/doenjang.jpg', 1, true),
('불고기', 15000.00, '달콤한 불고기', 'https://example.com/bulgogi.jpg', 2, true),
('비빔밥', 9000.00, '영양 만점 비빔밥', 'https://example.com/bibimbap.jpg', 3, true),
('냉면', 10000.00, '시원한 냉면', 'https://example.com/naengmyeon.jpg', 3, false)
ON CONFLICT DO NOTHING;
```

**근거**:
- PostgreSQL 컨테이너 첫 시작 시 자동 실행
- 테이블 생성 + 샘플 데이터 삽입
- 개발 환경 즉시 사용 가능

---

### 8.2 Application Start Order

**시작 순서 제어**: depends_on + healthcheck 조합

**설정**:
```yaml
customer-api:
  depends_on:
    postgres:
      condition: service_healthy
    redis:
      condition: service_healthy
```

**시작 순서**:
1. PostgreSQL 시작 → Health Check 통과 대기
2. Redis 시작 → Health Check 통과 대기
3. Application 시작

**근거**:
- Database와 Redis가 준비된 후 Application 시작
- 연결 실패 방지
- 안정적인 시작

---

## 9. Development vs Production

### 9.1 Environment Separation

**분리 방식**: Docker Compose 파일 분리

**파일 구조**:
```
docker-compose.yml           # 개발 환경
docker-compose.prod.yml      # 프로덕션 환경
```

**docker-compose.yml (개발)**:
```yaml
version: '3.8'

services:
  customer-api:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: customer-api
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      # ... (위에서 정의한 설정)
    volumes:
      - ./logs:/app/logs  # 로그 마운트
    # ... (나머지 설정)

  postgres:
    ports:
      - "5432:5432"  # 개발 시 외부 접근 허용
    # ... (나머지 설정)

  redis:
    ports:
      - "6379:6379"  # 개발 시 외부 접근 허용
    # ... (나머지 설정)
```

**docker-compose.prod.yml (프로덕션)**:
```yaml
version: '3.8'

services:
  customer-api:
    image: customer-api:latest  # Pre-built 이미지 사용
    container_name: customer-api
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      # ... (프로덕션 환경 변수)
    # volumes 제거 (로그는 중앙 집중식 로깅 사용)
    # ... (나머지 설정)

  postgres:
    # ports 제거 (외부 접근 차단)
    # ... (나머지 설정)

  redis:
    # ports 제거 (외부 접근 차단)
    # ... (나머지 설정)
```

**실행 방법**:
```bash
# 개발 환경
docker-compose up -d

# 프로덕션 환경
docker-compose -f docker-compose.prod.yml up -d
```

**근거**:
- 환경별 설정 명확히 분리
- 개발: 디버깅 용이 (포트 노출, 로그 마운트)
- 프로덕션: 보안 강화 (포트 차단, 중앙 로깅)

---

## 10. Complete docker-compose.yml

```yaml
version: '3.8'

services:
  customer-api:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: customer-api
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-dev}
      - DB_HOST=postgres
      - DB_PORT=5432
      - DB_NAME=${DB_NAME}
      - DB_USERNAME=${DB_USERNAME}
      - DB_PASSWORD=${DB_PASSWORD}
      - REDIS_HOST=redis
      - REDIS_PORT=6379
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 10s
      timeout: 5s
      retries: 3
      start_period: 30s
    deploy:
      resources:
        limits:
          memory: 512M
          cpus: '1.0'
        reservations:
          memory: 256M
          cpus: '0.5'
    volumes:
      - ./logs:/app/logs
    networks:
      - tableorder-network

  postgres:
    image: postgres:16-alpine
    container_name: postgres
    environment:
      - POSTGRES_DB=${DB_NAME}
      - POSTGRES_USER=${DB_USERNAME}
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./backend/src/main/resources/db/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USERNAME} -d ${DB_NAME}"]
      interval: 10s
      timeout: 5s
      retries: 5
    deploy:
      resources:
        limits:
          memory: 256M
          cpus: '0.5'
    networks:
      - tableorder-network

  redis:
    image: redis:7-alpine
    container_name: redis
    ports:
      - "6379:6379"
    command: redis-server --maxmemory 128mb --maxmemory-policy allkeys-lru
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 3
    deploy:
      resources:
        limits:
          memory: 128M
          cpus: '0.25'
    networks:
      - tableorder-network

volumes:
  postgres-data:
    driver: local

networks:
  tableorder-network:
    driver: bridge
```

---

## 11. Infrastructure Summary

| 컴포넌트 | 인프라 서비스 | 영속성 | 포트 | 리소스 제한 |
|---------|-------------|-------|------|-----------|
| Application | Docker Container (Custom) | 로그 (Host 마운트) | 8080 | 512M / 1 CPU |
| Database | PostgreSQL 16 | Docker Volume | 5432 (개발) | 256M / 0.5 CPU |
| Cache | Redis 7 | 없음 (메모리) | 6379 (개발) | 128M / 0.25 CPU |

---

## Summary

이 문서는 Unit 2 (Backend Customer API)의 인프라 설계를 정의했습니다. Docker Compose 기반 로컬 배포 환경으로, PostgreSQL, Redis, Spring Boot Application을 컨테이너로 실행하며, Health Check와 의존성 관리를 통해 안정적인 시작 순서를 보장합니다. 개발 환경과 프로덕션 환경을 분리하여 보안과 디버깅 편의성을 모두 고려했습니다.
