#!/bin/bash

./gradlew desktop:packageDmg desktop:packageUberJarForCurrentOS

cp ./desktop/build/compose/binaries/main/dmg/desktop_1.0.0-1_amd64.dmg ./MpChatMacDemo.deb
cp ./desktop/build/compose/jars/desktop-mac-x64-1.0.0.jar ./MpChatMacDemo.jar

