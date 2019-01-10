module com.dua3.fx.editors {
	exports com.dua3.fx.editors;
	opens com.dua3.fx.editors;
	opens com.dua3.fx.editors.intern;

	requires java.logging;
	requires javafx.base;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.web;
	requires jdk.jsobject;
	requires jdk.xml.dom;
	requires com.dua3.fx.util;
}
