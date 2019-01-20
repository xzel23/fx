package com.dua3.fx.samples.editor;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.Preferences;

import com.dua3.fx.application.FxController;
import com.dua3.fx.editors.CodeEditor;
import com.dua3.fx.editors.EditorSetting;
import com.dua3.fx.editors.EditorSettingsDialog;
import com.dua3.utility.io.IOUtil;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

public class EditorController extends FxController<EditorApp, EditorController> {

	private static final String PREF_EDITOR_PATH = "editor";

	private Charset charset = StandardCharsets.UTF_8;

	@FXML
	MenuBar menubar;

	@FXML
	CodeEditor editor;
	
	@Override
	protected void init(EditorApp app) {
		// use system menubar
		menubar.setUseSystemMenuBar(true);

		dirtyProperty.bind(editor.dirtyProperty());
		
		editor.editorReadyProperty().addListener((v,o,n) -> {
			// restore editor settings from preferences
			Preferences editorPref = getPreferences().node(PREF_EDITOR_PATH);
			editor.apply(EditorSetting.fromPreference(editorPref));
			// create a new document
			createDocument();
		});		
	}
	
	@Override
	protected List<FileChooser.ExtensionFilter> openFilters() {
		List<javafx.stage.FileChooser.ExtensionFilter> filters = super.openFilters();
		filters.add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
		return filters;
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
	protected void createDocument() {
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
		createAboutDialog().showAndWait();
	}
	
	@FXML
	public void search() {
		LOG.fine("search()");
		editor.search();
	}
	
	@FXML
	public void preferences() {
		LOG.fine("preferences()");
		EditorSettingsDialog dlg = editor.settingsDialog();
		dlg.showAndWait()
		.filter(b -> b==ButtonType.OK)
		.ifPresentOrElse(
			b -> { 
				EditorSetting s = dlg.getSetting();
				editor.apply(s);
				s.store(getPreferences().node(PREF_EDITOR_PATH));
			},
			() -> { 
				EditorSetting s = dlg.getOldSetting();
				editor.apply(s);
			});
	}
}
