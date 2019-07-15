package com.dua3.fx.editor;

import com.dua3.fx.editor.cli.Cli;
import com.dua3.fx.application.FxController;
import com.dua3.fx.application.FxDocument;
import com.dua3.fx.editors.text.TextEditor;
import com.dua3.fx.editors.text.TextEditorSettings;
import com.dua3.fx.editors.text.TextEditorSettingsDialog;
import com.dua3.utility.io.IOUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

public class EditorController extends FxController<EditorApp, EditorController> {

    public class TextDocument extends FxDocument {
        TextDocument(URI location) {
            super(location);
        }
        
        @Override
        protected void write(URI uri) throws IOException {
            String text = editor.getText();
            Path path = Paths.get(uri);
            Files.write(path, text.getBytes(charset));
            editor.setDirty(false);
            LOG.info(() -> String.format("document written to '%s' using charset %s", uri, charset));
        }

		public void setDirty(boolean dirty) {
			dirtyProperty.setValue(dirty);
		}
	}

	private static final String PREF_EDITOR_PATH = "editor";

	private Charset charset = StandardCharsets.UTF_8;

	@FXML
	MenuBar menubar;

	@FXML
    TextEditor editor;
	
	@Override
	protected void init(EditorApp app) {
		// handle command line arguments
		Cli.apply(this, getApp().getParameters().getRaw().toArray(String[]::new));

		menubar.setUseSystemMenuBar(true);

		dirtyProperty.bind(editor.dirtyProperty());
		
		editor.editorReadyProperty().addListener((v,o,n) -> {
			// restore editor settings from preferences
			Preferences editorPref = getPreferences().node(PREF_EDITOR_PATH);
			editor.apply(TextEditorSettings.fromPreference(editorPref));
			// create a new document
			createDocument();
		});

		dirtyProperty.addListener((v,o,n) -> {
			if (hasCurrentDocument() || newDocument()) {
				getCurrentDocument().setDirty(n);
			}
		});
	}
	
	@Override
	protected List<FileChooser.ExtensionFilter> openFilters() {
		List<javafx.stage.FileChooser.ExtensionFilter> filters = super.openFilters();
		filters.add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
		return filters;
	}
	
	@Override
	protected TextDocument loadDocument(URI uri) throws IOException {
        Path path = Paths.get(uri);
        String content = IOUtil.loadText(path, cs -> charset = cs);
        String extension = IOUtil.getExtension(path);
        editor.setContent(content, extension);
        editor.setReadOnly(!Files.isWritable(path));
        editor.setDirty(false);
        LOG.info(() -> String.format("document read from '%s'", uri));
        return new TextDocument(uri);
	}

	@Override
	protected void createDocument() {
		setCurrentDocument(new TextDocument(FxDocument.VOID_URI));
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
		TextEditorSettingsDialog dlg = editor.settingsDialog();
		dlg.showAndWait()
		.filter(b -> b==ButtonType.OK)
		.ifPresentOrElse(
			b -> { 
				TextEditorSettings s = dlg.getSettings();
				editor.apply(s);
				s.store(getPreferences().node(PREF_EDITOR_PATH));
			},
			() -> { 
				TextEditorSettings s = dlg.getOldSettings();
				editor.apply(s);
			});
	}

    @Override
    public TextDocument getCurrentDocument() {
        return (TextDocument) super.getCurrentDocument();
    }

    @Override
    public List<TextDocument> dirtyDocuments() {
        boolean dirty = hasCurrentDocument() && getCurrentDocument().isDirty();
        return dirty ? List.of(getCurrentDocument()) : Collections.emptyList();
    }

}
