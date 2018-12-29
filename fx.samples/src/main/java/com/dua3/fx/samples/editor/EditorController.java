package com.dua3.fx.samples.editor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;

import com.dua3.fx.application.FxApplication;
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
	
	private boolean save(String text) {
		LOG.info("save");
		if (hasDocument()) {
			Path path = Paths.get(getDocument());
			try {
				Files.write(path, text.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
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
		return true;
	}

	@FXML
	void open() {
		FileChooser chooser = new FileChooser();
		File file = chooser.showOpenDialog(getApp().getStage());
		
		if (file!=null) {
			try {
				load(file);
			} catch (IOException e) {
				LOG.log(Level.WARNING, "error loading file: "+e.getMessage(), e);
				Dialogs.alert(AlertType.ERROR)
				.title("Error")
				.header("Error loading file.")
				.text(e.getMessage())
				.build()
				.showAndWait();
			}
		}
	}

	private void load(File file) throws IOException {
		String content = Files.readString(file.toPath());
		String extension = IOUtil.getExtension(file.getName());
		editor.setText(content, extension);
		editor.setReadOnly(!file.canWrite());
		setDocument(file.toURI());
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
	@FXML
	void save() {
		editor.save();
	}
}
