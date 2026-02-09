# Unit of Work Dependencies - 테이블오더 서비스

## Dependency Matrix

| Unit | Unit 1 (Frontend) | Unit 2 (Customer API) | Unit 3 (Admin API) | PostgreSQL | Shared Components |
|------|-------------------|----------------------|-------------------|------------|-------------------|
| **Unit 1 (Frontend)** | - | Depends | Depends | No | No |
| **Unit 2 (Customer API)** | No | - | No | Depends | Depends |
| **Unit 3 (Admin API)** | No | No | - | Depends | Depends |

**Legend**:
- **Depends**: Direct dependency
- **No**: No direct dependency
- **-**: Self

---

## Dependency Details

### Unit 1: Frontend Dependencies

#### External Dependencies
- **Unit 2 (Customer API)**: REST API 호출
  - `GET /api/customer/menus`
  - `GET /api/customer/categories`
  - `POST /api/customer/orders`
  - `GET /api/customer/orders`
  - `DELETE /api/customer/orders/{orderId}`
  - `POST /api/customer/login`

- **Unit 3 (Admin API)**: REST API + SSE
  - `POST /api/admin/login`
  - `GET /api/admin/orders`
  - `PATCH /api/admin/orders/{orderId}/status`
  - `DELETE /api/admin/orders/{orderId}`
  - `POST /api/admin/tables/{tableId}/end-session`
  - `GET /api/admin/tables/{tableId}/past-orders`
  - `GET /api/admin/orders/stream` (SSE)

#### Internal Dependencies
- React Router (라우팅)
- Zustand (상태 관리)
- Axios (HTTP 클라이언트)
- Custom hooks → API services → Backend

#### Development Strategy
- **Mock Backend API** 사용하여 독립 개발
- API 계약 (OpenAPI/Swagger) 먼저 정의
- Mock 서버 또는 MSW (Mock Service Worker) 사용

---

### Unit 2: Backend Customer API Dependencies

#### External Dependencies
- **PostgreSQL Database**: 데이터 저장소
  - Tables: Store, Table, Menu, Category, Order, OrderItem, TableSession

#### Internal Dependencies (Shared Components)
- **Domain Layer**: Store, Table, Menu, Category, Order, OrderItem, TableSession
- **Security Layer**: JwtAuthenticationFilter, JwtTokenProvider
- **Exception Layer**: GlobalExceptionHandler, Custom Exceptions
- **Configuration**: SecurityConfig, MyBatisConfig, WebConfig

#### No Dependencies On
- Unit 1 (Frontend)
- Unit 3 (Admin API)

#### Development Strategy
- 독립적으로 개발 가능
- Database schema 먼저 정의
- MyBatis Mapper XML 작성

---

### Unit 3: Backend Admin API Dependencies

#### External Dependencies
- **PostgreSQL Database**: 데이터 저장소
  - Tables: Admin, Order, OrderItem, TableSession, Table

#### Internal Dependencies (Shared Components)
- **Domain Layer**: Admin, Order, OrderItem, TableSession, Table
- **Security Layer**: JwtAuthenticationFilter, JwtTokenProvider, PasswordEncoder
- **Exception Layer**: GlobalExceptionHandler, Custom Exceptions
- **Configuration**: SecurityConfig, MyBatisConfig, WebConfig

#### No Dependencies On
- Unit 1 (Frontend)
- Unit 2 (Customer API)

#### Development Strategy
- 독립적으로 개발 가능
- SSE (SseEmitterManager) 구현
- Database schema 먼저 정의
- MyBatis Mapper XML 작성

---

## Integration Points

### Frontend ↔ Backend Customer API

#### Integration Type
REST API (JSON over HTTP)

#### Communication Pattern
- **Request**: HTTP POST/GET/DELETE
- **Response**: JSON
- **Authentication**: JWT token (SessionStorage)
- **Error Handling**: HTTP status codes + ErrorResponseDto

#### Integration Steps
1. API 계약 정의 (OpenAPI/Swagger)
2. Frontend Mock 제거
3. Axios base URL 설정
4. CORS 설정 (Backend)
5. 통합 테스트

---

### Frontend ↔ Backend Admin API

#### Integration Type
REST API + SSE

#### Communication Pattern
- **REST API**: HTTP POST/GET/PATCH/DELETE
- **SSE**: EventSource (Server-Sent Events)
- **Authentication**: JWT token (SessionStorage)
- **Error Handling**: HTTP status codes + ErrorResponseDto

#### Integration Steps
1. API 계약 정의 (OpenAPI/Swagger)
2. Frontend Mock 제거
3. Axios base URL 설정
4. SSE EventSource 연결
5. CORS 설정 (Backend)
6. 통합 테스트

---

### Backend Units ↔ PostgreSQL

#### Integration Type
MyBatis (SQL Mapper)

#### Communication Pattern
- **Mapper Interface**: Java interface
- **Mapper XML**: SQL queries
- **Connection Pool**: HikariCP

#### Integration Steps
1. Database schema 생성
2. MyBatis Mapper Interface 작성
3. MyBatis Mapper XML 작성
4. application.yml 설정
5. Mapper 테스트

