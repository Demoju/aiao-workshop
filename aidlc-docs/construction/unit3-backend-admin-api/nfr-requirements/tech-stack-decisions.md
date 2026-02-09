# Tech Stack Decisions - Unit 3 (Backend Admin API)

## Overview
Unit 3의 기술 스택 선택 및 근거

---

## Core Framework

### Java 17+
- **Rationale**: 
  - LTS 버전
  - 최신 언어 기능 (Records, Pattern Matching)
  - 성능 향상
- **Version**: 17 (LTS)

### Spring Boot 3.x
- **Rationale**:
  - 엔터프라이즈급 프레임워크
  - 풍부한 생태계
  - Auto-configuration
  - Production-ready features (Actuator)
- **Version**: 3.2.x

---

## Database

### PostgreSQL
- **Rationale**:
  - 오픈소스 관계형 DB
  - ACID 트랜잭션 지원
  - 풍부한 데이터 타입
  - 성능 및 확장성
- **Version**: 15.x

### MyBatis
- **Rationale**:
  - SQL 제어 가능
  - 복잡한 쿼리 작성 용이
  - XML 기반 매핑
  - Spring Boot 통합 간편
- **Version**: 3.0.x
- **Alternative**: JPA/Hibernate (고려 가능)

---

## Security

### Spring Security
- **Rationale**:
  - 표준 보안 프레임워크
  - JWT 통합 용이
  - Filter 기반 인증/인가
- **Version**: 6.x (Spring Boot 3.x 포함)

### JWT (JSON Web Token)
- **Library**: jjwt (io.jsonwebtoken)
- **Algorithm**: HS256
- **Rationale**:
  - Stateless 인증
  - 확장성
  - 표준 기술
- **Version**: 0.12.x

### BCrypt
- **Implementation**: BCryptPasswordEncoder (Spring Security)
- **Rationale**:
  - 안전한 비밀번호 해싱
  - Salt 자동 생성
  - 업계 표준

---

## SSE (Server-Sent Events)

### Spring WebFlux SseEmitter
- **Rationale**:
  - Spring Boot 내장
  - 간단한 API
  - HTTP 기반 (방화벽 친화적)
- **Alternative**: WebSocket (양방향 통신 필요 시)

---

## Build Tool

### Maven
- **Rationale**:
  - 표준 빌드 도구
  - 의존성 관리 용이
  - 풍부한 플러그인
- **Version**: 3.9.x
- **Alternative**: Gradle (고려 가능)

---

## Testing

### JUnit 5
- **Rationale**:
  - Java 표준 테스트 프레임워크
  - 풍부한 어노테이션
  - Spring Boot 통합
- **Version**: 5.10.x

### Mockito
- **Rationale**:
  - Mock 객체 생성
  - Service 레이어 테스트
  - Spring Boot 통합
- **Version**: 5.x

### Spring Boot Test
- **Components**:
  - @SpringBootTest
  - @WebMvcTest (Controller 테스트)
  - @DataJpaTest (Mapper 테스트)
- **Rationale**: Spring 컨텍스트 통합 테스트

---

## Logging

### SLF4J + Logback
- **Rationale**:
  - Spring Boot 기본 로깅
  - 유연한 설정
  - 성능
- **Version**: Spring Boot 기본 포함

---

## Connection Pooling

### HikariCP
- **Rationale**:
  - Spring Boot 기본 Connection Pool
  - 빠른 성능
  - 안정성
- **Configuration**:
  ```yaml
  spring:
    datasource:
      hikari:
        minimum-idle: 5
        maximum-pool-size: 11
        connection-timeout: 30000
        idle-timeout: 600000
        max-lifetime: 1800000
  ```

---

## Development Tools

### Lombok
- **Rationale**:
  - 보일러플레이트 코드 감소
  - @Data, @Builder, @Slf4j
- **Version**: 1.18.x

### Spring Boot DevTools
- **Rationale**:
  - 자동 재시작
  - LiveReload
  - 개발 생산성 향상

---

## API Documentation (Optional)

### Swagger/OpenAPI
- **Library**: springdoc-openapi
- **Rationale**:
  - API 문서 자동 생성
  - 테스트 UI 제공
- **Version**: 2.x
- **Note**: 선택 사항

---

## Deployment

### Docker
- **Rationale**:
  - 환경 일관성
  - 배포 간소화
- **Base Image**: eclipse-temurin:17-jre-alpine

### Docker Compose
- **Rationale**:
  - 로컬 개발 환경
  - PostgreSQL + Backend 통합
- **Services**: PostgreSQL, Backend API

---

## Tech Stack Summary

| Category | Technology | Version | Rationale |
|----------|-----------|---------|-----------|
| Language | Java | 17 (LTS) | 최신 LTS, 성능 |
| Framework | Spring Boot | 3.2.x | 엔터프라이즈급 |
| Database | PostgreSQL | 15.x | 관계형 DB, ACID |
| ORM | MyBatis | 3.0.x | SQL 제어 |
| Security | Spring Security | 6.x | 표준 보안 |
| Auth | JWT (jjwt) | 0.12.x | Stateless 인증 |
| SSE | SseEmitter | Built-in | 실시간 푸시 |
| Build | Maven | 3.9.x | 표준 빌드 |
| Testing | JUnit 5 + Mockito | 5.x | 표준 테스트 |
| Logging | SLF4J + Logback | Built-in | Spring Boot 기본 |
| Connection Pool | HikariCP | Built-in | 빠른 성능 |
| DevTools | Lombok | 1.18.x | 코드 간소화 |

---

## Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- MyBatis -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>3.0.3</version>
    </dependency>
    
    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Spring Boot DevTools -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Actuator (Optional) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

---

## Configuration (application.yml)

```yaml
spring:
  application:
    name: tableorder-admin-api
  
  datasource:
    url: jdbc:postgresql://localhost:5432/tableorder
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 11
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  security:
    jwt:
      secret: ${JWT_SECRET:your-secret-key-change-in-production}
      expiration: 57600000  # 16 hours in milliseconds

mybatis:
  mapper-locations: classpath:mappers/**/*.xml
  type-aliases-package: com.tableorder.domain
  configuration:
    map-underscore-to-camel-case: true

logging:
  level:
    root: DEBUG
    com.tableorder: DEBUG

server:
  port: 8080
```
