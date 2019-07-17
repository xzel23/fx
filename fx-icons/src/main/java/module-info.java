module com.dua3.fx.icons {
    exports com.dua3.fx.icons;
    opens com.dua3.fx.icons;

    requires java.logging;
    requires javafx.graphics;

    uses com.dua3.fx.icons.IconProvider;
}
