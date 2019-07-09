package com.dua3.fx.markdowneditor;

import com.dua3.utility.lang.LangUtil;
import javafx.application.Application;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Editor {

	public static void main(String[] args) {
		LangUtil.setLogLevel(Level.FINE);
		LangUtil.setLogLevel(Level.WARNING, 
				Logger.getLogger("com.sun"),
				Logger.getLogger("javafx")
			);
		Application.launch(EditorApp.class, args);
	}

}