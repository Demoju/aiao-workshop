# NFR Requirements - Unit 2 (Backend Customer API)

## Performance Requirements

### PR-1: API Response Time
- **Target**: 2초 이내
- **Scope**: 모든 Customer API 엔드포인트
- **Measurement**: 95th percentile response time
- **Strategy**: 
  - Database 인덱스 최적화
  - Connection pool 효율적 관리
  - 불필요한 쿼리 제거

### PR-2: Database Query Optimization
- **Requirement**: 인덱스 설계 필수
- **Strategy**:
  - Primary key, Foreign key 인덱스
  - 자주 조회되는 컬럼 인덱스 (tableId, sessionId, status)
  - Composite index 고려 (주문 조회 시)
- **Tools**: EXPLAIN ANALYZE로 쿼리 성능 분석

### PR-3: Connection Pool
- **Configuration**: HikariCP 기본 설정 사용
- **Rationale**: Spring Boot 기본값이 대부분의 경우 최적
- **Monitoring**: Connection pool 메트릭 모니터링

## Scalability Requirements

### SC-1: Concurrent Requests
- **Expected Load**: 10개 이하 동시 요청
- **Target Environment**: 소규모 매장 (1-2개 테이블)
- **Strategy**: 단일 서버로 충분

### SC-2: Horizontal Scaling
- **Current**: 단일 서버
- **Future**: 필요 시 확장 가능하도록 Stateless 설계
- **Note**: Session 기반 인증이지만 DB 세션 저장으로 확장 가능

## Availability Requirements

### AV-1: Service Uptime
- **Target**: 99% (연간 3.65일 다운타임 허용)
- **Rationale**: 로컬 배포이지만 영업 시간 중 안정성 중요
- **Strategy**: 
  - Health check 엔드포인트 제공
  - 에러 모니터링 및 알림

### AV-2: Health Check
- **Endpoint**: `/actuator/health`
- **Type**: 기본 health check
- **Checks**: 
  - Application 상태
  - Database 연결 상태
- **Response Time**: 1초 이내

## Security Requirements

### SE-1: API Authentication
- **Customer API**: Session 기반 인증
  - 테이블 로그인 후 세션 생성
  - 세션 ID를 Cookie 또는 Header로 전달
- **Admin API**: JWT 기반 인증
  - 관리자 로그인 후 JWT 발급
  - Authorization: Bearer {token}
- **Rationale**: 고객은 간단한 세션, 관리자는 보안 강화

### SE-2: HTTPS
- **Development**: HTTP 허용
- **Production**: HTTPS 필수
- **Strategy**: 
  - 프로덕션 환경에서 SSL/TLS 인증서 적용
  - Spring Boot HTTPS 설정

### SE-3: SQL Injection Prevention
- **Primary Defense**: MyBatis Prepared Statement
- **Rationale**: 
  - MyBatis는 자동으로 Prepared Statement 사용
  - SQL 파라미터 바인딩으로 Injection 방지
- **Additional**: 
  - 입력 검증은 비즈니스 로직 오류 방지용
  - SQL Injection은 MyBatis가 처리

### SE-4: Password Hashing
- **Algorithm**: bcrypt
- **Strength**: 10 rounds (기본값)
- **Library**: Spring Security BCryptPasswordEncoder
- **Rationale**: 
  - 업계 표준
  - Salt 자동 생성
  - Rainbow table 공격 방지

## Reliability Requirements

### RE-1: Transaction Management
- **Strategy**: @Transactional 어노테이션
- **Scope**: 
  - 주문 생성 (Order + OrderItem 동시 저장)
  - 주문 취소 (상태 변경 + 이력 기록)
- **Isolation Level**: READ_COMMITTED (기본값)
- **Rollback**: RuntimeException 발생 시 자동 롤백

### RE-2: Error Logging
- **Framework**: SLF4J + Logback
- **Configuration**:
  - 파일 로테이션: 일일 로테이션
  - 보관 기간: 1년
  - 1년 후 자동 삭제
- **Log Levels**:
  - ERROR: 시스템 오류, 예외
  - WARN: 비즈니스 로직 경고
  - INFO: 주요 비즈니스 이벤트
  - DEBUG: 개발 디버깅 (프로덕션 비활성화)

### RE-3: Database Backup
- **Strategy**: 자동 백업 (일일)
- **Schedule**: 매일 새벽 2시
- **Retention**: 7일
- **Tools**: PostgreSQL pg_dump 또는 클라우드 자동 백업
- **Rationale**: 로컬 배포이지만 데이터 손실 방지 중요

## Maintainability Requirements

### MA-1: Code Quality
- **Tools**: Checkstyle
- **Configuration**: Google Java Style Guide
- **Enforcement**: 빌드 시 자동 검사
- **Rationale**: 
  - 코드 일관성 유지
  - SpotBugs는 추가하지 않음 (복잡도 낮음)

### MA-2: API Documentation
- **Tool**: Swagger/OpenAPI
- **UI**: Swagger UI (`/swagger-ui.html`)
- **Spec**: OpenAPI 3.0
- **Content**:
  - 모든 엔드포인트 문서화
  - Request/Response 예시
  - 에러 코드 설명
- **Rationale**: Frontend 팀과 협업 용이

### MA-3: Testing Strategy
- **Scope**: Unit 테스트만
- **Framework**: JUnit 5 + Mockito
- **Coverage Target**: 70% 이상
- **Focus**:
  - Service 레이어 비즈니스 로직
  - 중요 유틸리티 메서드
- **Rationale**: 
  - Integration 테스트는 시간 제약으로 생략
  - Unit 테스트로 핵심 로직 검증

## Tech Stack Alignment

### Frontend Integration
- **API Response Time**: Unit 1은 3초 기대, Unit 2는 2초 목표 → ✅ 충족
- **HTTPS**: 둘 다 개발 HTTP, 프로덕션 HTTPS → ✅ 일치
- **Authentication**: 
  - Customer: Session (Unit 1 SessionStorage와 호환)
  - Admin: JWT (Unit 1 SessionStorage와 호환)

### Database
- **Connection Pool**: HikariCP (Spring Boot 기본)
- **ORM**: MyBatis (요구사항)
- **Database**: PostgreSQL

## Summary

| Category | Requirement | Target/Strategy |
|----------|-------------|-----------------|
| Performance | API Response Time | 2초 이내 |
| Performance | DB Optimization | 인덱스 설계 |
| Performance | Connection Pool | HikariCP 기본값 |
| Scalability | Concurrent Requests | 10개 이하 |
| Scalability | Horizontal Scaling | 단일 서버 |
| Availability | Uptime | 99% |
| Availability | Health Check | 기본 health check |
| Security | Authentication | Session (고객) + JWT (관리자) |
| Security | HTTPS | 프로덕션 필수 |
| Security | SQL Injection | MyBatis Prepared Statement |
| Security | Password Hashing | bcrypt |
| Reliability | Transaction | @Transactional |
| Reliability | Logging | SLF4J + Logback + 로테이션 (1년) |
| Reliability | Backup | 자동 백업 (일일) |
| Maintainability | Code Quality | Checkstyle |
| Maintainability | API Docs | Swagger/OpenAPI |
| Maintainability | Testing | Unit 테스트 (70%+) |

