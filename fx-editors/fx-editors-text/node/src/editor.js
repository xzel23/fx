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

    unselectText() {
        console.error("missing override: Editor.unselectText()");
    }

    undo() {
        console.error("missing override: Editor.undo()");
    }

    redo() {
        console.error("missing override: Editor.redo()");
    }
}

// === Text Editor ====================================================================================================

// load Monaco Editor

import * as monaco from 'monaco-editor';
import './editor_text.css';
import './editor_markdown.css';
import './github-markdown.css';
import 'katex/dist/katex.css';

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

// --- Text Editor class definition

class TextEditor extends Editor {

    constructor(name, elementId, options) {
        super(name, elementId);
        this.monaco = monaco.editor.create(this.element, options);

        this.setTheme('light');

        // track dirt state (see https://github.com/Microsoft/monaco-editor/issues/353)
        this.onChangedDirtyState = (flag) => {};
        this.trackEditorChanges(this.monaco.getModel());

        console.info("new TextEditor instance: " + name);
    }

    refresh() {
        // for overloading
    }

    trackEditorChanges(model) {
        console.debug("trackEditorChanges()");
        this.lastSavedVersionId = model.getAlternativeVersionId();
        this.dirty = false;

        model.onDidChangeContent((evt) => {
            this.onContentChange(model.getAlternativeVersionId());
        });
    }

    onContentChange(newVersionId) {
        let oldDirtyState = this.dirty;

        this.dirty = newVersionId !== this.lastSavedVersionId;

        if (this.dirty != oldDirtyState) {
            console.debug("dirty: %s", this.dirty);
            this.onChangedDirtyState(this.dirty);
        }

        this.currentVersionId = newVersionId;

        this.refresh();
    }

    markEditorClean() {
        this.lastSavedVersionId = this.monaco.getModel().getAlternativeVersionId();
        if (this.dirty) {
            this.dirty = false;
            this.onChangedDirtyState(this.dirty);
        }
    }

    setOnChangedDirtyState(callback) {
        console.debug("setOnChangedDirtyState()");
        this.onChangedDirtyState = callback;
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
        this.refresh();
    }

    setContent(text, arg) {
        console.debug("setContent()");
        let uri = undefined, language = undefined;

        if (typeof arg === 'string' && arg.indexOf(':') >= 0) {
            try {
                uri = monaco.Uri.parse(arg);
            } catch (err) {
                console.warn("could not parse URI: %s", arg);
            }
        } else {
            language = arg;
        }

        const model = monaco.editor.createModel(text, language, uri);

        const oldModel = this.monaco.getModel();
        if (oldModel !== undefined) {
            oldModel.dispose();
        }

        this.monaco.setModel(model);
        this.trackEditorChanges(model);
        this.refresh();

        console.info("content set, language: %s", this.monaco.getModel().getModeId())
    }

    setText(text) {
        console.debug("setText()");
        this.monaco.getModel().setValue(text);
        this.refresh();
        console.info("text set, language: %s", this.monaco.getModel().getModeId());
    }

    replaceSelection(text) {
        console.debug("replaceSelection()");
        this.monaco.executeEdits("", [
            { range: this.monaco.getSelection(), text: text }
        ]);
        this.refresh();
    }

    getText() {
        console.debug("getText()");
        return this.monaco.getModel().getValue();
    }

    getSelection() {
        console.debug("getSelection()");
        return this.monaco.getModel().getValueInRange(this.monaco.getSelection());
    }

    unselectText() {
        console.debug("unselectText()");
        let s = this.monaco.getSelection();
        if (s!=null) {
            let end = s.getEndPosition();
            this.monaco.setSelection(monaco.Range.fromPositions(end, end));
        }
    }
    
    getLine(i) {
        console.debug("getLine()");
        return this.monaco.getModel().getLineContent(i + 1); // Java class is zero based!
    }

    getLineCount() {
        console.debug("getLineCount()");
        return this.monaco.getModel().getLineCount();
    }

    getLineNumber() {
        console.debug("getLineNumber()");
        return this.monaco.getPosition().lineNumber;
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

    undo() {
        console.debug("undo()");
        this.monaco.trigger('', 'undo', '');
        this.monaco.focus();
    }

    redo() {
        console.debug("undo()");
        this.monaco.trigger('', 'redo', '');
        this.monaco.focus();
    }
}

window.createTextEditor = function (name, element) {
    console.info("creating Text Editor instance with name '%s'", name);
    return new TextEditor(name, element, {automaticLayout: true});
};

// === Markdown Editor ================================================================================================

const MarkdownIt = require('markdown-it');
const katex = require ('katex');
const MarkdownIt_texmath = require('markdown-it-texmath').use(katex, {
    throwOnError: false,
    errorColor: "#cc0000"
});
const MarkdownIt_diagrams = require('markdown-it-diagrams');

class MarkdownEditor extends TextEditor {

    constructor(name, elementIdEditor, elementIdPreview, options) {
        super(name, elementIdEditor, options);
        this.md = new MarkdownIt({
            html: false,        // Enable HTML tags in source
            xhtmlOut: false,        // Use '/' to close single tags (<br />).
            breaks: false,        // Convert '\n' in paragraphs into <br>
            linkify: false,        // Autoconvert URL-like text to links
            typographer: true
        });
        this.md
            .use(MarkdownIt_texmath)
            .use(MarkdownIt_diagrams.diagramPlugin)
            .use(require('markdown-it-sub'))
            .use(require('markdown-it-sup'))
            .use(require('markdown-it-mark'))
            .use(require('markdown-it-task-lists'))
            .use(require('markdown-it-anchor').default)
            .use(require('markdown-it-toc-done-right').default)
            .use(require('markdown-it-emoji'))
            .use(require('markdown-it-abbr'))
            .use(require('markdown-it-deflist'));
        ;

        MarkdownIt_diagrams.awaitRenderAvailable()
            .then(fullfilled => console.debug('markdownit-diagrams ready'))
            .catch(error => console.error(error.message()));

        this.lastPreviewVersionId = 0;
        this.elementPreview = document.getElementById(elementIdPreview);

        this.refresh();

        const instance = this;
    }

    updatePreview() {
        if (this.currentVersionId===this.lastPreviewVersionId) {
            // preview is up to date
            return;
        }

        this.refresh();
    }

    refresh() {
        this.lastPreviewVersionId = this.currentVersionId;

        let tStart = performance.now();
        let text = this.getText();
        let tText = performance.now();
        this.elementPreview.innerHTML = this.md.render(text);
        let tTranslate = performance.now();

        console.debug("updatePreview() - times:\n"
            + "    text retrieval:       %5d ms\n"
            + "    markdown translation: %5d ms",
            (tText - tStart),
            (tTranslate - tText));
    }
}

window.createMarkdownEditor = function (name, elementIdEditor, elementIdPreview) {
    console.info("creating Markdown Editor instance with name '%s'", name);
    return new MarkdownEditor(name, elementIdEditor, elementIdPreview, {
        automaticLayout: true,
        wordWrap: 'on'
    });
};
