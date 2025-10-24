# Use an official OpenJDK image with Alpine Linux for a small footprint
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper files
COPY gradlew .
COPY gradle/wrapper/ /app/gradle/wrapper/

# Copy the build file(s)
COPY build.gradle settings.gradle /app/

# Grant execution rights to the Gradle wrapper
RUN chmod +x gradlew

# Build the application (this downloads dependencies and runs the build)
# The application source is not copied yet, so this step can be cached.
# You might need to change 'build' to 'bootJar' or similar depending on your setup.
RUN ./gradlew build -x test

# Now copy the rest of your source code
COPY src /app/src

# Re-run the build to generate the final JAR file
RUN ./gradlew bootJar -x test

# Expose the port the Spring Boot app will listen on (default is 8080)
EXPOSE 8080

# Command to run the application
# This assumes your main JAR is generated in build/libs/
ENTRYPOINT ["java", "-jar", "/app/build/libs/*.jar"]
