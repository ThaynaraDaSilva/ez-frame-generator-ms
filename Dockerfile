# Etapa 1: build da aplicação com Maven e Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Define o diretório de trabalho no contêiner
WORKDIR /app

# Copia o POM e baixa dependências (melhora o cache entre builds)
COPY pom.xml .

RUN mvn dependency:go-offline

# Copia o código-fonte da aplicação
COPY src ./src

# Compila a aplicação (gera o .jar no target/)
RUN mvn clean package -DskipTests

# Etapa 2: imagem enxuta apenas para execução
FROM eclipse-temurin:21-jdk-alpine

# Define o diretório de execução
WORKDIR /app

# Copia o JAR gerado da etapa de build
COPY --from=build /app/target/app.jar ./app.jar

# Define a porta exposta (opcional, mas útil)
EXPOSE 8080

# Executa a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]