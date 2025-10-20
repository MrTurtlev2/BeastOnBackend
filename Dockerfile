# Etap build – budowanie JAR z Mavenem
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Kopiujemy tylko pom.xml najpierw, żeby wykorzystać cache dla zależności
COPY pom.xml .
RUN mvn dependency:go-offline

# Kopiujemy kod źródłowy
COPY src ./src

# Budujemy aplikację
RUN mvn clean package -DskipTests

# Etap runtime – lekki obraz z JDK
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Kopiujemy JAR z etapu build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
