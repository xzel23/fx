module dua3_fx.icons {
    exports com.dua3.fx.icons;
    opens com.dua3.fx.icons;

    requires java.logging;
    requires javafx.graphics;
    requires javafx.controls;

    uses com.dua3.fx.icons.IconProvider;
}
