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
	
	@Override
	public void setShowLineNumbers(Boolean flag) {
		LOG.fine(() -> String.format("setting line number mode: %s", flag));
		String script = String.format("jSetShowLineNumbers(%s);", flag);
		getBridge().executeScript(script);
	}
	
	public boolean isShowLineNumbers() {
		return Boolean.TRUE.equals(getBridge().callScript("jIsShowLineNumbers()"));
	}
	
	@Override
	public void setFontSize(int size) {
		LOG.fine(() -> String.format("setting font size: %d", size));
		String script = String.format("jSetFontSize(%s);", size);
		getBridge().executeScript(script);
	}

	@Override
	public int getFontSize() {
		int size = ((Number) getBridge().callScript("jGetFontSize()")).intValue();
		LOG.fine(() -> String.format("font size: %d", size));		
		return size;
	}
	
	@Override
	public void setTheme(String theme) {
		LOG.fine(() -> String.format("setting theme: %s", theme));
		String script = String.format("jSetTheme('%s');", escape(theme));
		getBridge().executeScript(script);
	}
	
	@Override
	public String getTheme() {
		return String.valueOf(getBridge().callScript("jGetTheme()"));
	}
}
