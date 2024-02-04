FROM openjdk:21
WORKDIR /app
COPY . .
CMD ["java", "-jar", "bot.jar"]