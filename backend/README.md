# Table Order Admin API - Backend

## 프로젝트 개요
테이블오더 서비스의 관리자용 Backend API (Unit 3)

## 기술 스택
- Java 17
- Spring Boot 3.2.2
- MyBatis 3.0.3
- PostgreSQL 15
- JWT (jjwt 0.12.5)
- Maven

## 프로젝트 구조
```
backend/
├── src/main/java/com/tableorder/
│   ├── domain/          # 도메인 엔티티
│   ├── dto/             # Data Transfer Objects
│   ├── service/         # 비즈니스 로직 (TODO)
│   ├── controller/      # REST API 컨트롤러 (TODO)
│   ├── mapper/          # MyBatis 매퍼 (TODO)
│   ├── security/        # JWT 보안 (TODO)
│   ├── exception/       # 예외 처리
│   └── config/          # 설정
├── src/main/resources/
│   ├── application.yml  # 애플리케이션 설정
│   └── mappers/         # MyBatis XML (TODO)
└── pom.xml              # Maven 의존성
```

## 현재 상태
✅ 완료:
- 프로젝트 구조
- Domain Entities (Order, Admin, TableSession, OrderStatus)
- DTOs (AdminLoginRequestDto, AdminLoginResponseDto, OrderResponseDto, ErrorResponse)
- Exceptions (AuthenticationException, AccountLockedException, OrderNotFoundException, InvalidStatusTransitionException)
- GlobalExceptionHandler
- **AdminService** (login, getOrders, updateOrderStatus, deleteOrder)
- **AdminController** (4 REST endpoints)
- **Mappers** (AdminMapper, OrderMapper, TableSessionMapper + XML)
- **JwtTokenProvider** (JWT 생성/검증)
- SecurityConfig (PasswordEncoder)
- **Database Schema** (schema.sql)
- **Dockerfile**
- **docker-compose.yml**
- Main Application Class

⏳ TODO (선택적 - 추가 기능):
- TableSessionService (세션 종료, 과거 주문 조회)
- SseEmitterManager (SSE 실시간 업데이트)
- OrderEventPublisher
- 추가 Controller 엔드포인트 (세션 관리, SSE)
- Unit Tests
- Sample data (data.sql)

## 빌드 및 실행

### 빌드
```bash
cd backend
./mvnw clean package
```

### 실행
```bash
java -jar target/admin-api-1.0.0.jar
```

### Docker Compose
```bash
docker-compose up -d
```

## API 엔드포인트 (계획)
- `POST /api/admin/login` - 관리자 로그인
- `GET /api/admin/orders` - 주문 목록 조회
- `PATCH /api/admin/orders/{orderId}/status` - 주문 상태 변경
- `DELETE /api/admin/orders/{orderId}` - 주문 삭제
- `POST /api/admin/tables/{tableId}/end-session` - 세션 종료
- `GET /api/admin/tables/{tableId}/past-orders` - 과거 주문 조회
- `GET /api/admin/orders/stream` - SSE 실시간 업데이트

## 다음 단계
1. Service 레이어 구현 (AdminService, TableSessionService)
2. Controller 레이어 구현 (AdminController)
3. Mapper 레이어 구현 (MyBatis XML)
4. Security 구현 (JwtTokenProvider, JwtAuthenticationFilter)
5. Database Schema 작성
6. Unit Tests 작성
7. Docker 설정 완료

## 참고 문서
- Functional Design: `aidlc-docs/construction/unit3-backend-admin-api/functional-design/`
- NFR Requirements: `aidlc-docs/construction/unit3-backend-admin-api/nfr-requirements/`
- Infrastructure Design: `aidlc-docs/construction/unit3-backend-admin-api/infrastructure-design/`
- TDD Plans: `aidlc-docs/construction/plans/unit3-backend-admin-api-*`
