# Tech Stack Decisions - Unit 2 (Backend Customer API)

## Core Framework

### Java 17 (LTS)
- **Rationale**: 
  - 장기 지원 버전 (2029년까지)
  - 현대적인 언어 기능 (Records, Pattern Matching, Text Blocks)
  - Spring Boot 3.x와 최적 호환
- **Version**: 17.x

### Spring Boot 3.x
- **Rationale**:
  - 최신 안정 버전
  - Jakarta EE 9+ 지원
  - Native Image 지원 (향후 확장)
  - 성능 개선 및 보안 강화
- **Version**: 3.x (최신 stable)

## Build Tool

### Gradle
- **Rationale**:
  - Maven보다 빠른 빌드 속도
  - 유연한 빌드 스크립트
  - Kotlin DSL 지원 (향후 전환 가능)
  - 증분 빌드 지원
- **Version**: 8.x
- **Configuration**: Groovy DSL (표준)

## Database

### PostgreSQL
- **Rationale**: 요구사항
- **Version**: 14.x 이상
- **Features**:
  - ACID 트랜잭션
  - JSON 지원 (향후 확장)
  - 강력한 인덱싱

### MyBatis
- **Rationale**: 요구사항, SQL 제어 필요
- **Version**: 3.5.x
- **mybatis-spring-boot-starter**: 3.0.x
- **Features**:
  - XML 기반 SQL 매핑
  - Dynamic SQL 지원
  - Prepared Statement 자동 사용

### HikariCP
- **Rationale**: Spring Boot 기본 Connection Pool
- **Version**: Spring Boot 내장
- **Configuration**: 기본 설정 사용
- **Features**:
  - 빠른 성능
  - 낮은 오버헤드
  - 자동 Connection 관리

## Security

### Spring Security
- **Rationale**: 
  - 인증/인가 표준 프레임워크
  - BCrypt 비밀번호 해싱 제공
  - Session 및 JWT 지원
- **Version**: Spring Boot 3.x 내장
- **Features**:
  - BCryptPasswordEncoder (비밀번호 해싱)
  - Session 관리 (고객용)
  - JWT 지원 (관리자용)

## Validation

### Spring Validation (jakarta.validation)
- **Rationale**:
  - Spring Boot 기본 제공
  - 선언적 검증 (@Valid, @NotNull, @Size 등)
  - 비즈니스 로직 오류 방지
- **Version**: Spring Boot 3.x 내장
- **Usage**:
  - DTO 필드 검증
  - Controller 메서드 파라미터 검증

## JSON Processing

### Jackson
- **Rationale**:
  - Spring Boot 기본 JSON 라이브러리
  - 빠른 성능
  - 풍부한 어노테이션 지원
- **Version**: Spring Boot 3.x 내장
- **Features**:
  - 자동 직렬화/역직렬화
  - LocalDateTime 지원
  - Custom serializer 지원

## Logging

### SLF4J + Logback
- **Rationale**:
  - Spring Boot 기본 로깅 프레임워크
  - 유연한 설정
  - 파일 로테이션 지원
- **Version**: Spring Boot 3.x 내장
- **Configuration**:
  - 일일 로테이션
  - 1년 보관 후 삭제
  - 로그 레벨: INFO (프로덕션), DEBUG (개발)

## Code Quality

### Checkstyle
- **Rationale**:
  - 코드 스타일 일관성
  - Google Java Style Guide 적용
  - 빌드 시 자동 검사
- **Version**: 10.x
- **Configuration**: Google Java Style Guide
- **Enforcement**: Gradle 빌드 시 검사

### Lombok
- **Rationale**:
  - 보일러플레이트 코드 감소
  - @Data, @Builder, @Slf4j 등 유용한 어노테이션
  - 개발 생산성 향상
- **Version**: 1.18.x
- **Usage**:
  - Entity, DTO에 @Data
  - Builder 패턴에 @Builder
  - 로깅에 @Slf4j

## API Documentation

