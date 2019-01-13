package com.dua3.fx.samples;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.dua3.fx.samples.editor.EditorApp;
import com.dua3.utility.lang.LangUtil;

import javafx.application.Application;

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
