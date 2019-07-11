var showdown  = require('showdown');

import './editor.css';

// connect to logger
if (window.hasOwnProperty('bridge')) {
    console.log = function (m) {
        bridge.log(m);
    };
}

// set to true to enable trace messages
var debug = true;

// output trace messages in debug mode
function trace(m) {
    if (debug) {
        console.log("TRACE " + m);
    }
}

class MarkdownEditor {
    constructor() {
        this.converter = new showdown.Converter();
        this.div = document.getElementById("editor");
    }

    clear() {
        this.div.setContent("");
    }

    setText(text) {
        var html = this.converter.makeHtml(text);
        this.div.innerHTML=html;
    }

    setContent(text, ext) {
        this.setText(text);
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

global.editorInstance = new MarkdownEditor();
