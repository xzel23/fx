module com.dua3.fx.util {
	exports com.dua3.fx.util;
	opens com.dua3.fx.util;
	
    requires com.dua3.utility;
	requires java.sql;

	requires transitive javafx.controls;
	requires javafx.fxml;
	requires java.desktop;
	requires javafx.graphics;
}
