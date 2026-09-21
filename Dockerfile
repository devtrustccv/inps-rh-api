# ============================================================
# BUILD - Maven + Java 26
# ============================================================
FROM cgr.dev/chainguard/maven:3.9-jdk26-dev AS build

WORKDIR /app

# Verificar versões
RUN java -version && mvn -version

# Copiar POM
COPY pom.xml ./

# Baixar dependências e aproveitar cache do Maven
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q dependency:go-offline

# Copiar código fonte
COPY src ./src

# Build da aplicação
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests clean package \
    && ls -lh target


# ============================================================
# RUNTIME - Java 26
# ============================================================
FROM cgr.dev/chainguard/jre:openjdk-26

WORKDIR /app

# Copiar o JAR gerado
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
