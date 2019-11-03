package com.dua3.fx.editor;

import com.dua3.fx.application.FxController;
import com.dua3.fx.application.FxDocument;
import com.dua3.fx.editors.text.TextEditor;
import com.dua3.fx.editors.text.TextEditorSettings;
import com.dua3.fx.editors.text.TextEditorSettingsDialog;
import com.dua3.fx.util.Dialogs;
import com.dua3.utility.io.IOUtil;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.prefs.Preferences;

public class EditorController extends FxController<EditorAppBase, EditorController> {

    private static final String PREF_EDITOR_PATH = "editor";
    @FXML
    MenuBar menubar;
    @FXML
    TextEditor editor;
    private Charset charset = StandardCharsets.UTF_8;

    @Override
    protected void init(EditorAppBase app) {
        // handle command line arguments
        List<String> args = getApp().getParameters().getUnnamed();
        URI documentPath = args.isEmpty() ? null : Paths.get(args.get(0)).toUri();

        dirtyProperty.bind(editor.dirtyProperty());

        editor.editorReadyProperty().addListener((v, o, n) -> {
            // restore editor settings from preferences
            Preferences editorPref = getPreferences().node(PREF_EDITOR_PATH);
            editor.apply(TextEditorSettings.fromPreference(editorPref));
            // load or create a new document
            if (documentPath != null) {
                try {
                    loadDocument(documentPath);
                } catch (IOException e) {
                    LOG.log(Level.WARNING, "could not load document: " + documentPath, e);
                    Dialogs.error()
                            .title("Error")
                            .header("Document could not be loaded: %s", documentPath)
                            .text(e.getMessage())
                            .showAndWait();
                }
            } else {
                createDocument();
            }
        });

        dirtyProperty.addListener((v, o, n) -> {
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
        editor.setContent(content, uri);
        editor.setReadOnly(!Files.isWritable(path));
        editor.markEditorClean();
        LOG.info(() -> String.format("document read from '%s'", uri));
        return new TextDocument(uri);
    }

    @Override
    protected void createDocument() {
        setCurrentDocument(new TextDocument(FxDocument.VOID_URI));
    }

    @FXML
    public void about() {
        LOG.fine("about()");
        createAboutDialog().showAndWait();
    }

    @FXML
    public void preferences() {
        LOG.fine("preferences()");
        TextEditorSettingsDialog dlg = editor.settingsDialog();
        dlg.showAndWait()
                .filter(b -> b == ButtonType.OK)
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

    public class TextDocument extends FxDocument {
        TextDocument(URI location) {
            super(location);
        }

        @Override
        protected void write(URI uri) throws IOException {
            String text = editor.getText();
            Path path = Paths.get(uri);
            Files.write(path, text.getBytes(charset));
            editor.markEditorClean();
            LOG.info(() -> String.format("document written to '%s' using charset %s", uri, charset));
        }

        public void setDirty(boolean dirty) {
            dirtyProperty.setValue(dirty);
        }
    }

    @FXML
    public boolean exportPdf() {
        if (!hasCurrentDocument()) {
            LOG.info("no document; not exporting");
            return false;
        }

        Path parent = null;
        String initialFileName = "";
        FxDocument document = getCurrentDocument();
        try {
            if (document.hasLocation()) {
                document = getCurrentDocument();
                parent = document.getPath().getParent();
                initialFileName = Objects.toString(document.getPath().getFileName(), "");
            } else {
                String lastDocument = getPreference(PREF_DOCUMENT, "");
                if (lastDocument.isBlank()) {
                    parent = USER_HOME.toPath();
                } else {
                    Path path = Paths.get(URI.create(lastDocument));
                    parent = path.getParent();
                    initialFileName = Objects.toString(path.getFileName(), "");
                    initialFileName = IOUtil.replaceExtension(initialFileName, "pdf");
                }
            }
        } catch (IllegalStateException e) {
            // might for example be thrown by URI.create()
            LOG.log(Level.WARNING, "could not determine initial folder", e);
        }

        File initialDir = parent!=null ? parent.toFile() :  null;

        if (initialDir == null || !initialDir.isDirectory()) {
            initialDir = USER_HOME;
        }

        Optional<File> file = Dialogs
                .chooseFile()
                .initialDir(initialDir)
                .initialFileName(initialFileName)
                .filter(saveFilters())
                .selectedFilter(selectedSaveFilter())
                .showSaveDialog(getApp().getStage());

        if (file.isEmpty()) {
            LOG.fine("exportPDF(): no file was chosen");
        }

        // save document content
        return exportAsPdfAndHandleErrors(editor.getPreviewHtml(), file.get().toURI());
    }

    private String toXHTML( String html ) {
        final Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html();
    }
    
    private boolean exportAsPdfAndHandleErrors(String html, URI uri) {
        assert html!= null;
        assert uri != null;
        try (OutputStream out=IOUtil.getOutputStream(uri)) {
            String xhtml = toXHTML(html);
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(xhtml, uri.toString());
            builder.toStream(out);
            builder.run();
            return true;
        } catch (Exception e) {
            LOG.log(Level.WARNING, "error exporting document", e);
            Dialogs.error()
                    .title(TITLE_ERROR)
                    .header("'%s' could not be exported.", getDisplayName(uri))
                    .text("%s: %s", e.getClass().getSimpleName(), e.getMessage())
                    .build()
                    .showAndWait();
            return false;
        }
    }

    @FXML
    public void copy() { editor.copy(); }

    @FXML
    public void cut() {
		editor.cut();
    }

    @FXML
    public void paste() { editor.paste(); }

    @FXML
    public void undo() { editor.undo(); }

    @FXML
    public void redo() { editor.redo(); }

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
