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

// the editor
CodeMirror.modeURL = "codemirror/mode/%N/%N.js";
const editor = CodeMirror.fromTextArea(document.getElementById("editor"), {
	fullScreen : true,
	scrollbarStyle : 'native',
	mode : 'text'
});

// Set the editor content. Called from Java.
function jSetContent(text, ext) {
	var mode = getModeFromExtension(ext);
	CodeMirror.autoLoadMode(editor, mode.mode);
	editor.swapDoc(CodeMirror.Doc(text, mode.mime));

	// inform Java code that the buffer is clean
	bridge.setDirty(false);
	console.log("LOAD: updated editor content, ext=" + ext + ", mode="
			+ mode.mode);
}

// Paste text at current position. Called from Java code.
function jReplaceSelection(text) {
	trace("PASTING");
	editor.replaceSelection(text);
	trace("PASTED");
}

const mode_info_text = CodeMirror.findModeByExtension('txt');
function getModeFromExtension(ext) {
	var info = CodeMirror.findModeByExtension(ext);
	if (info) {
		return info;
	} else {
		return mode_info_text;
	}
}

function jSetModeFromExtension(ext) {
	var mode = getModeFromExtension(ext);
	editor.setOption("mode", mode.mime);
	CodeMirror.autoLoadMode(editor, mode.mode);
	trace("jSetModeFromExtension: mode set to " + mode.mode);
}

// set readonly mode
function jSetReadOnly(flag) {
	editor.setOption("readOnly", flag);
	trace("jSetReadOnly: readOnly = " + flag);
}

// set the placeholder text
function jSetPromptText(text) {
	editor.setOption("placeholder", text);
	trace("jSetPromptText: promptText = '"+text+"'");
}

// commands

// use the system clipboard for cut & paste
function jPaste() {
	trace("paste [BEGIN]");
	bridge.paste();
	trace("paste [END]");
}

function jCopy() {
	trace("copy [BEGIN]");
	var text = editor.getSelection();
	var arg = {
		'format' : 'text',
		'content' : text
	};
	bridge.copy(arg);
	trace("copy [END]");
}

function jCut() {
	trace("cut [BEGIN]");
	var text = editor.getSelection();
	var arg = {
		'format' : 'text',
		'content' : text
	};
	bridge.cut(arg);
	trace("cut [END]");
}

function jGetText() {
	return editor.getDoc().getValue();
}

function jGetLineCount() {
	return editor.lineCount();
}

function jGetLine(idx) {
	return editor.getLine(idx);
}

function jGetLineNumber(idx) {
	return editor.getCursor(idx).line;
}

// search/replace

var searchcursor = null;

function jSearch(pattern, ignoreCase, regExp, wrapAround) {
	if (searchcursor==null) {
		searchcursor = getSearchCursor(
			pattern, 
			null,
			{
			  caseFold : ignoreCase
			});
	} else {
		searchcursor.findNext();
	}
	trace("search");	
}


// track dirty state
editor.on('change', function() {
	bridge.setDirty(true);
});

const editor_initialised = true; 
