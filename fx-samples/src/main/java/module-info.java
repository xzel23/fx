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
 * Module-info.java file for the com.dua3.fx.samples module.
 * <p>
 * This module provides sample code and examples for the Dua3 FX library.
 */
module com.dua3.fx.samples {
    exports com.dua3.fx.samples;
    opens com.dua3.fx.samples;

    requires javafx.controls;

    requires com.dua3.utility;
    requires com.dua3.utility.logging;
    requires com.dua3.utility.logging.log4j;
    requires com.dua3.fx.icons;
    requires com.dua3.fx.controls;
    requires com.dua3.fx.util;
    requires static com.dua3.cabe.annotations;
    requires org.slf4j;
    requires java.logging;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.jul;
}
