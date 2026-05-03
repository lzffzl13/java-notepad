#!/bin/sh
# Maven Wrapper script for Unix-based systems
# Downloads Maven if not present and runs it

set -e

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(cd "$(dirname "$0")" && pwd)}"
export MAVEN_PROJECTBASEDIR

MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"
MAVEN_WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"

if [ -r "$MAVEN_WRAPPER_PROPERTIES" ]; then
    distributionUrl=$(grep -E '^distributionUrl=' "$MAVEN_WRAPPER_PROPERTIES" | cut -d'=' -f2-)
    wrapperUrl=$(grep -E '^wrapperUrl=' "$MAVEN_WRAPPER_PROPERTIES" | cut -d'=' -f2-)
fi

MAVEN_HOME="$HOME/.m2/wrapper/dists/$(echo "$distributionUrl" | sed 's/.*\///' | sed 's/\.zip$//')"

if [ ! -d "$MAVEN_HOME" ]; then
    echo "Downloading Maven..."
    mkdir -p "$MAVEN_HOME"
    curl -fsSL "$distributionUrl" -o "$MAVEN_HOME/maven.zip"
    unzip -q "$MAVEN_HOME/maven.zip" -d "$MAVEN_HOME"
    rm -f "$MAVEN_HOME/maven.zip"
    mv "$MAVEN_HOME"/apache-maven-*/* "$MAVEN_HOME/"
    rmdir "$MAVEN_HOME"/apache-maven-* 2>/dev/null || true
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
