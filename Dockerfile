# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copiamos apenas o JAR gerado no estágio anterior
COPY --from build /app/target/*.jar app.jar

# Configurações de segurança: não rodar como root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]