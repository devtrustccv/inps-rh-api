# ============================================================
# BUILD
# ============================================================
FROM cgr.dev/chainguard/maven:3.9-jdk26-dev AS build

WORKDIR /app

# Verificar Java e Maven
RUN java -version && mvn -version

# Copiar primeiro o pom para aproveitar o cache Docker
COPY pom.xml ./

# Baixar dependências
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q dependency:go-offline

# Copiar código fonte
COPY src ./src

# Compilar e gerar JAR
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests clean package \
    && ls -lh target


# ============================================================
# RUNTIME
# ============================================================
FROM cgr.dev/chainguard/jre:openjdk-26

WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
