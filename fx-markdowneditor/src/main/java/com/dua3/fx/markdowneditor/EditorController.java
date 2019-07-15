package com.dua3.fx.markdowneditor;

import com.dua3.fx.application.FxController;
import com.dua3.fx.application.FxDocument;
import com.dua3.fx.editors.markdown.MarkdownEditor;
import com.dua3.fx.editors.markdown.MarkdownEditorSettings;
import com.dua3.fx.editors.markdown.MarkdownEditorSettingsDialog;
import com.dua3.fx.markdowneditor.cli.Cli;
import com.dua3.utility.io.IOUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
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

    public class MarkdownDocument extends FxDocument {
        MarkdownDocument(URI location) {
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

	@FXML MenuBar menubar;
	@FXML ToolBar toolbar;
	@FXML MarkdownEditor editor;
	
	@Override
	protected void init(EditorApp app) {
		// handle command line arguments
		Cli.apply(this, getApp().getParameters().getRaw().toArray(String[]::new));

		// native look on MacOS
		menubar.setUseSystemMenuBar(true);

		// use the editor control's dirty property
		dirtyProperty.bind(editor.dirtyProperty());

		// apply preferences when editor is loaded
		editor.editorReadyProperty().addListener((v,o,n) -> {
			// restore editor settings from preferences
			Preferences editorPref = getPreferences().node(PREF_EDITOR_PATH);
			editor.apply(MarkdownEditorSettings.fromPreference(editorPref));
			// create a new document
			createDocument();
		});

		// track dirty state
		dirtyProperty.addListener((v,o,n) -> {
			if (hasCurrentDocument() || newDocument()) {
				getCurrentDocument().setDirty(n);
			}
		});

		// add actions to toolbar
		toolbar.getItems().addAll(editor.toolbarControls());
		toolbar.setVisible(true);
	}
	
	@Override
	protected List<FileChooser.ExtensionFilter> openFilters() {
		List<javafx.stage.FileChooser.ExtensionFilter> filters = super.openFilters();
		filters.add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
		return filters;
	}
	
	@Override
	protected MarkdownDocument loadDocument(URI uri) throws IOException {
        Path path = Paths.get(uri);
        String content = IOUtil.loadText(path, cs -> charset = cs);
        String extension = IOUtil.getExtension(path);
        editor.setContent(content, extension);
        editor.setReadOnly(!Files.isWritable(path));
        editor.setDirty(false);
        LOG.info(() -> String.format("document read from '%s'", uri));
        return new MarkdownDocument(uri);
	}

	@Override
	protected void createDocument() {
		setCurrentDocument(new MarkdownDocument(FxDocument.VOID_URI));
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
		// TODO
	}
	
	@FXML
	public void preferences() {
		LOG.fine("preferences()");
		MarkdownEditorSettingsDialog dlg = editor.settingsDialog();
		dlg.showAndWait()
		.filter(b -> b==ButtonType.OK)
		.ifPresentOrElse(
			b -> {
				MarkdownEditorSettings s = dlg.getSettings();
				editor.apply(s);
				s.store(getPreferences().node(PREF_EDITOR_PATH));
			},
			() -> {
				MarkdownEditorSettings s = dlg.getOldSettings();
				editor.apply(s);
			});
	}

    @Override
    public MarkdownDocument getCurrentDocument() {
        return (MarkdownDocument) super.getCurrentDocument();
    }

    @Override
    public List<MarkdownDocument> dirtyDocuments() {
        boolean dirty = hasCurrentDocument() && getCurrentDocument().isDirty();
        return dirty ? List.of(getCurrentDocument()) : Collections.emptyList();
    }

}
