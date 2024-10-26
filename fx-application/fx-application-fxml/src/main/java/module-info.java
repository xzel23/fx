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
 * This module provides the base class for applications using FXML for declaring the user interface.
 */
@NullMarked
module com.dua3.fx.application.fxml {
    exports com.dua3.fx.application.fxml;
    opens com.dua3.fx.application.fxml;

    requires transitive com.dua3.fx.application;

    requires org.apache.logging.log4j;

    requires com.dua3.utility;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.jspecify;
}
