module com.dua3.fx.document {
	exports com.dua3.fx.document;
	opens com.dua3.fx.document;
	opens com.dua3.fx.document.intern;

	requires java.logging;
	requires javafx.base;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.web;
	requires jdk.jsobject;
	requires jdk.xml.dom;
}
