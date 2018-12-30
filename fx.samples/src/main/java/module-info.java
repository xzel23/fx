module com.dua3.fx.samples {
    exports com.dua3.fx.samples;
    exports com.dua3.fx.samples.editor;
    opens com.dua3.fx.samples.editor;
    
    requires java.logging;
    
    requires com.dua3.utility;
    requires com.dua3.fx.util;
    requires com.dua3.fx.application;
    requires com.dua3.fx.editors;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
}
