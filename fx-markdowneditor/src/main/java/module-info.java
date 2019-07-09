module com.dua3.fx.editor {
    exports com.dua3.fx.markdowneditor;
    opens com.dua3.fx.markdowneditor;
    opens com.dua3.fx.markdowneditor.cli;

    requires java.logging;
    
    requires com.dua3.utility;
    requires com.dua3.fx.util;
    requires com.dua3.fx.application;
    requires com.dua3.fx.editors;
    requires com.dua3.fx.editors.markdown;

    requires info.picocli;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
	requires java.desktop;
}
