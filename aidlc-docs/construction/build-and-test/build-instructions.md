# Build Instructions

## Unit 1 - Frontend

### Prerequisites
- **Node.js**: 20.x
- **npm**: 10.x+

### Development Server
```bash
cd frontend
npm install
npm run dev
# → http://localhost:5173 (API proxy: /api → http://localhost:8080)
```

### Production Build
```bash
cd frontend
npm run build
# Output: frontend/dist/
```

### Docker Build
```bash
docker build -t tableorder-frontend ./frontend
docker run -p 3000:80 tableorder-frontend
```

---

## Unit 3 - Backend Admin API

### Prerequisites
- **Java**: 17+
- **Maven**: 3.9+
- **PostgreSQL**: 15+

### Local Build
```bash
cd backend
./mvnw clean package
java -jar target/admin-api-1.0.0.jar
# → http://localhost:8080
```

### Docker Build
```bash
docker build -t tableorder-backend ./backend
```

---

## Full System (Docker Compose)

```bash
docker-compose up --build
# frontend: http://localhost:3000
# backend: http://localhost:8080
# postgres: localhost:5432
```

## Troubleshooting

### Frontend npm install 실패
- Node.js 20.x 확인: `node -v`
- `rm -rf node_modules package-lock.json && npm install`

### Backend 빌드 실패
- Java 17+ 확인: `java -version`
- `./mvnw clean install -DskipTests`
