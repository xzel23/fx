#!/usr/bin/env bash
cd `dirname $0`
export JAVA_HOME=`/usr/libexec/java_home -v 17`
bash ./gradlew clean build publishToMavenLocal publish
