package com.dua3.fx.samples.editor;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.dua3.fx.application.FxController;
import com.dua3.fx.editors.CodeEditor;
import com.dua3.utility.io.IOUtil;

import javafx.fxml.FXML;

public class EditorController extends FxController<EditorApp, EditorController> {

	@FXML
	CodeEditor editor;
	
	@Override
	protected void init(EditorApp app) {
		editor.setOnSave(this::save);
		dirtyProperty.bind(editor.dirtyProperty());
	}
	
	@Override
	protected void openDocument(URI uri) throws IOException {
		Path path = Paths.get(uri);
		String content = Files.readString(path);
		String extension = IOUtil.getExtension(path);
		editor.setText(content, extension);
		editor.setReadOnly(!Files.isWritable(path));
		editor.setDirty(false);
		LOG.info(() -> String.format("document read from '%s'", uri));
	}

	@Override
	protected void saveDocument(URI uri) throws IOException {
		String text = editor.getText();
		Path path = Paths.get(uri);
		Files.write(path, text.getBytes(StandardCharsets.UTF_8));
		editor.setDirty(false);
		LOG.info(() -> String.format("document written to '%s'", uri));
	}

	@FXML
	void copy() {
		editor.copy();
	}
	@FXML
	void cut() {
		editor.cut();
	}
	@FXML
	void paste() {
		editor.paste();
	}
}
