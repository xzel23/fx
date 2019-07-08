#!/usr/bin/env bash

die() { echo "$*" 1>&2 ; exit 1; }

NAME=$1
test -z "$NAME" && die die "usage: `basename $0` <implementation_name>"

DIR="`dirname $0`/../fx-editors-${NAME}"
echo $DIR
test -d "${DIR}" || die "no implementation '${NAME}'"

NODE_DIR=${DIR}/node
SRC_DIR=${NODE_DIR}/src
DIST_DIR=${NODE_DIR}/dist
TARGET_DIR=${DIR}/src/main/resources/com/dua3/fx/editors/text

echo "preparing dist ..."
cp ${SRC_DIR}/editor.html ${DIST_DIR}/ || die "could not copy HTML"
( cd ${NODE_DIR} && npx webpack --mode=production ) || die "webpack command failed"

echo "copying files to ${TARGET_DIR} ..."
cp ${DIST_DIR}/editor.js ${TARGET_DIR}/ || "could not copy bundle to src folder"
cp ${DIST_DIR}/editor.html ${TARGET_DIR}/ || "could not copy HTML to src folder"

echo "SUCCESS"
