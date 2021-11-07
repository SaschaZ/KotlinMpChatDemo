#!/bin/bash

echo "build README for tag $1"
sed 's/%%LATEST_TAG%%/'$1'/g' README.template.md > README.md
cat README.md