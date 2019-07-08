@echo off
set DIR=%~dp0
set DIST_DIR="%DIR%/dist"
set TARGET_DIR="%DIR%/../src/main/resources/com/dua3/fx/editors/text"

cd %DIR%

echo "running webpack ..."
npx webpack --mode=production || exit 1

echo "copying files to %TARGET_DIR% ..."
cp "%DIST_DIR%/text_editor.bundle.js" "%TARGET_DIR%/"
cp "%DIST_DIR%/text_editor.html" "%TARGET_DIR}/"

echo "SUCCESS"
