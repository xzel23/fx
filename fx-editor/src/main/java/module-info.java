module dua3_fx.editor {
    exports com.dua3.fx.editor;
    opens com.dua3.fx.editor;
    opens com.dua3.fx.editor.cli;

    requires java.logging;

    requires dua3_utility;
    requires dua3_fx.util;
    requires dua3_fx.application;
    requires dua3_fx.editors;
    requires dua3_fx.editors.text;

    requires info.picocli;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
	requires java.desktop;
}