### Swagger/OpenAPI
- **Rationale**:
  - 자동 API 문서 생성
  - Swagger UI 제공
  - Frontend 팀과 협업 용이
- **Library**: springdoc-openapi-starter-webmvc-ui
- **Version**: 2.x
- **UI Endpoint**: `/swagger-ui.html`
- **Spec Endpoint**: `/v3/api-docs`

## Testing

### JUnit 5
- **Rationale**:
  - Java 표준 테스트 프레임워크
  - 풍부한 어노테이션 (@Test, @BeforeEach 등)
  - Spring Boot 기본 제공
- **Version**: 5.x (Spring Boot 내장)

### Mockito
- **Rationale**:
  - Mock 객체 생성
  - Service 레이어 단위 테스트
  - Spring Boot 기본 제공
- **Version**: 5.x (Spring Boot 내장)

### AssertJ
- **Rationale**:
  - 가독성 높은 assertion
  - 풍부한 assertion 메서드
- **Version**: 3.x (Spring Boot 내장)

## Monitoring

### Spring Boot Actuator
- **Rationale**:
  - Health check 엔드포인트 제공
  - 메트릭 수집
  - 운영 모니터링
- **Version**: Spring Boot 3.x 내장
- **Endpoints**:
  - `/actuator/health` (health check)
  - `/actuator/metrics` (메트릭)

## Development Tools

### Spring Boot DevTools
- **Rationale**:
  - 자동 재시작
  - LiveReload 지원
  - 개발 생산성 향상
- **Version**: Spring Boot 3.x 내장
- **Scope**: development only

## Summary Table

| Category | Technology | Version | Rationale |
|----------|-----------|---------|-----------|
| Language | Java | 17 (LTS) | 장기 지원, 현대적 기능 |
| Framework | Spring Boot | 3.x | 최신 안정 버전 |
| Build Tool | Gradle | 8.x | 빠른 빌드, 유연성 |
| Database | PostgreSQL | 14.x+ | 요구사항, ACID |
| ORM | MyBatis | 3.5.x | 요구사항, SQL 제어 |
| Connection Pool | HikariCP | Spring Boot 내장 | 빠른 성능 |
| Security | Spring Security | Spring Boot 내장 | 인증/인가, bcrypt |
| Validation | Spring Validation | Spring Boot 내장 | 선언적 검증 |
| JSON | Jackson | Spring Boot 내장 | 빠른 성능 |
| Logging | SLF4J + Logback | Spring Boot 내장 | 로테이션 지원 |
| Code Quality | Checkstyle | 10.x | 스타일 일관성 |
| Boilerplate | Lombok | 1.18.x | 생산성 향상 |
| API Docs | Swagger/OpenAPI | 2.x | 자동 문서화 |
| Testing | JUnit 5 | 5.x | 표준 프레임워크 |
| Testing | Mockito | 5.x | Mock 객체 |
| Monitoring | Spring Boot Actuator | Spring Boot 내장 | Health check |

## Dependencies (build.gradle)

```groovy
dependencies {
    // Spring Boot Starters
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    
    // Database
    implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3'
    runtimeOnly 'org.postgresql:postgresql'
    
    // Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    
    // Swagger/OpenAPI
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
    
    // Development
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
}
```

## Alignment with Unit 1 (Frontend)

| Aspect | Unit 1 (Frontend) | Unit 2 (Backend) | Alignment |
|--------|-------------------|------------------|-----------|
| API Response Time | 3초 기대 | 2초 목표 | ✅ 충족 |
| HTTPS | 개발 HTTP, 프로덕션 HTTPS | 개발 HTTP, 프로덕션 HTTPS | ✅ 일치 |
| Authentication | SessionStorage | Session + JWT | ✅ 호환 |
| Testing | Unit (TDD) | Unit (TDD) | ✅ 일치 |
| Code Quality | ESLint, Prettier | Checkstyle | ✅ 각 언어 표준 |
| API Docs | N/A | Swagger | ✅ Frontend 협업 |

