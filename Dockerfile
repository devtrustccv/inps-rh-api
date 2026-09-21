FROM cgr.dev/chainguard/maven:3.9-jdk26-dev AS build

WORKDIR /app

# Verificar versões utilizadas no build
RUN java -version && mvn -version

COPY pom.xml ./

# Cache das dependências Maven
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q dependency:go-offline

COPY src ./src

# Build
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests clean package \
    && ls -lh target


FROM cgr.dev/chainguard/jre:openjdk-26.0.2.1-dev

WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
