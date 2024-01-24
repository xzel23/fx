/**
 * This module is a Java module that provides integration of the Ikonli library with JavaFX Icons.
 * It exports the package com.dua3.fx.icons.ikonli and opens the same package for reflective access.
 *
 * This module requires the following external dependencies:
 * - org.apache.logging.log4j
 * - org.kordamp.ikonli.core
 * - org.kordamp.ikonli.javafx
 * - com.dua3.fx.icons
 * - javafx.graphics
 *
 * This module also uses the interface org.kordamp.ikonli.IkonHandler and provides an implementation
 * of the com.dua3.fx.icons.IconProvider interface with the class com.dua3.fx.icons.ikonli.IkonliIconProvider.
 */
module com.dua3.fx.icons.ikonli {
    exports com.dua3.fx.icons.ikonli;
    opens com.dua3.fx.icons.ikonli;

    requires org.apache.logging.log4j;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;

    requires transitive com.dua3.fx.icons;
    requires javafx.graphics;

    uses org.kordamp.ikonli.IkonHandler;
    provides com.dua3.fx.icons.IconProvider with com.dua3.fx.icons.ikonli.IkonliIconProvider;
}
