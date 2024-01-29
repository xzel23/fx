// Copyright 2019 Axel Howind
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Provides classes and interfaces for building and managing JavaFX applications.
 * <p>
 * This module exports the {@link com.dua3.fx.application} package, which contains
 * classes and interfaces for creating and managing JavaFX applications. It also opens
 * the same package for reflective access.
 * <p>
 * Look into the module com.dua3.fx.application.fxml when using FXML to declare the user interface.
 * <p>
 * This module requires the transitive modules {@link com.dua3.fx.util} and {@link com.dua3.fx.controls},
 * which provide utility classes and controls for building JavaFX applications.
 * It also requires the non-transitive module {@link com.dua3.utility}, which provides
 * general utility classes.
 * <p>
 * This module requires the module {@code org.apache.logging.log4j} for logging.
 * <p>
 * This module requires the modules {@code java.prefs}, {@code javafx.base}, {@code javafx.controls},
 * and {@code java.desktop} for core functionality and user interface rendering.
 * <p>
 * This module also requires the module {@code com.dua3.cabe.annotations} for automatic generation of runtime
 * null checks in the compiled classes.
 */
module com.dua3.fx.application {
    exports com.dua3.fx.application;
    opens com.dua3.fx.application;

    requires transitive com.dua3.fx.util;
    requires transitive com.dua3.fx.controls;

    requires com.dua3.utility;

    requires org.apache.logging.log4j;

    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires java.desktop;
    requires static com.dua3.cabe.annotations;
}
