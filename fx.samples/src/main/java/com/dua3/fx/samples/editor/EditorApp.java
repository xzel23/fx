package com.dua3.fx.samples.editor;

import com.dua3.fx.application.FxApplication;

public class EditorApp extends FxApplication<EditorApp,EditorController> {

	public EditorApp() {
		super("CodeEditor", "editor_app.fxml");
		
		// force initialization of preferences
		getPreferences();
	}

}
