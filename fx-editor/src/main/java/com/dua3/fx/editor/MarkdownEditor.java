package com.dua3.fx.editor;

import com.dua3.utility.lang.LangUtil;
import javafx.application.Application;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MarkdownEditor {

    public static void main(String[] args) {
        try {
            Application.launch(MarkdownEditorApp.class, args);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

}
