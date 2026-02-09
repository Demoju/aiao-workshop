# TDD Code Generation Plan - Unit 1 (Frontend)

## Unit Context
- **Workspace Root**: /Users/jinwan/Desktop/workspace/aiao-workshop
- **Code Root**: /Users/jinwan/Desktop/workspace/aiao-workshop/frontend
- **Project Type**: Greenfield
- **TDD Approach**: RED → GREEN → REFACTOR per method
- **Test Runner**: Vitest
- **Total Test Cases**: 30

---

### Plan Step 0: Project Setup & Contract Skeletons
- [ ] Initialize Vite + React + TypeScript project
- [ ] Install dependencies (zustand, axios, react-router-dom, tailwindcss, react-i18next, react-hook-form, zod, @hookform/resolvers)
- [ ] Install dev dependencies (vitest, @testing-library/react, @testing-library/jest-dom, @testing-library/user-event, jsdom, msw)
- [ ] Configure Tailwind CSS
- [ ] Configure Vitest (vitest.config.ts)
- [ ] Configure i18n (ko/en)
- [ ] Generate all type definitions (types/index.ts, types/schemas.ts)
- [ ] Generate all file skeletons with NotImplementedError/placeholder

---

### Plan Step 1: Store Layer (TDD)

#### 1.1 CartStore
- [ ] RED: TC-STORE-001 (addToCart 새 메뉴)
- [ ] GREEN: addToCart 기본 구현
- [ ] RED: TC-STORE-002 (addToCart 중복 메뉴)
- [ ] GREEN: 중복 처리 구현
- [ ] RED: TC-STORE-003 (addToCart max 99)
- [ ] GREEN: max 99 제한 구현
- [ ] RED: TC-STORE-004 (updateQuantity)
- [ ] GREEN: updateQuantity 구현
- [ ] RED: TC-STORE-005 (updateQuantity 0 → 삭제)
- [ ] GREEN: 0일 때 삭제 구현
- [ ] RED: TC-STORE-006 (removeItem)
- [ ] GREEN: removeItem 구현
- [ ] RED: TC-STORE-007 (clearCart)
- [ ] GREEN: clearCart 구현
- [ ] REFACTOR: CartStore 전체 리팩토링

#### 1.2 AuthStore
- [ ] RED: TC-STORE-008 (setAuth)
- [ ] GREEN: setAuth 구현
- [ ] RED: TC-STORE-009 (logout)
- [ ] GREEN: logout 구현
- [ ] REFACTOR: AuthStore 리팩토링

#### 1.3 OrderStore
- [ ] RED: TC-STORE-010 (setTables)
- [ ] GREEN: setTables 구현
- [ ] RED: TC-STORE-011 (addOrder)
- [ ] GREEN: addOrder 구현
- [ ] RED: TC-STORE-012 (updateOrderStatus)
- [ ] GREEN: updateOrderStatus 구현
- [ ] RED: TC-STORE-013 (removeOrder)
- [ ] GREEN: removeOrder 구현
- [ ] REFACTOR: OrderStore 리팩토링

#### 1.4 LoadingStore
- [ ] RED: TC-STORE-014 (setLoading)
- [ ] GREEN: setLoading 구현
- [ ] REFACTOR: LoadingStore 리팩토링

---

### Plan Step 2: Validation Layer (TDD)
- [ ] RED: TC-VAL-001 (tableLoginSchema 유효)
- [ ] GREEN: tableLoginSchema 구현
- [ ] RED: TC-VAL-002 (tableLoginSchema 빈 값 실패)
- [ ] GREEN: 검증 로직 확인
- [ ] RED: TC-VAL-003 (adminLoginSchema 유효)
- [ ] GREEN: adminLoginSchema 구현
- [ ] REFACTOR: Schema 리팩토링

---

### Plan Step 3: Service Layer (TDD)
- [ ] Setup MSW (Mock Service Worker) for API mocking
- [ ] RED: TC-API-001 (getMenus)
- [ ] GREEN: customerApi.getMenus 구현
- [ ] RED: TC-API-002 (createOrder)
- [ ] GREEN: customerApi.createOrder 구현
- [ ] RED: TC-API-003 (cancelOrder)
- [ ] GREEN: customerApi.cancelOrder 구현
- [ ] RED: TC-API-004 (tableLogin)
- [ ] GREEN: customerApi.tableLogin 구현
- [ ] RED: TC-API-005 (adminLogin)
- [ ] GREEN: adminApi.adminLogin 구현
- [ ] RED: TC-API-006 (updateOrderStatus)
- [ ] GREEN: adminApi.updateOrderStatus 구현
- [ ] RED: TC-API-007 (JWT interceptor)
- [ ] GREEN: Request interceptor 구현
- [ ] RED: TC-API-008 (401 → logout)
- [ ] GREEN: Response interceptor 구현
- [ ] REFACTOR: Service layer 리팩토링

---

### Plan Step 4: Hook Layer (TDD)
- [ ] RED: TC-HOOK-001 (useCart)
- [ ] GREEN: useCart 구현
- [ ] RED: TC-HOOK-002 (useAuth 테이블 로그인)
- [ ] GREEN: useAuth 구현
- [ ] RED: TC-HOOK-003 (useAuth 관리자 로그인)
- [ ] GREEN: admin 로그인 로직 추가
- [ ] RED: TC-HOOK-004 (useOrderStream SSE)
- [ ] GREEN: useOrderStream 구현
- [ ] RED: TC-HOOK-005 (useOrderStream 재연결 max 3)
- [ ] GREEN: 재연결 로직 구현
- [ ] REFACTOR: Hook layer 리팩토링

---

### Plan Step 5: Component Layer (TDD)
- [ ] RED: TC-COMP-001 (MenuCard 렌더링)
- [ ] GREEN: MenuCard 구현
- [ ] RED: TC-COMP-002 (MenuCard 추가 버튼)
- [ ] GREEN: 버튼 이벤트 구현
- [ ] RED: TC-COMP-003 (CartItem 수량 버튼)
- [ ] GREEN: CartItem 구현
- [ ] RED: TC-COMP-004 (OrderCard 렌더링)
- [ ] GREEN: OrderCard 구현
- [ ] REFACTOR: Component layer 리팩토링
- [ ] Shared components 구현 (ErrorBoundary, LoadingSpinner, ConfirmDialog, LazyImage)

---

### Plan Step 6: Page Layer & Routing
- [ ] Customer pages 구현 (MenuPage, CartPage, OrderHistoryPage, TableLoginPage)
- [ ] Admin pages 구현 (AdminLoginPage, AdminDashboardPage, TableManagementPage, MenuManagementPage)
- [ ] Admin components 구현 (TableCard, OrderDetailModal, PastOrdersModal)
- [ ] App.tsx (Router + lazy loading + ErrorBoundary)
- [ ] Header component 구현

---

### Plan Step 7: Deployment Artifacts
- [ ] Dockerfile (multi-stage: node → nginx)
- [ ] nginx.conf (SPA fallback, API proxy, SSE proxy)
- [ ] vite.config.ts (proxy 설정)
- [ ] .env.example

---

### Plan Step 8: Documentation
- [ ] Code summary → aidlc-docs/construction/unit1-frontend/code/code-summary.md

---

## Execution Summary
- **Plan Steps**: 9 (Step 0~8)
- **TDD Steps**: Step 1~5 (RED-GREEN-REFACTOR)
- **Non-TDD Steps**: Step 0 (setup), Step 6 (pages), Step 7 (deploy), Step 8 (docs)
- **Total Test Cases**: 30
