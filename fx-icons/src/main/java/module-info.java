module com.dua3.fx.icons {
    exports com.dua3.fx.icons;
    opens com.dua3.fx.icons;

    requires org.apache.logging.log4j;

    requires javafx.controls;
    requires javafx.graphics;

    uses com.dua3.fx.icons.IconProvider;
}
