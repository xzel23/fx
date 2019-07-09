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

import CodeMirror from 'codemirror';

import 'codemirror/mode/meta.js';
import 'codemirror/addon/selection/active-line.js';
import 'codemirror/addon/scroll/simplescrollbars.js';
import 'codemirror/addon/search/searchcursor.js';
import 'codemirror/addon/search/search.js';
import 'codemirror/addon/display/fullscreen.js';
import 'codemirror/addon/display/placeholder.js';

import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/xq-dark.css';
import 'codemirror/theme/xq-light.css';
import 'codemirror/addon/display/fullscreen.css';
import 'codemirror/addon/scroll/simplescrollbars.css';
import 'codemirror/addon/search/matchesonscrollbar.css';

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

// Code taken from codemirror/loadmode extension and changed to be compatible with webpack:
// ----<8--------<8--------<8--------<8--------<8--------<8--------<8----

// CodeMirror, copyright (c) by Marijn Haverbeke and others
// Distributed under an MIT license: https://codemirror.net/LICENSE

var loading = {};

function splitCallback(cont, n) {
    var countDown = n;
    return function() { if (--countDown == 0) cont(); };
}

function ensureDeps(mode, cont) {
    var deps = CodeMirror.modes[mode].dependencies;
    if (!deps) return cont();
    var missing = [];
    for (var i = 0; i < deps.length; ++i) {
        if (!CodeMirror.modes.hasOwnProperty(deps[i]))
            missing.push(deps[i]);
    }
    if (!missing.length) return cont();
    var split = splitCallback(cont, missing.length);
    for (var i = 0; i < missing.length; ++i)
        CodeMirror.requireMode(missing[i], split);
}

CodeMirror.requireMode = function(mode, cont) {
    if (typeof mode != "string") mode = mode.name;
    if (CodeMirror.modes.hasOwnProperty(mode)) return ensureDeps(mode, cont);
    if (loading.hasOwnProperty(mode)) return loading[mode].push(cont);

    require("codemirror/mode/"+mode+"/"+mode+".js");
    cont();
};

CodeMirror.autoLoadMode = function(instance, mode) {
    if (!CodeMirror.modes.hasOwnProperty(mode))
        CodeMirror.requireMode(mode, function() {
            instance.setOption("mode", instance.getOption("mode"));
        });
};

// ---->8-------->8-------->8-------->8-------->8-------->8-------->8----

const mode_info_text = CodeMirror.findModeByExtension('txt');
function getModeFromExtension(ext) {
    var info = CodeMirror.findModeByExtension(ext);
    if (info) {
        return info;
    } else {
        return mode_info_text;
    }
}

export class CodeEditor {
    constructor(textArea, options) {
        this.cm = CodeMirror.fromTextArea(textArea, options);
        this.mode = options["mode"];

        // track dirty state
        this.cm.on('change', function() {
            bridge.setDirty(true);
        });

        this.setFontSize(14);
    }

    clear() {
        this.setText("");
    }

    setText(text) {
        // force reset of placeholder (because it sometime fails to update when setting content)
        var placeholder = this.cm.getOption("placeholder");
        this.cm.setOption("placeholder", "");

        this.cm.swapDoc(CodeMirror.Doc(text, this.mode.mime));

        this.cm.setOption("placeholder", placeholder);
    }

    setContent(text, ext) {
        // clear buffer first
        this.clear();

        // set mode
        var mode = getModeFromExtension(ext);
        CodeMirror.autoLoadMode(this.cm, mode.mode);
        this.mode = mode;

        // set text
        this.setText(text);

        // inform Java code that the buffer is clean
        bridge.setDirty(false);
        console.log("LOAD: updated editor content, ext=" + ext + ", mode="
            + mode.mode);
    }

    // Set the editor content. Called from Java.
    // Paste text at current position. Called from Java code.
    replaceSelection(text) {
        trace("PASTING");
        this.cm.replaceSelection(text);
        trace("PASTED");
    }

    setModeFromExtension(ext) {
        var mode = getModeFromExtension(ext);
        this.cm.setOption("mode", mode.mime);
        CodeMirror.autoLoadMode(this.cm, mode.mode);
        trace("setModeFromExtension: mode set to " + mode.mode);
        this.mode = mode;
    }

    // set readonly mode
    setReadOnly(flag) {
        this.cm.setOption("readOnly", flag);
        trace("setReadOnly: readOnly = " + flag);
    }

    // set the placeholder text
    setPromptText(text) {
        this.cm.setOption("placeholder", text);
        trace("setPromptText: promptText = '"+text+"'");
    }

    // use the system clipboard for cut & paste
    paste() {
        trace("paste()");
        bridge.paste();
    }

    copy() {
        var text = this.cm.getSelection();
        trace("copy(): '"+text+"'");
        var arg = {
            'format' : 'text',
            'content' : text
        };
        bridge.copy(arg);
    }

    cut() {
        var text = this.cm.getSelection();
        trace("cut(): '"+text+"'");
        var arg = {
            'format' : 'text',
            'content' : text
        };
        bridge.cut(arg);
    }

    getText() {
        return this.cm.getDoc().getValue();
    }

    getLineCount() {
        return this.cm.lineCount();
    }

    getLine(idx) {
        return this.cm.getLine(idx);
    }

    getLineNumber(idx) {
        return this.cm.getCursor(idx).line;
    }

    search() {
        this.cm.execCommand("find");
    }

    setShowLineNumbers(flag) {
        trace('lineNumbers: '+flag);
        this.cm.setOption('lineNumbers', flag);
    }

    isShowLineNumbers() {
        return this.cm.getOption('lineNumbers');
    }

    setHighlightCurrentLine(flag) {
        trace('styleActiveLine: '+flag);
        this.cm.setOption('styleActiveLine', flag);
    }

    isHighlightCurrentLine() {
        return this.cm.getOption('styleActiveLine');
    }

    setFontSize(size) {
        this.cm.getWrapperElement().style["font-size"] = size+"px";
    }

    getFontSize() {
        var szs = this.cm.getWrapperElement().style["font-size"];
        return parseFloat(szs.replace("px",""));
    }

    setTheme(theme) {
        this.cm.setOption('theme', theme);
    }

    getTheme() {
        return this.cm.getOption('theme');
    }

    setLine(i,s) {
        this.cm.replaceRange(s, {line: i, ch: 0}, {line: i});
    }

    addLine(s) {
        this.cm.replaceRange(s+'\n', {line: Infinity, ch: 0});
    }

}

global.editorInstance = new CodeEditor(document.getElementById("editor"), {
    fullScreen : true,
    scrollbarStyle : 'overlay',
    mode : 'text',
    lineNumbers : false,
    inputStyle : 'textarea'
});