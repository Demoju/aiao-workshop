# Unit Test Execution Instructions

## TDD Artifacts Detection

✅ **TDD artifacts found**:
- `unit3-backend-admin-api-test-plan.md` (50 test cases)
- `unit3-backend-admin-api-contracts.md`

**Note**: TDD 방식으로 시작했으나 일반 방식으로 전환하여 완료. Unit tests는 미구현 상태.

## Unit Test Status

### Unit 3 (Backend Admin API)
- **Total Test Cases**: 50 (계획됨)
- **Implemented**: 0
- **Status**: ⏳ TODO

## Unit Test Execution (구현 시)

### Run All Unit Tests
```bash
cd backend
./mvnw test
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=AdminServiceTest
```

### Run with Coverage
```bash
./mvnw test jacoco:report
# Report: target/site/jacoco/index.html
```

## Test Categories

### Service Layer Tests
- **AdminServiceTest**: login, getOrders, updateOrderStatus, deleteOrder
- **TableSessionServiceTest**: endSession, getPastOrders (미구현)

### Controller Layer Tests
- **AdminControllerTest**: REST API endpoints (미구현)

### Mapper Layer Tests
- **AdminMapperTest**: MyBatis queries (미구현)
- **OrderMapperTest**: MyBatis queries (미구현)
- **TableSessionMapperTest**: MyBatis queries (미구현)

### Security Tests
- **JwtTokenProviderTest**: JWT generation/validation (미구현)

## Test Implementation Guide

### Example: AdminService Login Test
```java
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    
    @Mock
    private AdminMapper adminMapper;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AdminService adminService;
    
    @Test
    void login_Success() {
        // Given
        Admin admin = Admin.builder()
                .id(1L)
                .username("admin")
                .password("$2a$10$hashedPassword")
                .loginAttempts(0)
                .build();
        
        when(adminMapper.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("password", admin.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(admin)).thenReturn("jwt-token");
        
        // When
        AdminLoginResponseDto response = adminService.login("admin", "password");
        
        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(adminMapper).updateLoginAttempts(1L, 0, null);
    }
}
```

## Coverage Target
- **Minimum**: 70%
- **Recommended**: 80%+

## Next Steps
1. Implement unit tests based on test-plan.md
2. Run tests and verify coverage
3. Fix failing tests
4. Proceed to integration tests
