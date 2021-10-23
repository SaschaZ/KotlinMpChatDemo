FROM openjdk:15-slim-buster

ARG PORT
ARG PATH

COPY . /usr/src/kotlinMpChatDemo
WORKDIR /usr/src/kotlinMpChatDemo

RUN echo "./gradlew server:run --args='-p ${PORT} --path ${PATH}'" > run.sh && \
    chmod +x run.sh

CMD ["/bin/bash", "-c", "./run.sh"]

