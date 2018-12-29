#!/bin/bash

function die () {
  echo $* 
  exit 1
}

pushd .
cd `dirname $0`

for P in utility fx.app fx.editors fx.samples query ; do
  echo "building ${P} ..."
  ( cd "${P}" && ./gradlew && echo "${P}: OK." ) || die "${P}: FAIL."
done

popd
