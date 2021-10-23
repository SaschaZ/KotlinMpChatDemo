FROM openjdk:15-slim-buster as build

COPY . /usr/src/kotlinMpChatDemo
WORKDIR /usr/src/kotlinMpChatDemo

CMD ["/bin/bash", "-c", "./gradlew", "shadowJar"]


FROM openjdk:15-slim-buster as runner

ARG PORT=9020
ARG PATH=/chat

RUN mkdir -p /home/runner
WORKDIR /home/runner

COPY --from=build /usr/src/kotlinMpChatDemo/MpChatServerDemo.jar ./MpChatServerDemo.jar
RUN echo "java -jar ./MpChatServerDemo.jar -p ${PORT} --path ${PATH}" > ./run.sh && \
    chmod +x ./run.sh

CMD ["/bin/bash", "-c", "./run.sh" ]