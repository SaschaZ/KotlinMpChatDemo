#!/bin/bash

keyPassword=$1

path=`pwd`

./gradlew assembleRelease \
    -Pandroid.injected.signing.store.file=${path}/android/key.jks \
    -Pandroid.injected.signing.store.password=${keyPassword} \
    -Pandroid.injected.signing.key.alias=KotlinMpChatAndroidDemo \
    -Pandroid.injected.signing.key.password=${keyPassword}

cp ./android/build/outputs/apk/release/android-release.apk ./MpChatAndroidDemo.apk
