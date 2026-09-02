#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mvn clean javadoc:javadoc
echo "Documentación disponible en target/site/apidocs/index.html"
