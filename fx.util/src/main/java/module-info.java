module com.dua3.fx.util {
	exports com.dua3.fx.util;
	opens com.dua3.fx.util;
	
	requires java.logging;
    requires com.dua3.utility;

	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires transitive java.desktop;
	requires transitive javafx.graphics;
}
