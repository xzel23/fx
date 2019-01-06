module com.dua3.fx.application {
	exports com.dua3.fx.application;
	opens com.dua3.fx.application;
	
	requires transitive java.logging;
    requires transitive com.dua3.utility;
    requires transitive com.dua3.fx.util;
    requires transitive java.prefs;

    requires java.desktop;
    
    requires javafx.base;
    requires javafx.graphics;
	requires javafx.controls;
	requires javafx.fxml;
	
}
