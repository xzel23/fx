module  com.dua3.fx.icons.ikonli {
    exports com.dua3.fx.icons.ikonli;
    opens com.dua3.fx.icons.ikonli;

    requires org.slf4j;


    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;

    requires transitive com.dua3.fx.icons;
    requires javafx.graphics;

    uses org.kordamp.ikonli.IkonHandler;
    provides com.dua3.fx.icons.IconProvider with com.dua3.fx.icons.ikonli.IkonliIconProvider;
}
