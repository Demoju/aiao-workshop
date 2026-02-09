# Logical Components - Unit 1 (Frontend)

## Overview
NFR 설계 패턴을 구현하기 위한 논리적 인프라 컴포넌트 정의

## 1. API Layer

### AxiosInstance
- **역할**: Axios 인스턴스 생성 및 interceptor 설정
- **위치**: `frontend/src/services/api.ts`
- **책임**:
  - Base URL 설정
  - Request interceptor: JWT 토큰 자동 첨부
  - Response interceptor: 에러 중앙 처리, 로딩 상태 토글
  - 401 → 로그인 리다이렉트

## 2. State Management Layer

### CartStore (Zustand + persist → localStorage)
- **역할**: 장바구니 상태 관리 및 지속성
- **위치**: `frontend/src/store/cartStore.ts`

### AuthStore (Zustand + persist → sessionStorage)
- **역할**: 인증 상태 (JWT, 사용자 정보)
- **위치**: `frontend/src/store/authStore.ts`

### LoadingStore (Zustand, no persist)
- **역할**: 글로벌 로딩 상태
- **위치**: `frontend/src/store/loadingStore.ts`

### OrderStore (Zustand, no persist)
- **역할**: 주문 상태 관리
- **위치**: `frontend/src/store/orderStore.ts`

## 3. Error Handling Layer

### ErrorBoundary
- **역할**: 앱 최상위 에러 경계
- **위치**: `frontend/src/shared/ErrorBoundary.tsx`
- **동작**: 에러 발생 시 fallback UI 표시, 새로고침 버튼 제공

## 4. SSE Layer

### useOrderStream Hook
- **역할**: SSE 연결 관리 및 재연결 로직
- **위치**: `frontend/src/hooks/useOrderStream.ts`
- **재연결**: 즉시 재시도, 최대 3회, 실패 시 사용자 알림

## 5. Routing Layer

### AppRouter
- **역할**: React.lazy + Suspense 기반 라우트 코드 분할
- **위치**: `frontend/src/App.tsx`
- **동작**: 각 페이지를 lazy import, Suspense fallback으로 LoadingSpinner 표시

## 6. i18n Layer

### i18n Configuration
- **역할**: react-i18next 초기화 및 언어 리소스 관리
- **위치**: `frontend/src/i18n/index.ts`
- **리소스**: `frontend/src/i18n/locales/{ko,en}/`

## 7. Validation Layer

### Zod Schemas
- **역할**: 폼 입력 검증 스키마 정의
- **위치**: `frontend/src/types/schemas.ts`
- **적용**: React Hook Form의 zodResolver와 연동

## 8. Image Optimization

### LazyImage Component
- **역할**: Intersection Observer 기반 이미지 지연 로딩
- **위치**: `frontend/src/shared/LazyImage.tsx`
- **동작**: 뷰포트 진입 시 이미지 로드, placeholder 표시

## Component Interaction

```
App
├── ErrorBoundary (global)
│   └── i18n Provider
│       └── AppRouter (lazy routes)
│           ├── Customer Pages
│           │   ├── MenuPage → useMenu hook → AxiosInstance → API
│           │   ├── CartPage → CartStore (persist/localStorage)
│           │   ├── OrderHistoryPage → useOrder hook → AxiosInstance
│           │   └── TableLoginPage → React Hook Form + Zod → AuthStore
│           └── Admin Pages
│               ├── AdminLoginPage → React Hook Form + Zod → AuthStore
│               ├── AdminDashboardPage → useOrderStream (SSE) → OrderStore
│               ├── TableManagementPage → useTable hook → AxiosInstance
│               └── MenuManagementPage → useMenu hook → AxiosInstance
├── LoadingStore ← AxiosInstance (interceptor 연동)
└── LoadingSpinner (global, LoadingStore 구독)
```

## File Structure Summary

```
frontend/src/
├── services/
│   └── api.ts                  # AxiosInstance + interceptors
├── store/
│   ├── cartStore.ts            # persist → localStorage
│   ├── authStore.ts            # persist → sessionStorage
│   ├── loadingStore.ts         # no persist
│   └── orderStore.ts           # no persist
├── shared/
│   ├── ErrorBoundary.tsx       # Global error boundary
│   ├── LazyImage.tsx           # Intersection Observer lazy load
│   ├── LoadingSpinner.tsx      # Global loading indicator
│   └── ConfirmDialog.tsx       # Confirmation modal
├── hooks/
│   └── useOrderStream.ts      # SSE + retry (max 3)
├── i18n/
│   ├── index.ts                # i18n config
│   └── locales/
│       ├── ko/                 # 한국어
│       └── en/                 # English
├── types/
│   └── schemas.ts              # Zod validation schemas
└── App.tsx                     # Router + lazy loading
```
