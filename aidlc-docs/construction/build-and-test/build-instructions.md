# Build Instructions - Unit 1 (Frontend)

## Prerequisites
- **Node.js**: 20.x
- **npm**: 10.x+
- **Docker**: (프로덕션 빌드 시)

## Build Steps

### 1. Install Dependencies
```bash
cd frontend
npm install
```

### 2. Development Server
```bash
npm run dev
# → http://localhost:5173
# API proxy: /api → http://localhost:8080
```

### 3. Production Build
```bash
npm run build
# Output: frontend/dist/
```

### 4. Docker Build
```bash
docker build -t tableorder-frontend ./frontend
docker run -p 3000:80 tableorder-frontend
```

### 5. Docker Compose (전체 시스템)
```bash
docker-compose up --build
# frontend: http://localhost:3000
# backend: http://localhost:8080
```

## Build Verification
- `npm run build` 성공 시 `dist/` 디렉토리에 정적 파일 생성
- Route-based code splitting으로 페이지별 chunk 파일 생성 확인

## Troubleshooting

### npm install 실패
- Node.js 20.x 버전 확인: `node -v`
- `rm -rf node_modules package-lock.json && npm install`

### Build 실패
- TypeScript 에러: `npx tsc --noEmit` 으로 확인
- 테스트 파일은 `tsconfig.app.json`에서 exclude 됨
