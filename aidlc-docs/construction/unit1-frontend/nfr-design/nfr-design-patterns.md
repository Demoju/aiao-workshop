# NFR Design Patterns - Unit 1 (Frontend)

## 1. Performance: Route-based Code Splitting

**패턴**: React.lazy + Suspense를 사용한 라우트 단위 코드 분할
**근거**: 소규모 매장 앱으로 페이지 수가 8개, 라우트 단위 분할이 복잡도 대비 효과적

```
적용 대상:
- /customer/menu → MenuPage (lazy)
- /customer/cart → CartPage (lazy)
- /customer/orders → OrderHistoryPage (lazy)
- /customer/login → TableLoginPage (lazy)
- /admin/login → AdminLoginPage (lazy)
- /admin/dashboard → AdminDashboardPage (lazy)
- /admin/tables → TableManagementPage (lazy)
- /admin/menus → MenuManagementPage (lazy)
```

## 2. Error Handling: Global Error Boundary

**패턴**: 앱 최상위 단일 ErrorBoundary
**근거**: 소규모 앱으로 글로벌 1개로 충분, 에러 시 전체 fallback UI 표시

```
App
└── ErrorBoundary (global)
    └── Router
        └── Pages...
```

## 3. API Error Handling: Axios Interceptor

**패턴**: Axios response interceptor에서 중앙 집중 에러 처리
**근거**: 모든 API 호출의 에러를 한 곳에서 관리, 401/403/500 등 공통 처리

```
처리 흐름:
1. Axios interceptor가 에러 캐치
2. 401 → 로그인 페이지 리다이렉트
3. 403 → 권한 없음 알림
4. 500 → 서버 에러 알림
5. Network error → 연결 실패 알림
```

## 4. Loading State: Global Zustand Store

**패턴**: Zustand store에서 글로벌 로딩 상태 관리
**근거**: Axios interceptor와 연동하여 요청 시작/종료 시 자동 로딩 상태 토글

```
loadingStore:
- isLoading: boolean
- setLoading(value): void
```

## 5. SSE Reconnection: Immediate Retry (Max 3)

**패턴**: 즉시 재연결, 최대 3회 시도
**근거**: 소규모 매장 환경에서 빠른 복구 우선, 3회 실패 시 수동 재연결 안내

```
재연결 흐름:
1. 연결 끊김 감지
2. 즉시 재연결 시도 (1회)
3. 실패 시 즉시 재시도 (2회)
4. 실패 시 즉시 재시도 (3회)
5. 3회 실패 → "연결이 끊어졌습니다. 새로고침해주세요" 메시지
```

## 6. State Persistence: Zustand Persist Middleware

**패턴**: Zustand persist middleware로 자동 상태 지속
**근거**: 선언적 설정으로 간편, 스토리지 타입 지정 가능

```
persist 설정:
- cartStore → localStorage (장바구니 유지)
- authStore → sessionStorage (인증 정보, 탭 종료 시 삭제)
- uiStore → 미적용 (in-memory only)
```

## 7. Internationalization: Hook-based (useTranslation)

**패턴**: react-i18next의 useTranslation hook
**근거**: 함수형 컴포넌트와 자연스러운 통합, 네임스페이스 분리 가능

```
지원 언어: ko (기본), en
네임스페이스: common, customer, admin
```

## 8. Form Validation: React Hook Form + Zod

**패턴**: React Hook Form으로 폼 관리, Zod로 스키마 검증
**근거**: TypeScript 친화적, 선언적 스키마 정의, 작은 번들 크기

```
적용 대상:
- TableLoginPage: 테이블 번호 입력
- AdminLoginPage: ID/PW 입력
```

## 9. Accessibility: Tailwind UI Components

**패턴**: Tailwind UI 기반 접근성 내장 컴포넌트 활용
**근거**: 기본 접근성(WCAG 2.1 Level A) 충족, 추가 라이브러리 불필요

```
적용:
- Semantic HTML 태그 사용
- Tailwind UI 컴포넌트의 내장 ARIA 속성 활용
- 키보드 네비게이션 지원
- 색상 대비 준수
```

## 10. Image Optimization: Lazy Loading

**패턴**: Intersection Observer API를 사용한 이미지 지연 로딩
**근거**: 메뉴 이미지가 많은 페이지에서 초기 로드 성능 개선

```
적용:
- MenuCard 컴포넌트의 메뉴 이미지
- loading="lazy" 속성 + Intersection Observer fallback
- placeholder 이미지 표시 후 뷰포트 진입 시 로드
```

## Pattern Summary

| NFR | 패턴 | 복잡도 |
|-----|------|--------|
| Performance | Route-based code splitting | 낮음 |
| Error Boundary | Global ErrorBoundary | 낮음 |
| API Error | Axios interceptor | 낮음 |
| Loading | Zustand global store | 낮음 |
| SSE | Immediate retry, max 3 | 낮음 |
| Persistence | Zustand persist middleware | 낮음 |
| i18n | useTranslation hook | 중간 |
| Validation | React Hook Form + Zod | 중간 |
| Accessibility | Tailwind UI components | 낮음 |
| Image | Lazy loading (IO) | 낮음 |
