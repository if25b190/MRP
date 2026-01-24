FROM eclipse-temurin:21-jdk AS build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN chmod +x ./mvnw
RUN --mount=type=cache,target=/root/.m2 ./mvnw -f $HOME/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jdk
ARG TARGET_FOLDER=/usr/app/target
COPY --from=build $TARGET_FOLDER /app
RUN mv /app/*.jar /app/runner.jar
EXPOSE 8080
ENTRYPOINT java -jar /app/runner.jar
