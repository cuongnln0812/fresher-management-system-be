#Build the application
FROM maven:3.8.4-openjdk-17 AS BUILD
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install

# Run the application
FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /app/target/phase1_FAMS-0.0.1-SNAPSHOT.jar ./phase1-aws.jar
EXPOSE 8080
CMD ["java", "-jar", "phase1-aws.jar"]