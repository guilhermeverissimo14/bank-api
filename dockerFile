#build compila e empacota o JAR
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

#runtime só o necessário pra rodar
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/bank-api-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
