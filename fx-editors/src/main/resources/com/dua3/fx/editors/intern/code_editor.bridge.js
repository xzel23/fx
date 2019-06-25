// Copyright 2019 Axel Howind
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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

// Set the editor content. Called from Java.
function jSetContent(text, ext) {
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
function jReplaceSelection(text) {
	trace("PASTING");
	code_editor.replaceSelection(text);
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
	code_editor.setOption("mode", mode.mime);
	CodeMirror.autoLoadMode(code_editor, mode.mode);
	trace("jSetModeFromExtension: mode set to " + mode.mode);
}

// set readonly mode
function jSetReadOnly(flag) {
	code_editor.setOption("readOnly", flag);
	trace("jSetReadOnly: readOnly = " + flag);
}

// set the placeholder text
function jSetPromptText(text) {
	code_editor.setOption("placeholder", text);
	trace("jSetPromptText: promptText = '"+text+"'");
}

// use the system clipboard for cut & paste
function jPaste() {
	trace("jPaste()");
	bridge.paste();
}

function jCopy() {
	var text = code_editor.getSelection();
	trace("jCopy(): '"+text+"'");
	var arg = {
		'format' : 'text',
		'content' : text
	};
	bridge.copy(arg);
}

function jCut() {
	var text = code_editor.getSelection();
	trace("jCut(): '"+text+"'");
	var arg = {
		'format' : 'text',
		'content' : text
	};
	bridge.cut(arg);
}

function jGetText() {
	return code_editor.getDoc().getValue();
}

function jGetLineCount() {
	return code_editor.lineCount();
}

function jGetLine(idx) {
	return code_editor.getLine(idx);
}

function jGetLineNumber(idx) {
	return code_editor.getCursor(idx).line;
}

function jSearch() {
	code_editor.execCommand("find");
}

function jSetShowLineNumbers(flag) {
	trace('lineNumbers: '+flag);
	code_editor.setOption('lineNumbers', flag);
}

function jIsShowLineNumbers() {
	return code_editor.getOption('lineNumbers');
}

function jSetHighlightCurrentLine(flag) {
	trace('styleActiveLine: '+flag);
	code_editor.setOption('styleActiveLine', flag);
}

function jIsHighlightCurrentLine() {
	return code_editor.getOption('styleActiveLine');
}

function jSetFontSize(size) {
	code_editor.getWrapperElement().style["font-size"] = size+"px";
}

function jGetFontSize() {
	szs = code_editor.getWrapperElement().style["font-size"];
	return parseFloat(szs.replace("px",""));
}

function jSetTheme(theme) {
	code_editor.setOption('theme', theme);
}

function jGetTheme() {
	return code_editor.gcetOption('theme');
}

function jSetLine(i,s) {
	code_editor.replaceRange(s, {line: i, ch: 0}, {line: i});
}

function jAddLine(s) {
	code_editor.replaceRange(s+'\n', {line: Infinity, ch: 0});
}

// track dirty state
code_editor.on('change', function() {
	bridge.setDirty(true);
});

jSetFontSize(14);

const editor_initialised = true; 
