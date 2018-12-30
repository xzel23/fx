package com.dua3.fx.samples.editor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;

import com.dua3.fx.application.FxController;
import com.dua3.fx.editors.CodeEditor;
import com.dua3.fx.util.Dialogs;
import com.dua3.utility.io.IOUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

public class EditorController extends FxController<EditorApp, EditorController> {

	@FXML
	CodeEditor editor;
	
	@Override
	protected void init(EditorApp app) {
		editor.setOnSave(this::save);
		dirtyProperty.bind(editor.dirtyProperty());
	}
	
	@Override
	protected boolean saveDocument(URI uri) {
		String text = editor.getText();
		Path path = Paths.get(getDocument());

		try {
			Files.write(path, text.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
			setDocument(uri);
			return true;
		} catch (IOException e) {
			LOG.log(Level.WARNING, "error writing file", e);
			Dialogs.alert(AlertType.ERROR)
			.title("File could not be saved.")
			.header(path.toString())
			.text(e.getMessage())
			.build()
			.showAndWait();
			return false;
		}
	}

	@Override
	protected boolean openDocument(URI uri) {
		try {
			Path path = Paths.get(uri);
			String content = Files.readString(path);
			String extension = IOUtil.getExtension(path);
			editor.setText(content, extension);
			editor.setReadOnly(!Files.isWritable(path));
			setDocument(uri);
			return true;
		} catch (IOException e) {
			LOG.log(Level.WARNING, "error loading file: "+e.getMessage(), e);
			Dialogs.alert(AlertType.ERROR)
			.title("Error")
			.header("Error loading file.")
			.text(e.getMessage())
			.build()
			.showAndWait();
			return false;
		}
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
