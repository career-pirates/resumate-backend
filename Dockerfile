# ===== Build Stage =====
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
#RUN ./gradlew -x test bootJar
RUN ./gradlew -x test bootJar --no-daemon \
  && jar="$(ls -S build/libs/*.jar | head -n1)" \
  && mv "$jar" app.jar

# ===== Run Stage ======
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
#COPY --from=build /app/build/libs/*.jar app.jar
COPY --from=build /app/app.jar app.jar
RUN groupadd -r app && useradd -r -g app app
COPY --from=build --chown=app:app /app/app.jar app.jar
USER app:app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]