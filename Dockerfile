FROM adoptopenjdk/openjdk11:latest

COPY . /anteus
WORKDIR /anteus

EXPOSE 7000
# When the container starts: build, test and run the app.
CMD ./gradlew build && ./gradlew test && ./gradlew run
