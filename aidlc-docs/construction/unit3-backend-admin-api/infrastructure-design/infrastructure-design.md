# Infrastructure Design - Unit 3 (Backend Admin API)

## Overview
논리적 컴포넌트를 실제 인프라 서비스로 매핑

---

## Deployment Architecture

### Deployment Model
- **Environment**: Local (Docker Compose)
- **Components**: 
  - Backend Admin API (Spring Boot)
  - PostgreSQL Database
  - Frontend (별도 배포, Unit 1)

### Architecture Diagram
```
┌─────────────────────────────────────────────────────────┐
│                   Admin Browser                         │
│  - React Frontend (Unit 1)                              │
│  - Port: 3000                                           │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ HTTP/SSE
                     ↓
┌─────────────────────────────────────────────────────────┐
│            Backend Admin API (Unit 3)                   │
│  - Spring Boot 3.x                                      │
│  - Port: 8080                                           │
│  - Container: tableorder-admin-api                      │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ JDBC
                     ↓
┌─────────────────────────────────────────────────────────┐
│                  PostgreSQL                             │
│  - Port: 5432                                           │
│  - Container: tableorder-db                             │
│  - Volume: postgres-data                                │
└─────────────────────────────────────────────────────────┘
```

---

## Infrastructure Components

### 1. Application Server

#### Spring Boot Application
- **Technology**: Spring Boot 3.x embedded Tomcat
- **Port**: 8080
- **Container Name**: tableorder-admin-api
- **Base Image**: eclipse-temurin:17-jre-alpine
- **Resources**:
  - CPU: 1 core
  - Memory: 512MB
- **Environment Variables**:
  ```yaml
  SPRING_DATASOURCE_URL: jdbc:postgresql://tableorder-db:5432/tableorder
  SPRING_DATASOURCE_USERNAME: postgres
  SPRING_DATASOURCE_PASSWORD: postgres
  JWT_SECRET: your-secret-key-change-in-production
  ```

### 2. Database

#### PostgreSQL
- **Version**: 15-alpine
- **Port**: 5432 (host) → 5432 (container)
- **Container Name**: tableorder-db
- **Volume**: postgres-data (persistent storage)
- **Environment Variables**:
  ```yaml
  POSTGRES_DB: tableorder
  POSTGRES_USER: postgres
  POSTGRES_PASSWORD: postgres
  ```
- **Resources**:
  - CPU: 1 core
  - Memory: 256MB

### 3. Network

#### Docker Network
- **Name**: tableorder-network
- **Type**: bridge
- **Purpose**: Container 간 통신

---

## Docker Compose Configuration

### docker-compose.yml
```yaml
version: '3.8'

services:
  # PostgreSQL Database
  db:
    image: postgres:15-alpine
    container_name: tableorder-db
    environment:
      POSTGRES_DB: tableorder
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - tableorder-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Backend Admin API
  admin-api:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: tableorder-admin-api
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/tableorder
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      JWT_SECRET: your-secret-key-change-in-production
    ports:
      - "8080:8080"
    depends_on:
      db:
        condition: service_healthy
    networks:
      - tableorder-network

volumes:
  postgres-data:

networks:
  tableorder-network:
    driver: bridge
```

---

## Dockerfile

### Backend Dockerfile
```dockerfile
# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## Database Schema

### Tables

#### admin
```sql
CREATE TABLE admin (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    login_attempts INT DEFAULT 0,
    locked_until TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES store(id)
);

CREATE INDEX idx_admin_username ON admin(username);
```

#### orders
```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL,
    table_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES store(id),
    FOREIGN KEY (table_id) REFERENCES "table"(id),
    FOREIGN KEY (session_id) REFERENCES table_session(id)
);

CREATE INDEX idx_orders_session_id ON orders(session_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_order_number ON orders(order_number);
```

#### table_session
```sql
CREATE TABLE table_session (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL,
    table_id BIGINT NOT NULL,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT true,
    FOREIGN KEY (store_id) REFERENCES store(id),
    FOREIGN KEY (table_id) REFERENCES "table"(id)
);

CREATE INDEX idx_table_session_table_active ON table_session(table_id, is_active);
```

---

## Security Infrastructure

### JWT Configuration
- **Secret Key**: Environment variable `JWT_SECRET`
- **Algorithm**: HS256
- **Expiration**: 16 hours (57600000 ms)
- **Storage**: SessionStorage (Frontend)

### Password Hashing
- **Algorithm**: BCrypt
- **Rounds**: 10 (default)
- **Implementation**: BCryptPasswordEncoder

### CORS
- **Allowed Origins**: `*` (개발 환경)
- **Allowed Methods**: `*`
- **Allowed Headers**: `*`

---

## Monitoring and Logging

### Logging
- **Framework**: SLF4J + Logback
- **Level**: DEBUG (개발 환경)
- **Output**: Console
- **Format**: 
  ```
  %d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
  ```

### Health Check
- **Endpoint**: `/actuator/health`
- **Implementation**: Spring Boot Actuator
- **Response**:
  ```json
  {
    "status": "UP"
  }
  ```

---

## Deployment Steps

### 1. Build and Start
```bash
# Start all services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f admin-api
```

### 2. Database Initialization
```bash
# Connect to PostgreSQL
docker exec -it tableorder-db psql -U postgres -d tableorder

# Run schema creation scripts
\i /path/to/schema.sql
```

### 3. Verify Deployment
```bash
# Health check
curl http://localhost:8080/actuator/health

# Test login endpoint
curl -X POST http://localhost:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

### 4. Stop Services
```bash
docker-compose down

# Remove volumes (data loss)
docker-compose down -v
```

---

## Infrastructure Summary

| Component | Technology | Port | Resources | Purpose |
|-----------|-----------|------|-----------|---------|
| Admin API | Spring Boot 3.x | 8080 | 512MB | REST API + SSE |
| Database | PostgreSQL 15 | 5432 | 256MB | Data storage |
| Network | Docker Bridge | - | - | Container communication |
| Volume | Docker Volume | - | - | Data persistence |

---

## Scalability Considerations

### Current Setup (Single Instance)
- **Capacity**: 1 관리자, 10 테이블
- **Sufficient**: 소규모 매장

### Future Scaling (If Needed)
- **Horizontal Scaling**: 
  - Multiple API instances behind load balancer
  - Stateless design (JWT) supports scaling
- **Database Scaling**:
  - Read replicas for query performance
  - Connection pooling already configured
- **SSE Scaling**:
  - Sticky sessions or Redis Pub/Sub for multi-instance

---

## Backup and Recovery

### Current Setup
- **Backup**: None (개발 환경)
- **Data Persistence**: Docker volume

### Production Recommendations
- **Daily Backups**: PostgreSQL pg_dump
- **Backup Retention**: 30 days
- **Recovery Time Objective (RTO)**: 1 hour
- **Recovery Point Objective (RPO)**: 24 hours
