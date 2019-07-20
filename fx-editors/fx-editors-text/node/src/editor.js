// ====================================================================================================================

// define the Editor prototype

const logToConsole = function (name, level, msg) {
    console.log("[" + name + ":" + level + "] " + msg);
};

class Editor {
    constructor(name, elementId, log = logToConsole) {
        this.name = name;
        this.element = document.getElementById(elementId);
        this.log = (level, msg) => log(this.name, level, msg);

        this.LEVEL_ERROR = 3;
        this.LEVEL_WARNING = 2;
        this.LEVEL_INFO = 1;
        this.LEVEL_TRACE = 0;

        this.level = this.LEVEL_INFO;
    }

    error(msg) {
        log(this.LEVEL_ERROR, msg);
    }

    warning(msg) {
        this.log(this.LEVEL_WARNING, msg);
    }

    info(msg) {
        this.log(this.LEVEL_INFO, msg);
    }

    trace(msg) {
        this.log(this.LEVEL_TRACE, msg);
    }

    //  the remaining methods are implementation dependent
    setReadOnly(flag) {
        this.error("method not overridden");
    }

    setPromptText(text) {
        this.error("missing override: Editor.setPromptText()");
    }

    clear() {
        this.error("missing override: Editor.clear()");
    }

    setText(text) {
        this.error("missing override: Editor.setText(...)");
    }

    replaceSelection(text) {
        this.error("missing override: Editor.replaceSelection(...)");
    }

    getText() {
        this.error("missing override: Editor.getText()");
    }

    getSelection() {
        this.error("missing override: Editor.getSelection()");
    }
}

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
};

// --- create Text Editor instance

class TextEditor extends Editor {

    constructor(name, elementId, log = logToConsole) {
        super(name, elementId, log);

        this.monaco = monaco.editor.create(this.element, {});
    }

    setReadOnly(flag) {
        this.trace("setReadOnly(...)");
        this.monaco.updateOptions({readOnly: flag});
    }

    setPromptText(text) {
        this.trace("setPromptText()");
    }

    clear() {
        this.trace("clear()");
        this.monaco.getModel().setValue("");
    }

    setText(text) {
        this.trace("setText(...)");
        this.monaco.getModel().setValue(text);
    }

    replaceSelection(text) {
        this.trace("replaceSelection(...)");
        this.monaco.replaceSelection(text);
    }

    getText() {
        this.trace("getText()");
        return this.monaco.getModel().getValue();
    }

    getSelection() {
        this.trace("getSelection()");
        return this.monaco.getModel().getSelection();
    }

}

window.createTextEditor = function (name, element, log = logToConsole) {
    log("-", 1, "creating Text Editor instance");
    return new TextEditor(name, element, log);
};
