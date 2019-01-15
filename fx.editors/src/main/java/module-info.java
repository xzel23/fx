module com.dua3.fx.editors {
	exports com.dua3.fx.editors;
	opens com.dua3.fx.editors;
	opens com.dua3.fx.editors.intern;

	requires com.dua3.fx.util;
	requires com.dua3.fx.web;

	requires java.logging;
	requires java.prefs;
	requires javafx.base;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.web;
	requires jdk.jsobject;
}
