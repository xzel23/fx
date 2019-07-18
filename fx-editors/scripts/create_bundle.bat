@echo off

set NAME=%1

set SCRIPT_DIR=%~dp0
set DIR=%SCRIPT_DIR%\..\fx-editors-%NAME%

set NODE_DIR=%DIR%\node
set SRC_DIR=%NODE_DIR%\src
set DIST_DIR=%NODE_DIR%\dist
set TARGET_DIR=%DIR%\src\main\resources\com\dua3\fx\editors\%NAME%

echo "preparing dist ..."
copy %SRC_DIR%\editor.html %DIST_DIR%\ || goto :error

cd %NODE_DIR% || goto :error
npx webpack --mode=production || goto :error

echo "copying files to %TARGET_DIR% ..."
copy "%DIST_DIR%\text_editor.bundle.js" "%TARGET_DIR%\"
copy "%DIST_DIR%\text_editor.html" "%TARGET_DIR}\"

echo "SUCCESS"
goto :EOF

:error
echo "FAIL!"


