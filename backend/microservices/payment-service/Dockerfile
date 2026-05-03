# 1. Aşama: Derleme (Build)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Bağımlılıkları önbelleğe almak için önce sadece pom.xml kopyalanır
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Kaynak kodları kopyala ve JAR paketini oluştur
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Aşama: Çalıştırma (Runtime)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Sadece oluşan JAR dosyasını önceki aşamadan al
COPY --from=build /app/target/*.jar app.jar

# Uygulamayı başlat
ENTRYPOINT ["java", "-jar", "app.jar"]