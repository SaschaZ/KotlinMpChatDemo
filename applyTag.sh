#!/bin/bash

echo "applying tag $1"

sed -E 's/packageVersion = "[0-9]+\.[0-9]+\.[0-9]+"/packageVersion = "'${1:1}'"/g' desktop/build.gradle.kts > desktop/build.gradle2.kts
mv desktop/build.gradle2.kts desktop/build.gradle.kts

sed -E 's/const val currentTag = "v[0-9]+\.[0-9]+\.[0-9]+"/const val currentTag = "'$1'"/g' common/src/commonMain/kotlin/dev/zieger/mpchatdemo/common/CurrentTag.kt > common/src/commonMain/kotlin/dev/zieger/mpchatdemo/common/CurrentTag2.kt
mv common/src/commonMain/kotlin/dev/zieger/mpchatdemo/common/CurrentTag2.kt common/src/commonMain/kotlin/dev/zieger/mpchatdemo/common/CurrentTag.kt