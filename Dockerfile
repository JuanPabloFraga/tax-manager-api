# ── Stage 1: Build ──
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copiar Maven wrapper y pom.xml primero (cachear dependencias)
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copiar código fuente y compilar
COPY src src
RUN ./mvnw package -DskipTests -B

# ── Stage 2: Runtime ──
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiar JAR del stage anterior
COPY --from=build /app/target/*.jar app.jar

# Usuario no-root por seguridad
RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
