package com.dua3.fx.samples.editor;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import com.dua3.fx.application.FxController;
import com.dua3.fx.editors.CodeEditor;
import com.dua3.utility.io.IOUtil;

import javafx.fxml.FXML;

public class EditorController extends FxController<EditorApp, EditorController> {

	private Charset charset = StandardCharsets.UTF_8;

	@FXML
	CodeEditor editor;
	
	@Override
	protected void init(EditorApp app) {
		dirtyProperty.bind(editor.dirtyProperty());
	}
	
	@Override
	protected void openDocument(URI uri) throws IOException {
		Path path = Paths.get(uri);
		
		String content = IOUtil.loadText(path, cs -> charset=cs);
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
		Files.write(path, text.getBytes(charset));
		editor.setDirty(false);
		LOG.info(() -> String.format("document written to '%s' using charset", uri, charset));
	}

	@Override
	protected void createDocument() throws IOException {
		editor.setText("", "txt");
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
	public void about() {
		LOG.fine("about()");

		try {
			AboutDialog about = new AboutDialog();
			about.showAndWait();
			about.close();
		} catch (IOException e) {
			LOG.log(Level.WARNING, "could not show 'about' dialog", e);
		}
	}
}
