# Tech Stack Decisions - Unit 1 (Frontend)

## Core Framework

### React 18+
- **Rationale**: 요구사항, 컴포넌트 기반, 풍부한 생태계
- **Version**: 18.x (Concurrent features)

### TypeScript
- **Rationale**: Type safety, 개발 생산성, 에러 감소
- **Version**: 5.x

## State Management

### Zustand
- **Rationale**: 요구사항, 간단한 API, 작은 번들 크기
- **Middleware**: persist (LocalStorage/SessionStorage 자동 관리)

## Styling

### Tailwind CSS
- **Rationale**: 빠른 개발, 반응형 유틸리티, 작은 번들
- **Plugins**: forms, typography

## HTTP Client

### Axios
- **Rationale**: 요구사항, interceptors, 간편한 에러 처리
- **Version**: 1.x

## Routing

### React Router
- **Rationale**: 표준 라우팅 라이브러리
- **Version**: 6.x

## Internationalization

### react-i18next
- **Rationale**: 한국어 + 영어 지원
- **Version**: Latest

## Testing

### Jest
- **Rationale**: React 표준 테스트 프레임워크
- **Version**: 29.x

### React Testing Library
- **Rationale**: 사용자 중심 테스트
- **Version**: 14.x

## Build Tool

### Vite
- **Rationale**: 빠른 개발 서버, HMR, 작은 번들
- **Version**: 5.x

## Code Quality

### ESLint
- **Config**: eslint-config-react-app
- **Plugins**: typescript, react-hooks

### Prettier
- **Config**: Default + Tailwind plugin

## Development Tools

### React DevTools
- **Rationale**: 디버깅

### Zustand DevTools
- **Rationale**: 상태 디버깅

## Summary

| Category | Technology | Version | Rationale |
|----------|-----------|---------|-----------|
| Framework | React | 18.x | 요구사항, 컴포넌트 기반 |
| Language | TypeScript | 5.x | Type safety |
| State | Zustand | Latest | 간단한 API, persist |
| Styling | Tailwind CSS | 3.x | 빠른 개발, 반응형 |
| HTTP | Axios | 1.x | Interceptors |
| Routing | React Router | 6.x | 표준 라우팅 |
| i18n | react-i18next | Latest | 다국어 |
| Testing | Jest + RTL | Latest | TDD |
| Build | Vite | 5.x | 빠른 빌드 |
