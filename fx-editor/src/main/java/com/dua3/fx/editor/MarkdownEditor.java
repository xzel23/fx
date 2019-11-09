package com.dua3.fx.editor;

import javafx.application.Application;

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
