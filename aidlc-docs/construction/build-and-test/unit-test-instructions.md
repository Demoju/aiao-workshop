# Unit Test Execution Instructions

## Unit 1 - Frontend (TDD Complete)

### Run Tests
```bash
cd frontend
npx vitest run
```

### Results
- **Test Files**: 11 passed
- **Tests**: 34 passed, 0 failed
- **Duration**: ~1.7s

| Layer | Tests | Status |
|-------|-------|--------|
| CartStore | 7 | ✅ |
| AuthStore | 2 | ✅ |
| OrderStore | 4 | ✅ |
| LoadingStore | 1 | ✅ |
| Validation | 3 | ✅ |
| Customer API | 6 | ✅ |
| Interceptors | 2 | ✅ |
| useCart | 1 | ✅ |
| useAuth | 2 | ✅ |
| useOrderStream | 2 | ✅ |
| Components | 4 | ✅ |

---

## Unit 3 - Backend Admin API (TDD Planned)

### TDD Artifacts
- ✅ Test plan: `unit3-backend-admin-api-test-plan.md` (50 test cases)
- ✅ Contracts: `unit3-backend-admin-api-contracts.md`

### Run Tests
```bash
cd backend
./mvnw test
```

### Test Categories
- Service Layer: AdminService (login, orders, status, session)
- Controller Layer: AdminController (endpoints, auth, error handling)
- Security: JWT token, authentication filter
- Mapper: MyBatis SQL queries

### Coverage Target: 70%+
