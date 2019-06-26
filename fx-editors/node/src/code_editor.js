import CodeMirror from 'codemirror';

import 'codemirror/mode/meta.js';
import 'codemirror/addon/selection/active-line.js';
import 'codemirror/addon/scroll/simplescrollbars.js';
import 'codemirror/addon/search/searchcursor.js';
import 'codemirror/addon/search/search.js';
import 'codemirror/addon/mode/loadmode.js';
import 'codemirror/addon/display/fullscreen.js';
import 'codemirror/addon/display/placeholder.js';

import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/xq-dark.css';
import 'codemirror/theme/xq-light.css';
import 'codemirror/addon/display/fullscreen.css';
import 'codemirror/addon/scroll/simplescrollbars.css';
import 'codemirror/addon/search/matchesonscrollbar.css';

CodeMirror.modeURL = 'codemirror/mode/%N/%N.js';

export const code_editor = CodeMirror.fromTextArea(document.getElementById('code_editor'), {
    fullScreen : true,
    scrollbarStyle : 'overlay',
    mode : 'text',
    lineNumbers : true,
    inputStyle : 'textarea'
});

// connect to logger
console.log = function(m) {
    bridge.log(m);
};

// set to true to enable trace messages
var debug = true;

// output trace messages in debug mode
export function trace(m) {
    if (debug) {
        console.log("TRACE " + m);
    }
}

// Set the editor content. Called from Java.
export function jSetContent(text, ext) {
    var mode = getModeFromExtension(ext);
    CodeMirror.autoLoadMode(code_editor, mode.mode);

    // force reset of placeholder (because it sometime fails to update when setting content)
    var placeholder = code_editor.getOption("placeholder");
    code_editor.setOption("placeholder", "");

    code_editor.swapDoc(CodeMirror.Doc(text, mode.mime));

    code_editor.setOption("placeholder", placeholder);

    // inform Java code that the buffer is clean
    bridge.setDirty(false);
    console.log("LOAD: updated editor content, ext=" + ext + ", mode="
        + mode.mode);
}

// Paste text at current position. Called from Java code.
export function jReplaceSelection(text) {
    trace("PASTING");
    code_editor.replaceSelection(text);
    trace("PASTED");
}

const mode_info_text = CodeMirror.findModeByExtension('txt');
export function getModeFromExtension(ext) {
    var info = CodeMirror.findModeByExtension(ext);
    if (info) {
        return info;
    } else {
        return mode_info_text;
    }
}

export function jSetModeFromExtension(ext) {
    var mode = getModeFromExtension(ext);
    code_editor.setOption("mode", mode.mime);
    CodeMirror.autoLoadMode(code_editor, mode.mode);
    trace("jSetModeFromExtension: mode set to " + mode.mode);
}

// set readonly mode
export function jSetReadOnly(flag) {
    code_editor.setOption("readOnly", flag);
    trace("jSetReadOnly: readOnly = " + flag);
}

// set the placeholder text
export function jSetPromptText(text) {
    code_editor.setOption("placeholder", text);
    trace("jSetPromptText: promptText = '"+text+"'");
}

// use the system clipboard for cut & paste
export function jPaste() {
    trace("jPaste()");
    bridge.paste();
}

export function jCopy() {
    var text = code_editor.getSelection();
    trace("jCopy(): '"+text+"'");
    var arg = {
        'format' : 'text',
        'content' : text
    };
    bridge.copy(arg);
}

export function jCut() {
    var text = code_editor.getSelection();
    trace("jCut(): '"+text+"'");
    var arg = {
        'format' : 'text',
        'content' : text
    };
    bridge.cut(arg);
}

export function jGetText() {
    return code_editor.getDoc().getValue();
}

export function jGetLineCount() {
    return code_editor.lineCount();
}

export function jGetLine(idx) {
    return code_editor.getLine(idx);
}

export function jGetLineNumber(idx) {
    return code_editor.getCursor(idx).line;
}

export function jSearch() {
    code_editor.execCommand("find");
}

export function jSetShowLineNumbers(flag) {
    trace('lineNumbers: '+flag);
    code_editor.setOption('lineNumbers', flag);
}

export function jIsShowLineNumbers() {
    return code_editor.getOption('lineNumbers');
}

export function jSetHighlightCurrentLine(flag) {
    trace('styleActiveLine: '+flag);
    code_editor.setOption('styleActiveLine', flag);
}

export function jIsHighlightCurrentLine() {
    return code_editor.getOption('styleActiveLine');
}

export function jSetFontSize(size) {
    code_editor.getWrapperElement().style["font-size"] = size+"px";
}

export function jGetFontSize() {
    szs = code_editor.getWrapperElement().style["font-size"];
    return parseFloat(szs.replace("px",""));
}

export function jSetTheme(theme) {
    code_editor.setOption('theme', theme);
}

export function jGetTheme() {
    return code_editor.gcetOption('theme');
}

export function jSetLine(i,s) {
    code_editor.replaceRange(s, {line: i, ch: 0}, {line: i});
}

export function jAddLine(s) {
    code_editor.replaceRange(s+'\n', {line: Infinity, ch: 0});
}

// track dirty state
code_editor.on('change', function() {
    bridge.setDirty(true);
});

jSetFontSize(14);

const editor_initialised = true;
