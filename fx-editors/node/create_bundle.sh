#!/usr/bin/env bash
DIR=`dirname $0`
DIST_DIR=${DIR}/dist
TARGET_DIR=${DIR}/../src/main/resources/com/dua3/fx/editors/intern

cd ${DIR}

echo "running webpack ..."
npx webpack --mode=production || exit 1

echo "copying files to ${TARGET_DIR} ..."
cp ${DIST_DIR}/code_editor.bundle.js ${TARGET_DIR}/ || exit 1
cp ${DIST_DIR}/code_editor.html ${TARGET_DIR}/ || exit 1

echo "SUCCESS"
