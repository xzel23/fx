package com.dua3.fx.editors;

import java.util.prefs.Preferences;

public class EditorSetting {
	private static final boolean DEFAULT_SHOW_LINE_NUMBERS = false;
	private static final boolean DEFAULT_HIGHLIGHT_CURRENT_LINE = false;
	private static final int DEFAULT_FONT_SIZE = 14;
	private static final String DEFAULT_THEME = "default";
	private static final String PREF_THEME = "theme";
	private static final String PREF_FONT_SIZE = "font_size";
	private static final String PREF_SHOW_LINE_NUMBERS = "show_line_numbers";
	private static final String PREF_HIHGHLIGHT_CURRENT_LINE = "highlight_current_line";
	private String theme;
	private int fontSize;
	private boolean showLineNumbers;
	private boolean highlightCurrentLine;

	public EditorSetting() {
		this.theme = DEFAULT_THEME;
		this.fontSize = DEFAULT_FONT_SIZE;
		this.showLineNumbers = DEFAULT_SHOW_LINE_NUMBERS;
		this.highlightCurrentLine = DEFAULT_HIGHLIGHT_CURRENT_LINE;
	}
	
	public void setTheme(String theme) {
		this.theme = theme;
	}
	
	public String getTheme() {
		return theme;
	}
	
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	
	public int getFontSize() {
		return fontSize;
	}
	
	public void setShowLineNumbers(boolean showLineNumbers) {
		this.showLineNumbers = showLineNumbers;
	}
	
	public boolean isShowLineNumbers() {
		return showLineNumbers;
	}

	public void setHighlightCurrentLine(boolean highlightCurrentLine) {
		this.highlightCurrentLine = highlightCurrentLine;
	}
	
	public boolean isHighlightCurrentLine() {
		return highlightCurrentLine;
	}
	
	public static EditorSetting copyOf(EditorSetting other) {
		EditorSetting inst = new EditorSetting();
		inst.assign(other);
		return inst;
	}

	public static EditorSetting fromPreference(Preferences node) {
		EditorSetting cs = new EditorSetting();
		cs.load(node);
		return cs;
	}

	public void load(Preferences node) {
		setTheme(node.get(PREF_THEME, DEFAULT_THEME));
		setFontSize(node.getInt(PREF_FONT_SIZE, DEFAULT_FONT_SIZE));
		setShowLineNumbers(node.getBoolean(PREF_SHOW_LINE_NUMBERS, DEFAULT_SHOW_LINE_NUMBERS));
		setHighlightCurrentLine(node.getBoolean(PREF_HIHGHLIGHT_CURRENT_LINE, DEFAULT_HIGHLIGHT_CURRENT_LINE));
	}

	public void store(Preferences node) {
		node.put(PREF_THEME, getTheme());
		node.putInt(PREF_FONT_SIZE, getFontSize());
		node.putBoolean(PREF_SHOW_LINE_NUMBERS, isShowLineNumbers());
		node.putBoolean(PREF_HIHGHLIGHT_CURRENT_LINE, isHighlightCurrentLine());
	}
	
	public void assign(EditorSetting other) {
		this.theme = other.theme;
		this.fontSize = other.fontSize;
		this.showLineNumbers = other.showLineNumbers;
		this.highlightCurrentLine = other.highlightCurrentLine;
	}

}
