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

import org.jspecify.annotations.NullMarked;

/**
 * Provides classes and interfaces for building and managing JavaFX applications.
 */
@NullMarked
open module com.dua3.fx.application {
    exports com.dua3.fx.application;

    requires transitive com.dua3.utility.fx;
    requires transitive com.dua3.utility.fx.controls;

    requires com.dua3.utility;
    requires com.dua3.utility.logging;
    requires com.dua3.utility.logging.log4j;

    requires org.apache.logging.log4j;

    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires org.jspecify;
}