---

### Backend Units ↔ Shared Components

#### Integration Type
Java package import

#### Communication Pattern
- **Domain**: Entity classes
- **Security**: JWT filter, token provider
- **Exception**: Global exception handler
- **Configuration**: Spring configuration classes

#### Integration Steps
1. Shared components 먼저 구현
2. Unit 2, Unit 3에서 import
3. 의존성 주입 (Spring DI)

---

## Development Sequence

### Recommended Order

#### Step 1: Foundation (Parallel)
```
[Shared Components]
    ├── Domain entities
    ├── Security (JWT)
    ├── Exception handling
    └── Configuration

[Database Schema]
    └── PostgreSQL tables

[API Contract]
    └── OpenAPI/Swagger spec
```

#### Step 2: Backend Development (Parallel)
```
[Unit 2: Customer API]
    ├── CustomerController
    ├── CustomerService
    ├── CustomerMapper
    └── Unit tests

[Unit 3: Admin API]
    ├── AdminController
    ├── AdminService
    ├── AdminMapper
    ├── SseEmitterManager
    └── Unit tests
```

#### Step 3: Frontend Development (Parallel with Backend)
```
[Unit 1: Frontend]
    ├── Customer domain (with Mock)
    ├── Admin domain (with Mock)
    ├── Shared components
    ├── Custom hooks
    └── Unit tests
```

#### Step 4: Integration
```
[Frontend ↔ Backend]
    ├── Remove mocks
    ├── Connect to real APIs
    ├── Test REST API calls
    ├── Test SSE connection
    └── Integration tests
```

#### Step 5: Build and Test
```
[Full System]
    ├── Docker Compose setup
    ├── Build all units
    ├── Run all tests
    └── Local deployment test
```

---

## Dependency Graph

```
                    [PostgreSQL Database]
                            |
                            | (SQL)
                            |
        +-------------------+-------------------+
        |                                       |
        v                                       v
[Unit 2: Customer API]              [Unit 3: Admin API]
        |                                       |
        | (Shared Components)                   | (Shared Components)
        |                                       |
        +-------------------+-------------------+
                            |
                            | (REST API + SSE)
                            |
                            v
                    [Unit 1: Frontend]
```

---

## Mock Strategy for Parallel Development

### Frontend Mock (Unit 1)

#### Option 1: MSW (Mock Service Worker)
```typescript
// mocks/handlers.ts
import { rest } from 'msw';

export const handlers = [
  rest.get('/api/customer/menus', (req, res, ctx) => {
    return res(ctx.json([
      { menuId: 1, menuName: 'Pizza', price: 15000 }
    ]));
  }),
  
  rest.post('/api/customer/orders', (req, res, ctx) => {
    return res(ctx.json({
      orderId: 1,
      orderNumber: 'ORD-001',
      status: 'PENDING'
    }));
  })
];
```

#### Option 2: Axios Mock Adapter
```typescript
// mocks/api.ts
import MockAdapter from 'axios-mock-adapter';
import axios from 'axios';

const mock = new MockAdapter(axios);

mock.onGet('/api/customer/menus').reply(200, [
  { menuId: 1, menuName: 'Pizza', price: 15000 }
]);

mock.onPost('/api/customer/orders').reply(200, {
  orderId: 1,
  orderNumber: 'ORD-001',
  status: 'PENDING'
});
```

---

## CORS Configuration

### Backend CORS Setup

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000") // Frontend URL
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

---

## API Contract (OpenAPI/Swagger)

### Contract-First Approach

1. **Define API spec** (OpenAPI 3.0)
2. **Generate TypeScript types** (Frontend)
3. **Generate Java DTOs** (Backend - optional)
4. **Implement endpoints** (Backend)
5. **Implement API calls** (Frontend)

### Example OpenAPI Spec
```yaml
openapi: 3.0.0
info:
  title: Table Order API
  version: 1.0.0
paths:
  /api/customer/menus:
    get:
      summary: Get menus
      parameters:
        - name: categoryId
          in: query
          schema:
            type: integer
      responses:
        '200':
          description: Success
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/MenuResponseDto'
```

---

## Testing Integration Points

### REST API Integration Test

```java
@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testCreateOrder() throws Exception {
        mockMvc.perform(post("/api/customer/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tableId\":1,\"items\":[]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists());
    }
}
```

### SSE Integration Test

```typescript
// Frontend SSE test
test('should receive SSE events', (done) => {
  const eventSource = new EventSource('/api/admin/orders/stream');
  
  eventSource.addEventListener('new-order', (event) => {
    const order = JSON.parse(event.data);
    expect(order.orderId).toBeDefined();
    eventSource.close();
    done();
  });
});
```

---

## Notes

- Unit 2와 Unit 3는 서로 독립적 (직접 의존성 없음)
- Frontend는 Backend Mock을 사용하여 병렬 개발
- Shared components는 Backend 내부에서만 공유
- API 계약을 먼저 정의하여 Frontend/Backend 병렬 개발 가능
- CORS 설정 필수 (Frontend와 Backend 별도 배포)
