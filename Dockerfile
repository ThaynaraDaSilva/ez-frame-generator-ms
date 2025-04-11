# Etapa 1: build da aplicação com Maven e Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Definacao o diretório de trabalho no contêiner
WORKDIR /app

# Copia o POM e baixa dependências (melhora cache)
COPY pom.xml .

RUN mvn dependency:go-offline

# Copia o codigo fonte
COPY src ./src


# Compila a aplicação
RUN mvn clean package -DskipTests

# Etapa 2: imagem enxuta apenas para execução
FROM eclipse-temurin:21-jdk-alpine

# Copia o JAR gerado da etapa de build
COPY --from=build /app/target/app.jar /app/app.jar

# Define a porta (opcional, útil para documentação e mapeamento no Docker Compose)
EXPOSE 8080

# Executa o JAR
ENTRYPOINT ["java", "-jar", "app.jar"]