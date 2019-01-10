module com.dua3.fx.editors {
	exports com.dua3.fx.editors;
	opens com.dua3.fx.editors;
	opens com.dua3.fx.editors.intern;

	requires com.dua3.fx.util;

	requires java.logging;
	requires javafx.base;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.web;
	requires jdk.jsobject;
}
