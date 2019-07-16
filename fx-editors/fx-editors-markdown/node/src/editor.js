var showdown  = require('showdown');

import './editor.css';
import './github-markdown.css';

// connect to logger
console.log = function (m) {
    bridge.log(m);
};

// set to true to enable trace messages
let debug = true;

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
        this.promptText = '';

        this.converter.setFlavor("github");
    }

    clear() {
        this.div.setContent("");
    }

    setText(text) {
        trace("setting text");
        let  html = this.converter.makeHtml(text);
        this.div.innerHTML=html;
    }

    // Set the editor content. Called from Java.
    setContent(text, ext) {
        trace("setting content");
        this.setText(text);
    }

    // Paste text at current position. Called from Java code.
    replaceSelection(text) {
        this.div.execCommand('insertText', false, data);
    }

    // set readonly mode
    setReadOnly(flag) {
        trace("readonly mode: "+flag);
        this.div.setAttribute("contenteditable", !flag);
    }

    // set the placeholder text
    setPromptText(text) {
        this.promptText = text;
    }

    // use the system clipboard for cut & paste
    paste() {
        trace("paste");
        let text = document.querySelector("#output");
        text.focus();
        document.execCommand("paste");
    }

    copy() {
        trace("copy");
        let selection = document.querySelector("#input");
        selection.select();
        document.execCommand("copy");
    }

    cut() {
        trace("cut");
        let selection = document.querySelector("#input");
        selection.select();
        document.execCommand("cut");
    }

    getText() {
        trace("getText");
        let html = this.div.innerHTML;
        var text = this.converter.makeMarkdown(html);
        return text;
    }

    heading(level) {
        this.div.focus();
        document.execCommand('formatBlock', false, '<h' + level + '>');
    }

    emphasis() {
        this.div.focus();
        document.execCommand("italic");
    }

    strong() {
        this.div.focus();
        document.execCommand("bold");
    }

    underline() {
        this.div.focus();
        document.execCommand("underline");
    }

    strikethrough() {
        this.div.focus();
        document.execCommand("strikethrough");
    }
}

global.editorInstance = new MarkdownEditor();
