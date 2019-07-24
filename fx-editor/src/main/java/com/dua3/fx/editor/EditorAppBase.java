package com.dua3.fx.editor;

import com.dua3.fx.application.FxApplication;

public class EditorAppBase extends FxApplication<EditorAppBase, EditorController> {

    private static final String VERSION = "V 0.1";
    private static final String COPYRIGHT = "©2019 Axel Howind";
    private static final String CONTACT_MAIL = "axel@dua3.com";

    public EditorAppBase(String fxmlFile, String appName) {
        super(fxmlFile);

        // set metadata
        setApplicationName(appName);
        setVersionString(VERSION);
        setCopyright(COPYRIGHT);
        setContactMail(CONTACT_MAIL);

        // force initialization of preferences
        getPreferences();
    }

}
