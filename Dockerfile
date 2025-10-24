# --- STAGE 1: Build the JAR file ---
# Use a more robust base image (e.g., Jammy/Debian instead of Alpine)
FROM eclipse-temurin:17-jdk-jammy AS build

# Set the working directory
WORKDIR /app

# Copy necessary build files
COPY gradlew .
COPY gradle/wrapper/ /app/gradle/wrapper/
COPY build.gradle settings.gradle /app/

# Copy source code for building
COPY src /app/src

# Grant execution rights to the Gradle wrapper
RUN chmod +x gradlew

# Run the build command (the failure point)
RUN ./gradlew bootJar -x test


# --- STAGE 2: Create a minimal runtime image ---
# Use a small JRE image for the final runtime
FROM eclipse-temurin:17-jre-jammy

# Set the working directory
WORKDIR /app

# Copy the generated JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port (Render's default is 10000, but 8080 is the app default)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
