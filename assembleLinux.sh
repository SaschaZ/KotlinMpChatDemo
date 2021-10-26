#!/bin/bash

./gradlew desktop:packageDeb desktop:packageUberJarForCurrentOS

cp ./desktop/build/compose/binaries/main/deb/desktop_1.0.0-1_amd64.deb ./MpChatLinuxDemo.deb
cp ./desktop/build/compose/jars/desktop-linux-x64-1.0.0.jar ./MpChatLinuxDemo.jar

