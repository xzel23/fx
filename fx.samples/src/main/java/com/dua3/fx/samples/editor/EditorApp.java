package com.dua3.fx.samples.editor;

import com.dua3.fx.application.FxApplication;

public class EditorApp extends FxApplication<EditorApp,EditorController> {

	private final String APP_NAME = "CodeEditor";
	private final String VERSION = "V 0.1";
	private final String COPYRIGHT = "©2018 Axel Howind";
	private final String CONTACT_MAIL = "axel@dua3.com";
	
	public EditorApp() {
		super("editor_app.fxml");
		
		// set metadata
		setApplicationName(APP_NAME);
		setVersionString(VERSION);
		setCopyright(COPYRIGHT);
		setContactMail(CONTACT_MAIL);
		
		// force initialization of preferences
		getPreferences();
	}

}
