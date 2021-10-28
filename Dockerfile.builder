FROM openjdk:15-slim-buster

ENV USER builder
ENV GROUP builders
ENV HOME_PATH /home/builder
ENV PROJECT_PATH ${HOME_PATH}/kotlinMpChatDemo

ARG user_id=1000
ENV USER_ID=${user_id}
ARG group_id=1000
ENV GROUP_ID=${group_id}

RUN addgroup --gid ${GROUP_ID} ${GROUP}
RUN adduser --disabled-password --home ${HOME_PATH} -gecos '' --uid ${USER_ID} --gid ${GROUP_ID} ${USER}

WORKDIR ${PROJECT_PATH}

USER ${USER}

# Remove possible temporary build files
RUN rm -f ./local.properties && \
    find . -name build -print0 | xargs -0 rm -rf && \
    rm -rf .gradle && \
    rm -rf ~/.m2 && \
    rm -rf ~/.gradle && \
    rm -f ./MpChatServerDemo.jar

CMD ["/bin/bash", "-c", "./gradlew server:shadowJar" ]