# Etapa base com Maven para build
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY Desconecta/pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn package -DskipTests

# Etapa desenvolvimento
FROM maven:3.9.9-eclipse-temurin-17 AS dev
WORKDIR /app
COPY Desconecta/pom.xml .
RUN mvn dependency:go-offline
COPY . .
CMD ["mvn", "spring-boot:run"]


# Etapa produção
FROM eclipse-temurin:17-jre AS prod
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]

