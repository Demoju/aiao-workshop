# Build stage
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle clean build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/ ./
RUN ls -la && mv *.jar app.jar || echo "No jar found"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
