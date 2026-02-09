# Infrastructure Design - Unit 1 (Frontend)

## Deployment Environment
- **Type**: Docker Container (로컬 Docker Compose)
- **Cloud Services**: 없음 (메뉴 이미지만 외부 S3 URL 참조)

## Container Configuration

### Frontend Container
- **Base Image**: `node:20-alpine` (빌드), `nginx:alpine` (서빙)
- **Build Strategy**: Multi-stage build
  - Stage 1: `npm run build` (Vite 빌드)
  - Stage 2: Nginx로 정적 파일 서빙
- **Port**: 80 (내부) → 3000 (외부 매핑)

### Nginx Configuration
- SPA 라우팅: 모든 경로 → `index.html` fallback
- API Proxy: `/api/*` → Backend 컨테이너로 프록시
- Gzip 압축 활성화
- 정적 파일 캐싱 헤더

## Service Mapping

| 논리적 컴포넌트 | 인프라 매핑 |
|----------------|------------|
| React App | Nginx 정적 파일 서빙 |
| API 호출 (Axios) | Nginx reverse proxy → Backend |
| SSE 연결 | Nginx proxy (buffering off) → Backend |
| 메뉴 이미지 | 외부 S3 URL 직접 참조 |
| i18n 리소스 | 번들에 포함 (정적) |

## Environment Variables

| 변수 | 용도 | 기본값 |
|------|------|--------|
| `VITE_API_BASE_URL` | Backend API URL | `/api` |

## Network

```
Docker Compose Network:
┌─────────────────────────────────────────┐
│  docker-compose network (bridge)        │
│                                         │
│  ┌──────────┐       ┌──────────┐       │
│  │ frontend │──────▶│ backend  │       │
│  │ :3000    │ /api  │ :8080    │       │
│  │ (nginx)  │       │ (spring) │       │
│  └──────────┘       └────┬─────┘       │
│                          │              │
│                     ┌────▼─────┐       │
│                     │ postgres │       │
│                     │ :5432    │       │
│                     └──────────┘       │
└─────────────────────────────────────────┘
```
