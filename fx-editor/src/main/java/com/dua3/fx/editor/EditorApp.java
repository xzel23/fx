package com.dua3.fx.editor;

import com.dua3.fx.application.FxApplication;

public class EditorApp extends FxApplication<EditorApp,EditorController> {

	private static final String APP_NAME = "CodeEditor";
	private static final String VERSION = "V 0.1";
	private static final String COPYRIGHT = "©2019 Axel Howind";
	private static final String CONTACT_MAIL = "axel@dua3.com";
	
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
