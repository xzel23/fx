#!/usr/bin/env bash
DIR=`dirname $0`
cd ${DIR}

TARGET_DIR=src/main/resources/com/dua3/fx/editors/intern

TUI_EDITOR_PATH=https://github.com/nhn/tui.editor/raw/production/dist
TUI_EDITOR_JS=tui-editor-Editor-full.min.js
wget ${TUI_EDITOR_PATH}/${TUI_EDITOR_JS} -O ${TARGET_DIR}/${TUI_EDITOR_JS}
