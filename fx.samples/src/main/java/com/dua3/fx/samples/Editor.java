package com.dua3.fx.samples;

import java.util.logging.Level;
import com.dua3.utility.lang.LangUtil;
import javafx.application.Application;
import com.dua3.fx.samples.editor.EditorApp;

public class Editor {

	public static void main(String[] args) {
		LangUtil.setLogLevel(Level.INFO);
		Application.launch(EditorApp.class, args);
	}

}
