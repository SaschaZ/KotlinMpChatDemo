FROM openjdk:16
COPY . /usr/src/kotlinMpChatDemo
WORKDIR /usr/src/kotlinMpChatDemo
EXPOSE 8080
RUN ./gradlew server:assemble
CMD ["./gradlew", "server:run"]