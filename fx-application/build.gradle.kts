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

project.description = "JavaFX utilities (application)"

javafx {
    version = rootProject.libs.versions.javafx.get()
    modules("javafx.controls")
}

dependencies {
    api(rootProject.libs.dua3.utility)
    api(rootProject.libs.dua3.utility.logging)
    api(rootProject.libs.dua3.utility.logging.log4j)
    api(rootProject.libs.dua3.utility.fx)
    api(rootProject.libs.dua3.utility.fx.controls)
}

// Configure tests to run in a forked VM
tasks.test {
    useJUnitPlatform()

    // Run tests in a forked VM
    forkEvery = 1

    // Set JVM arguments for the forked VM
    jvmArgs = listOf(
        "-Djava.awt.headless=true",
        "-Dtestfx.robot=glass",
        "-Dtestfx.headless=true",
        "-Dprism.order=sw"
    )

    // Print test output to console
    testLogging {
        events("passed", "skipped", "failed")
    }
}
