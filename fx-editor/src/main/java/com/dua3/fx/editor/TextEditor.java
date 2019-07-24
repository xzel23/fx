package com.dua3.fx.editor;

import com.dua3.utility.lang.LangUtil;
import javafx.application.Application;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TextEditor {

	public static void main(String[] args) {
		LangUtil.setLogLevel(Level.FINE);
		LangUtil.setLogLevel(Level.WARNING, 
				Logger.getLogger("com.sun"),
				Logger.getLogger("javafx")
			);
        Application.launch(TextEditorApp.class, args);
	}

}
