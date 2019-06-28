// Copyright 2019 Axel Howind
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// connect to logger
console.log = function(m) {
    bridge.log(m);
};

// set to true to enable trace messages
var debug = true;

// output trace messages in debug mode
function trace(m) {
    if (debug) {
        console.log("TRACE " + m);
    }
}

// CodeMirror settings
CodeMirror.modeURL = "codemirror/mode/%N/%N.js";

const mode_info_text = CodeMirror.findModeByExtension('txt');
function getModeFromExtension(ext) {
    var info = CodeMirror.findModeByExtension(ext);
    if (info) {
        return info;
    } else {
        return mode_info_text;
    }
}

/*
textArea = document.getElementById("editor");

{
options = {
            fullScreen : true,
            scrollbarStyle : 'overlay',
            mode : 'text',
            lineNumbers : false,
            inputStyle : 'textarea'
        }
 */

class CodeEditor {
    constructor(textArea, options) {
        this.editor = CodeMirror.fromTextArea(textArea, options);
        this.szs = 14;

        // track dirty state
        editor.on('change', function() {
            bridge.setDirty(true);
        });

        jSetFontSize(14);
    }

    jSetContent(text, ext) {
        var mode = getModeFromExtension(ext);
        CodeMirror.autoLoadMode(editor, mode.mode);

        // force reset of placeholder (because it sometime fails to update when setting content)
        var placeholder = editor.getOption("placeholder");
        editor.setOption("placeholder", "");

        editor.swapDoc(CodeMirror.Doc(text, mode.mime));

        editor.setOption("placeholder", placeholder);

        // inform Java code that the buffer is clean
        bridge.setDirty(false);
        console.log("LOAD: updated editor content, ext=" + ext + ", mode="
            + mode.mode);
    }

    // Set the editor content. Called from Java.
    // Paste text at current position. Called from Java code.
    jReplaceSelection(text) {
        trace("PASTING");
        editor.replaceSelection(text);
        trace("PASTED");
    }

    jSetModeFromExtension(ext) {
        var mode = getModeFromExtension(ext);
        editor.setOption("mode", mode.mime);
        CodeMirror.autoLoadMode(editor, mode.mode);
        trace("jSetModeFromExtension: mode set to " + mode.mode);
    }

    // set readonly mode
    jSetReadOnly(flag) {
        editor.setOption("readOnly", flag);
        trace("jSetReadOnly: readOnly = " + flag);
    }

    // set the placeholder text
    jSetPromptText(text) {
        editor.setOption("placeholder", text);
        trace("jSetPromptText: promptText = '"+text+"'");
    }

    // use the system clipboard for cut & paste
    jPaste() {
        trace("jPaste()");
        bridge.paste();
    }

    jCopy() {
        var text = editor.getSelection();
        trace("jCopy(): '"+text+"'");
        var arg = {
            'format' : 'text',
            'content' : text
        };
        bridge.copy(arg);
    }

    jCut() {
        var text = editor.getSelection();
        trace("jCut(): '"+text+"'");
        var arg = {
            'format' : 'text',
            'content' : text
        };
        bridge.cut(arg);
    }

    jGetText() {
        return editor.getDoc().getValue();
    }

    jGetLineCount() {
        return editor.lineCount();
    }

    jGetLine(idx) {
        return editor.getLine(idx);
    }

    jGetLineNumber(idx) {
        return editor.getCursor(idx).line;
    }

    jSearch() {
        editor.execCommand("find");
    }

    jSetShowLineNumbers(flag) {
        trace('lineNumbers: '+flag);
        editor.setOption('lineNumbers', flag);
    }

    jIsShowLineNumbers() {
        return editor.getOption('lineNumbers');
    }

    jSetHighlightCurrentLine(flag) {
        trace('styleActiveLine: '+flag);
        editor.setOption('styleActiveLine', flag);
    }

    jIsHighlightCurrentLine() {
        return editor.getOption('styleActiveLine');
    }

    jSetFontSize(size) {
        editor.getWrapperElement().style["font-size"] = size+"px";
    }

    jGetFontSize() {
        szs = editor.getWrapperElement().style["font-size"];
        return parseFloat(szs.replace("px",""));
    }

    jSetTheme(theme) {
        editor.setOption('theme', theme);
    }

    jGetTheme() {
        return editor.getOption('theme');
    }

    jSetLine(i,s) {
        editor.replaceRange(s, {line: i, ch: 0}, {line: i});
    }

    jAddLine(s) {
        editor.replaceRange(s+'\n', {line: Infinity, ch: 0});
    }

}

export default function init (bridge, textArea, options) {
    return new CodeEditor(textArea, options);
}
