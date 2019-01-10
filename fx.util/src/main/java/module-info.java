module com.dua3.fx.util {
	exports com.dua3.fx.util;
	opens com.dua3.fx.util;
	
	requires java.logging;
    requires com.dua3.utility;

	requires javafx.controls;
	requires javafx.fxml;
	requires java.desktop;
	requires javafx.graphics;
}
