#!/usr/bin/env bash
cd `dirname $0`
export JAVA_HOME=`/usr/libexec/java_home -v 11`
bash ./gradlew clean build publishToMavenLocal publish
