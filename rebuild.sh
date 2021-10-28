#/bin/bash

rm -f ./MpChatServerDemo.jar; \
  docker-compose down && \
  docker-compose build --force-rm --no-cache chatdemo_builder && \
  docker-compose up -d chatdemo_builder && \
  docker-compose logs -f chatdemo_builder
