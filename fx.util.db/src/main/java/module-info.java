module com.dua3.fx.util.db {
	exports com.dua3.fx.util.db;
	opens com.dua3.fx.util.db;
	
	requires transitive java.logging;
	
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	
    requires transitive com.dua3.utility.db;
    requires transitive com.dua3.fx.util;
}
