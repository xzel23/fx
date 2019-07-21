// ====================================================================================================================

class Editor {
    constructor(name, elementId) {
        this.name = name;
        this.element = document.getElementById(elementId);
    }

    //  the remaining methods are implementation dependent
    setReadOnly(flag) {
        console.error("method not overridden");
    }

    setPromptText(text) {
        console.error("missing override: Editor.setPromptText()");
    }

    clear() {
        console.error("missing override: Editor.clear()");
    }

    setText(text) {
        console.error("missing override: Editor.setText(...)");
    }

    replaceSelection(text) {
        console.error("missing override: Editor.replaceSelection(...)");
    }

    getText() {
        console.error("missing override: Editor.getText()");
    }

    getSelection() {
        console.error("missing override: Editor.getSelection()");
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

    constructor(name, elementId) {
        super(name, elementId);
        this.monaco = monaco.editor.create(this.element, {});
        console.info("new TextEditor instance: " + name);
    }

    setReadOnly(flag) {
        console.debug("setReadOnly(%s)", flag);
        this.monaco.updateOptions({readOnly: flag});
        console.info("readonly: %s", flag);
    }

    setPromptText(text) {
        console.debug("setPromptText()");
    }

    clear() {
        console.debug("clear()");
        this.monaco.getModel().setValue("");
    }

    setContent(text, uri) {
        console.debug("setContent()");
        const model = monaco.editor.createModel(text, undefined, uri);
        this.monaco.setModel(model);
        console.info("content set, language: " + model.language);
    }

    setText(text) {
        console.debug("setText()");
        this.monaco.getModel().setValue(text);
        console.info("text set, language: " + model.language);
    }

    replaceSelection(text) {
        console.debug("replaceSelection()");
        this.monaco.replaceSelection(text);
    }

    getText() {
        console.debug("getText()");
        return this.monaco.getModel().getValue();
    }

    getSelection() {
        console.debug("getSelection()");
        return this.monaco.getModel().getSelection();
    }

}

window.createTextEditor = function (name, element) {
    console.info("creating Text Editor instance with name '%s'", name);
    return new TextEditor(name, element);
};
