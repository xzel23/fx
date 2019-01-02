package com.dua3.fx.samples.editor;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CoderMalfunctionError;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.dua3.fx.application.FxController;
import com.dua3.fx.editors.CodeEditor;
import com.dua3.fx.util.Dialogs;
import com.dua3.utility.io.IOUtil;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class EditorController extends FxController<EditorApp, EditorController> {

	/** The default character encoding. */
	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	/** The character encodings used to load files. */
	private static final List<Charset> CHARSETS;

	static {
		// setup list of charset; use a set to avoif having duplicate entries
		Set<Charset> charsets = new LinkedHashSet<>();
		charsets.add(StandardCharsets.UTF_8);
		charsets.add(Charset.defaultCharset());
		charsets.add(StandardCharsets.ISO_8859_1);
		CHARSETS = new ArrayList<>(charsets);
	}

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
		
		String content = null;
		ByteBuffer data = ByteBuffer.wrap(Files.readAllBytes(path));
		data.mark();
		for (Charset cs : CHARSETS) {
			try {
				data.reset();
				content = cs.newDecoder()
					.decode(data)
					.toString();
				charset = cs;
				LOG.info(() -> String.format("loaded '%s' using charset %s", uri, charset));
				break;
			} catch (CharacterCodingException e) {
				LOG.fine(() -> String.format("could not decode '%s' using charset %s", uri, cs));
			}
		}

		if (content==null) {
			if (!Dialogs
				.confirmation("The character encoding of '%s' could not be determined. Open anyway?")
				.showAndWait()
				.equals(ButtonType.YES)) {
				LOG.info(() -> String.format("encoding problem - not loading '%s'", uri));
				return;
			}

			charset = DEFAULT_CHARSET;
			LOG.info(() -> "encoding problem - loading as "+charset.name());
			content = new String(data.array(), charset);
		}

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
