module dua3_fx.icons {
    exports com.dua3.fx.icons;
    opens com.dua3.fx.icons;

    requires java.logging;
    requires javafx.controls;
    requires javafx.graphics;

    uses com.dua3.fx.icons.IconProvider;
}
