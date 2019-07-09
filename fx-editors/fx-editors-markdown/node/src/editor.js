import Editor from 'tui-editor';

import 'codemirror/lib/codemirror.css';
import 'tui-editor/dist/tui-editor.css';
import 'tui-editor/dist/tui-editor-contents.css';
import 'highlight.js/styles/github.css';

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

trace("MarkdownEditor class definition");

export class MarkdownEditor {
    constructor() {
        this.editor = new Editor({
            el: document.querySelector('#editSection'),
            initialEditType: 'markdown',
            previewStyle: 'vertical',
            height: '300px'
        });

        // TODO track dirty state
    }

    clear() {
        // TODO
    }

    setText(text) {
        // TODO
    }

    setContent(text, ext) {
        // TODO
    }

    // Set the editor content. Called from Java.
    // Paste text at current position. Called from Java code.
    replaceSelection(text) {
        // TODO
    }

    setModeFromExtension(ext) {
        // TODO
    }

    // set readonly mode
    setReadOnly(flag) {
        // TODO
    }

    // set the placeholder text
    setPromptText(text) {
        // TODO
    }

    // use the system clipboard for cut & paste
    paste() {
        // TODO
    }

    copy() {
        // TODO
    }

    cut() {
        // TODO
    }

    getText() {
        // TODO
    }

    getLineCount() {
        // TODO
    }

    getLine(idx) {
        // TODO
    }

    getLineNumber(idx) {
        // TODO
    }

    search() {
        // TODO
    }

    setShowLineNumbers(flag) {
        // TODO
    }

    isShowLineNumbers() {
        // TODO
    }

    setHighlightCurrentLine(flag) {
        // TODO
    }

    isHighlightCurrentLine() {
        // TODO
    }

    setFontSize(size) {
        // TODO
    }

    getFontSize() {
        // TODO
    }

    setTheme(theme) {
        // TODO
    }

    getTheme() {
        // TODO
    }

    setLine(i,s) {
        // TODO
    }

    addLine(s) {
        // TODO
    }

}

trace("MarkdownEditor define instance");

global.editorInstance = new MarkdownEditor();
