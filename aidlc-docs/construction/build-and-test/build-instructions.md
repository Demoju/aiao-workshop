# Build Instructions - Table Order Admin API

## Prerequisites
- **Build Tool**: Maven 3.9+
- **Java**: JDK 17+
- **Docker**: 20.10+ (선택사항)
- **PostgreSQL**: 15+ (로컬 실행 시)

## Build Steps

### 1. Install Dependencies
```bash
cd backend
./mvnw clean install -DskipTests
```

### 2. Configure Environment
```bash
# PostgreSQL 연결 정보 (application.yml에서 설정)
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tableorder
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export SPRING_SECURITY_JWT_SECRET=your-secret-key-change-in-production-min-256-bits-long
```

### 3. Build All Units
```bash
# Maven 빌드
./mvnw clean package

# 또는 Docker 이미지 빌드
docker build -t tableorder-admin-api .
```

### 4. Verify Build Success
- **Expected Output**: `BUILD SUCCESS`
- **Build Artifacts**: `target/admin-api-1.0.0.jar`
- **Common Warnings**: Lombok 관련 경고는 정상

## Docker Compose로 빌드 및 실행

### 전체 스택 실행
```bash
# 프로젝트 루트에서
docker-compose up -d

# 로그 확인
docker-compose logs -f admin-api

# 상태 확인
docker-compose ps
```

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

## Troubleshooting

### Build Fails with Dependency Errors
- **Cause**: Maven 의존성 다운로드 실패
- **Solution**: 
  ```bash
  ./mvnw dependency:purge-local-repository
  ./mvnw clean install
  ```

### Build Fails with Compilation Errors
- **Cause**: Java 버전 불일치
- **Solution**: 
  ```bash
  java -version  # JDK 17 확인
  export JAVA_HOME=/path/to/jdk17
  ```

### Docker Build Fails
- **Cause**: Docker daemon 미실행
- **Solution**: 
  ```bash
  sudo systemctl start docker
  docker ps  # 확인
  ```
