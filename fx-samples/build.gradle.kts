// Copyright 2019, 2022 Axel Howind
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

plugins {
    id("application")
}

description = "JavaFX utilities (samples)"

javafx {
    configuration = "implementation"
    modules = listOf("javafx.base", "javafx.fxml", "javafx.controls")
}

dependencies {
    implementation(rootProject.libs.dua3.utility)
    implementation(project(":fx-util"))
    implementation(project(":fx-controls"))
    implementation(project(":fx-icons"))
    runtimeOnly(project(":fx-icons:fx-icons-ikonli"))
    runtimeOnly(rootProject.libs.ikonli.fontawesome)

    implementation(rootProject.libs.dua3.utility.logging.log4j)
    implementation(rootProject.libs.log4j.core)
    implementation(rootProject.libs.log4j.jul)
    implementation(rootProject.libs.log4j.jcl)
    implementation(rootProject.libs.log4j.slf4j2)
    implementation(rootProject.libs.commons.logging)
    implementation(rootProject.libs.slf4j.api)
}

// set main
application {
    mainClass.set("com.dua3.fx.samples.Main")
}
