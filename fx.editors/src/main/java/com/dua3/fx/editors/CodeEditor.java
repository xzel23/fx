package com.dua3.fx.editors;

import java.util.logging.Logger;

import com.dua3.fx.editors.intern.EditorBase;

public class CodeEditor extends EditorBase {
	/** Logger */
    public static final Logger LOG = Logger.getLogger(CodeEditor.class.getName());

    /**
     * Default constructor.
     */
	public CodeEditor() {
		super("intern/code_editor.fxml", "intern/code_editor.html");
	}

	/**
	 * Set editing mode from file extension.
	 * 
	 * @param extension
	 *  the file extension
	 */
	public void setModeFromExtension(String extension) {
		LOG.fine(() -> String.format("setting mode by file extension: %s", extension));
		String script = String.format("jSetModeFromExtension('%s');", escape(extension));
		getBridge().executeScript(script);
	}	
}
