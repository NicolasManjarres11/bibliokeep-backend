# -- Stage 1 - Builder
# Perform the extraction in a separate builder container
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /builder

# Copia de Gradle al contenedor
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew

# Descargar las dependencias de gradle
RUN ./gradlew build -x test --no-daemon > /dev/null 2>&1 || true

# Copiar el codigo fuente
COPY src/ src/
# Compilar el JAR
RUN ./gradlew clean bootJar -x test --no-daemon

# Extaer las capas (layers) del JAR usando layertools
ARG JAR_FILE=build/libs/*.jar

# RUN lstat build/libs/

COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# -- Stage 2 - Runner
# This is the runtime container
FROM eclipse-temurin:21-jre-alpine
WORKDIR /application

# Seguridad: Usuario no-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the extracted jar contents from the builder container into the working directory in the runtime container
# Every copy step creates a new docker layer
# This allows docker to only pull the changes it really needs
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

# Variables de entorno por defecto
ENV SERVER_PORT=8080

EXPOSE 8080

# Start the application jar - this is not the uber jar used by the builder
# This jar only contains application code and references to the extracted jar files
# This layout is efficient to start up and AOT cache (and CDS) friendly
ENTRYPOINT ["java", "-jar", "application.jar"]