FROM adoptopenjdk/openjdk11:latest

COPY . /anteus
WORKDIR /anteus

EXPOSE 7000
# When the container starts: build, test and run the app.
CMD ./gradlew build -x test && ./gradlew run
#TODO: I am skipping test phase, as it's not working in docker despite it does in local
