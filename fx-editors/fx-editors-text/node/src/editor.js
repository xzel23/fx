// ====================================================================================================================

class Editor {
    constructor(name, elementId) {
        this.name = name;
        this.element = document.getElementById(elementId);
        this.setTheme('light');
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
        this.monaco = monaco.editor.create(this.element, {automaticLayout: true});
        console.info("new TextEditor instance: " + name);
    }

    setReadOnly(flag) {
        console.debug("setReadOnly(%s)", flag);
        this.monaco.updateOptions({readOnly: flag});
        console.info("readonly: %s", flag);
    }

    isReadOnly() {
        console.debug("isReadOnly(%s)");
        return this.monaco.getConfiguration().readOnly;
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
        const model = monaco.editor.createModel(text, undefined, monaco.Uri.parse(uri));
        this.monaco.setModel(model);
        console.info("content set, language: %s", this.monaco.getModel().getModeId())
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

    setShowLineNumbers(flag) {
        console.debug("setShowLineNumbers(%s)", flag);
        this.monaco.updateOptions({lineNumbers: flag});
    }

    isShowLineNumbers() {
        console.debug("isShowLineNumbers()");
        return this.monaco.getOptions().lineNumbers;
    }

    setFontSize(sz) {
        console.debug("setFontSize(%s)", sz);
        this.monaco.updateOptions({fontSize: sz + 'px'});
    }

    getFontSize() {
        console.debug("getFontSize()");
        return this.monaco.getConfiguration().fontInfo.fontSize;
    }

    setHighlightCurrentLine(flag) {
        console.debug("setHighlightCurrentLine()");
        let highlight = flag ? 'line' : 'none';
        this.monaco.updateOptions({renderLineHighlight: highlight});
    }

    isHighlightCurrentLine() {
        console.debug("isHighlightCurrentLine()");
        return this.monaco.getConfiguration().renderLineHighlight!=='none';
    }

    setTheme(theme) {
        console.debug("setTheme()");
        let monacoTheme = undefined;
        switch (theme) {
            default:
            case 'light':
                this.theme = 'light';
                monacoTheme = 'vs';
                break;
            case 'dark':
                this.theme = 'dark';
                monacoTheme = 'vs-dark';
                break;
            case 'high contrast':
                this.theme = 'high contrast';
                monacoTheme = 'hc-black';
                break;
        }

        monaco.editor.setTheme(monacoTheme);
    }

    getTheme() {
        console.debug("getTheme()");
        return this.theme;
    }
}

window.createTextEditor = function (name, element) {
    console.info("creating Text Editor instance with name '%s'", name);
    return new TextEditor(name, element);
};
