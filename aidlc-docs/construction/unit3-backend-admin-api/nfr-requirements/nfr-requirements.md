# NFR Requirements - Unit 3 (Backend Admin API)

## Overview
Unit 3의 비기능 요구사항 정의

---

## Performance Requirements

### PR-1: API Response Time
- **Target**: 500ms 이하
- **Measurement**: 95th percentile
- **Strategy**: 
  - 인덱스 최적화
  - 현재 세션 주문만 조회
  - Connection pooling

### PR-2: Database Connection Pool
- **Size**: 11개 (최소 필요량)
- **Configuration**:
  ```yaml
  spring:
    datasource:
      hikari:
        minimum-idle: 5
        maximum-pool-size: 11
  ```
- **Rationale**: 테이블 10개 + 관리자 1명 = 최대 11개 동시 연결

### PR-3: Query Optimization
- **Strategy**: 현재 세션 주문만 조회
- **Implementation**: 
  ```sql
  WHERE sessionId IN (
    SELECT id FROM table_session WHERE isActive = true
  )
  ```

---

## Scalability Requirements

### SC-1: Concurrent Users
- **Expected**: 1명 (관리자)
- **Max Load**: 11개 동시 요청 (테이블 10 + 관리자 1)
- **Strategy**: Stateless API, 수평 확장 가능 (필요시)

### SC-2: SSE Connection Limit
- **Limit**: 1개 (관리자 1명)
- **Strategy**: 단일 연결 유지
- **Implementation**: SseEmitterManager에 연결 수 제한 없음

---

## Availability Requirements

### AV-1: Uptime
- **Target**: Best effort (개발 단계)
- **Strategy**: 기본 에러 처리

### AV-2: Data Backup
- **Strategy**: 백업 없음 (개발 단계)
- **Note**: 프로덕션 시 일일 백업 권장

---

## Security Requirements

### SE-1: SSL/TLS
- **Requirement**: 불필요 (개발 환경)
- **Note**: 프로덕션 시 HTTPS 필수

### SE-2: CORS
- **Configuration**: 모든 origin 허용 (개발용)
- **Implementation**:
  ```java
  @CrossOrigin(origins = "*")
  ```
- **Note**: 프로덕션 시 특정 도메인만 허용

### SE-3: JWT Security
- **Algorithm**: HS256
- **Secret Key**: 강력한 비밀 키 사용
- **Expiration**: 16시간

### SE-4: Password Hashing
- **Algorithm**: bcrypt
- **Implementation**: BCryptPasswordEncoder

### SE-5: Account Locking
- **Trigger**: 5회 로그인 실패
- **Duration**: 30분

---

## Reliability Requirements

### RE-1: Error Handling
- **Strategy**: 기본 Spring Boot 에러 핸들러
- **Format**: 간단한 문자열 메시지
- **Example**:
  ```json
  {
    "error": "Error message"
  }
  ```

### RE-2: Logging
- **Level**: DEBUG (개발 단계)
- **Configuration**:
  ```yaml
  logging:
    level:
      root: DEBUG
      com.tableorder: DEBUG
  ```
- **Note**: 프로덕션 시 INFO 레벨 권장

### RE-3: API Rate Limiting
- **Strategy**: 제한 없음 (개발 단계)
- **Note**: 프로덕션 시 Rate Limiting 권장

---

## Maintainability Requirements

### MA-1: Code Quality
- **Language**: Java 17+
- **Framework**: Spring Boot 3.x
- **Build Tool**: Maven or Gradle
- **Code Style**: Google Java Style Guide

### MA-2: Testing
- **Requirement**: Unit 테스트만
- **Framework**: JUnit 5 + Mockito
- **Coverage Target**: 70% 이상
- **Strategy**:
  - Service 레이어 테스트
  - Controller 레이어 테스트 (MockMvc)
  - Mapper 테스트 (MyBatis Test)

### MA-3: Documentation
- **API Documentation**: Swagger/OpenAPI (선택)
- **Code Comments**: 복잡한 비즈니스 로직만
- **README**: 프로젝트 설정 및 실행 방법

---

## Monitoring Requirements

### MO-1: Monitoring
- **Strategy**: 로그만 (개발 단계)
- **Implementation**: 콘솔 로그 출력
- **Note**: 프로덕션 시 메트릭 수집 권장

### MO-2: Health Check
- **Endpoint**: `/actuator/health` (Spring Boot Actuator)
- **Strategy**: 기본 헬스체크

---

## Summary Table

| Category | Requirement | Target | Priority |
|----------|-------------|--------|----------|
| Performance | API Response Time | 500ms 이하 | High |
| Performance | DB Connection Pool | 11개 | High |
| Scalability | Concurrent Users | 1명 (관리자) | Medium |
| Scalability | SSE Connections | 1개 | Medium |
| Security | JWT Expiration | 16시간 | High |
| Security | Account Locking | 5회 실패 시 30분 | High |
| Security | SSL/TLS | 불필요 (개발) | Low |
| Reliability | Error Handling | 기본 핸들러 | Medium |
| Reliability | Logging | DEBUG 레벨 | Medium |
| Maintainability | Testing | Unit 테스트만 | High |
| Monitoring | Strategy | 로그만 | Low |
