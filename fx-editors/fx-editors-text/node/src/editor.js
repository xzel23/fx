// ====================================================================================================================

// define the Editor prototype

function Editor(name) { this.name = name; }

// define some logging methods - trace only outputs in debug mode
Editor.prototype.log              = console.log;
Editor.prototype.warning          = (msg)  => "[WARN] "+msg;
Editor.prototype.error            = (msg)  => "[ERROR] "+msg;
Editor.prototype.trace            = (msg)  => {};

Editor.debug                      = false;
Editor.isDebug                    = ()     => this.debug;
Editor.setDebug                   = (flag) => {
    if (flag) {
        this.debug = true;
        this.trace = (msg)  => this.log("[TRACE] "+msg);
    } else {
        this.debug = false;
        this.trace = (msg)  => {};
    }
}

//  the remaining methods are implementation dependent
Editor.prototype.setReadOnly      = (flag) => this.warning("missing override: Editor.setReadOnly(...)");
Editor.prototype.setPromptText    = (text) => this.warning("missing override: Editor.setPromptText()");
Editor.prototype.clear            = ()     => this.warning("missing override: Editor.clear()");
Editor.prototype.setText          = (text) => this.warning("missing override: Editor.setText(...)");
Editor.prototype.replaceSelection = (text) => this.warning("missing override: Editor.replaceSelection(...)");
Editor.prototype.getText          = ()     => this.warning("missing override: Editor.getText()");
Editor.prototype.getSelection     = ()     => this.warning("missing override: Editor.getSelection()");

// === Text Editor ====================================================================================================

// load Monaco Editor

import * as monaco from 'monaco-editor';

// Since packaging is done by you, you need
// to instruct the editor how you named the
// bundles that contain the web workers.
self.MonacoEnvironment = {
    getWorkerUrl: function (moduleId, label) {
        if (label === 'json') {
            return './json.worker.bundle.js';
        }
        if (label === 'css') {
            return './css.worker.bundle.js';
        }
        if (label === 'html') {
            return './html.worker.bundle.js';
        }
        if (label === 'typescript' || label === 'javascript') {
            return './ts.worker.bundle.js';
        }
        return './editor.worker.bundle.js';
    }
}

// --- create Text Editor instance

const editor_text = new Editor("txt");

editor_text.init = (name) => {
    editor_text.monaco = monaco.editor.create(document.getElementById('container'), {
        value: [
            'function x() {',
            '\tconsole.log("Hello world!");',
            '}'
        ].join('\n'),
        language: 'javascript'
    });
}

editor_text.setReadOnly      = (flag) => { this.trace("setReadOnly(...)"); this.monaco.updateOptions({ readOnly: flag }); }
editor_text.setPromptText    = (text) => this.trace("setPromptText()");
editor_text.clear            = ()     => { this.trace("clear()"); this.monaco.getModel().setValue(""); }
editor_text.setText          = (text) => { this.trace("setText(...)"); this.monaco.getModel().setValue(text); }
editor_text.replaceSelection = (text) => { this.trace("replaceSelection(...)"); this.monaco.replaceSelection(text); }
editor_text.getText          = ()     => { this.trace("getText()"); return this.monaco.getModel().getValue(); }
editor_text.getSelection     = ()     => { this.trace("getSelection()"); return this.monaco.getModel().getSelection(); }

window.editor_text = editor_text;

// === Markdown Editor ================================================================================================

// --- create Markdown Editor instance

const editor_markdown = new Editor("md");

editor_markdown.init = () => {
    // TODO
}

window.editor_markdown = editor_markdown;
