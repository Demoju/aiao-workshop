# NFR Requirements - Unit 1 (Frontend)

## Performance Requirements

### PR-1: Page Load Time
- **Target**: 1초 이내
- **Measurement**: First Contentful Paint (FCP)
- **Strategy**: Code splitting, lazy loading, image optimization

### PR-2: API Response Handling
- **Target**: 3초 이내 응답 (Backend 요구사항)
- **Strategy**: Loading indicators, optimistic updates

### PR-3: SSE Reconnection
- **Max Wait**: 30초 (exponential backoff)
- **Strategy**: Automatic reconnection with backoff

## Scalability Requirements

### SC-1: Concurrent Users
- **Expected**: 10명 이하 (소규모 매장)
- **Strategy**: Client-side rendering, minimal server load

## Availability Requirements

### AV-1: Uptime
- **Target**: Best effort (로컬 배포)
- **Strategy**: Error boundaries, graceful degradation

## Security Requirements

### SE-1: HTTPS
- **Requirement**: 프로덕션 필수, 개발 환경 선택
- **Strategy**: HTTPS 설정 (프로덕션)

### SE-2: XSS Protection
- **Requirement**: React 기본 XSS 방지
- **Strategy**: JSX 자동 이스케이프

### SE-3: JWT Storage
- **Requirement**: SessionStorage (요구사항)
- **Risk**: XSS 취약, HTTPS 권장

## Reliability Requirements

### RE-1: Error Handling
- **Requirement**: 모든 에러 캐치 및 사용자 피드백
- **Strategy**: ErrorBoundary, try-catch, API error handling

### RE-2: State Recovery
- **Requirement**: 페이지 새로고침 시 상태 복구
- **Strategy**: LocalStorage (장바구니), SessionStorage (로그인)

## Maintainability Requirements

### MA-1: Code Quality
- **Requirement**: TypeScript, ESLint, Prettier
- **Strategy**: Type safety, code formatting

### MA-2: Testing
- **Requirement**: Unit tests (TDD)
- **Strategy**: Jest, React Testing Library

## Usability Requirements

### US-1: Responsive Design
- **Requirement**: 모든 디바이스 지원
- **Strategy**: Tailwind CSS responsive utilities

### US-2: Accessibility
- **Requirement**: 기본 접근성 (WCAG 2.1 Level A)
- **Strategy**: Semantic HTML, ARIA labels

### US-3: Internationalization
- **Requirement**: 한국어 + 영어
- **Strategy**: i18n library (react-i18next)
