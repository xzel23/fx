rootProject.name = "dua3-fx"
val projectVersion = "0.30.1"

include("fx-util")
include("fx-util-db")
include("fx-application")
include("fx-controls")
include("fx-icons")
include("fx-icons:fx-icons-ikonli")
include("fx-web")
// samples and apps
include("fx-samples")

dependencyResolutionManagement {

    val isSnapshot = projectVersion.endsWith("SNAPSHOT")

    versionCatalogs {
        create("libs") {
            version("projectVersion", projectVersion)

            plugin("versions", "com.github.ben-manes.versions").version("0.49.0")
            plugin("test-logger", "com.adarshr.test-logger").version("4.0.0")
            plugin("spotbugs", "com.github.spotbugs").version("6.0.0-beta.4")
            plugin("cabe", "com.dua3.cabe").version("1.3.0")
            plugin("javafx", "org.openjfx.javafxplugin").version("0.1.0")

            version("cabe", "1.0.0")
            version("dua3-utility", "11.1.3")
            version("javafx", "21.0.1")
            version("ikonli", "12.3.1")
            version("junit", "5.10.0")
            version("log4j", "2.21.0")
            version("slf4j", "2.0.9")

            library("cabe-annotations", "com.dua3.cabe", "cabe-annotations").versionRef("cabe")
            library("dua3-utility", "com.dua3.utility", "utility").versionRef("dua3-utility")
            library("dua3-utility-db", "com.dua3.utility", "utility-db").versionRef("dua3-utility")
            library("dua3-utility-logging", "com.dua3.utility", "utility-logging").versionRef("dua3-utility")
            library("dua3-utility-swing", "com.dua3.utility", "utility-swing").versionRef("dua3-utility")
            library("ikonli-fontawesome", "org.kordamp.ikonli", "ikonli-fontawesome-pack").versionRef("ikonli")
            library("ikonli-javafx", "org.kordamp.ikonli", "ikonli-javafx").versionRef("ikonli")
            library("slf4j-api", "org.slf4j", "slf4j-api").versionRef("slf4j")
            library("slf4j-simple", "org.slf4j", "slf4j-simple").versionRef("slf4j")
            library("jul-to-slf4j", "org.slf4j", "jul-to-slf4j").versionRef("slf4j")
            library("log4j-to-slf4j", "org.apache.logging.log4j", "log4j-to-slf4j").versionRef("log4j")
            library("junit-jupiter-api", "org.junit.jupiter", "junit-jupiter-api").versionRef("junit")
            library("junit-jupiter-engine", "org.junit.jupiter", "junit-jupiter-engine").versionRef("junit")
        }
    }

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {

        // Maven Central Repository
        mavenCentral()

        // Sonatype Releases
        maven {
            name = "oss.sonatype.org-releases"
            url = java.net.URI("https://s01.oss.sonatype.org/content/repositories/releases/")
            mavenContent {
                releasesOnly()
            }
        }

        // Apache releases
        maven {
            name = "apache-releases"
            url = java.net.URI("https://repository.apache.org/content/repositories/releases/")
            mavenContent {
                releasesOnly()
            }
        }

        if (isSnapshot) {
            // local maven repository
            mavenLocal()

            // Sonatype Snapshots
            maven {
                name = "oss.sonatype.org-snapshots"
                url = java.net.URI("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                mavenContent {
                    snapshotsOnly()
                }
            }

            // Apache staging
            maven {
                name = "apache-staging"
                url = java.net.URI("https://repository.apache.org/content/repositories/staging/")
                mavenContent {
                    releasesOnly()
                }
            }

            // Apache snapshots
            maven {
                name = "apache-snapshots"
                url = java.net.URI("https://repository.apache.org/content/repositories/snapshots/")
                mavenContent {
                    snapshotsOnly()
                }
            }
        }
    }

}

