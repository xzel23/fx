module dua3_fx.icons.ikonli {
    exports com.dua3.fx.icons.ikonli;
    opens com.dua3.fx.icons.ikonli;

    requires java.logging;

    requires org.kordamp.iconli.core;
    requires org.kordamp.ikonli.javafx;

    requires dua3_fx.icons;
    requires javafx.graphics;

    uses org.kordamp.ikonli.IkonHandler;
    provides com.dua3.fx.icons.IconProvider with com.dua3.fx.icons.ikonli.IkonliIconProvider;
}
