import org.gradle.internal.extensions.stdlib.toDefaultLowerCase

rootProject.name = "dua3-fx"
val projectVersion = "1.5.0-SNAPSHOT"

include("fx-application")
include("fx-application:fx-application-fxml")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {

    val isSnapshot = projectVersion.toDefaultLowerCase().contains("-snapshot")
    val isReleaseCandidate = projectVersion.toDefaultLowerCase().contains("-rc")

    versionCatalogs {
        create("libs") {
            version("projectVersion", projectVersion)

            plugin("cabe", "com.dua3.cabe").version("3.1.0")
            plugin("forbiddenapis", "de.thetaphi.forbiddenapis").version("3.9")
            plugin("javafx", "org.openjfx.javafxplugin").version("0.1.0")
            plugin("jmh", "me.champeau.jmh").version("0.7.3")
            plugin("jreleaser", "org.jreleaser").version("1.19.0")
            plugin("sonar", "org.sonarqube").version("6.2.0.5505")
            plugin("spotbugs", "com.github.spotbugs").version("6.2.0")
            plugin("test-logger", "com.adarshr.test-logger").version("4.0.0")
            plugin("versions", "com.github.ben-manes.versions").version("0.52.0")

            version("dua3-utility", "20.0.0-SNAPSHOT")
            version("dua3-license", "0.0.1-SNAPSHOT")
            version("javafx", "23.0.2")
            version("jmh", "1.37")
            version("jspecify", "1.0.0")
            version("log4j-bom", "2.25.0")
            version("spotbugs", "4.9.3")

            library("dua3-utility-bom", "com.dua3.utility", "utility-bom").versionRef("dua3-utility")
            library("dua3-utility", "com.dua3.utility", "utility").withoutVersion()
            library("dua3-utility-db", "com.dua3.utility", "utility-db").withoutVersion()
            library("dua3-utility-logging", "com.dua3.utility", "utility-logging").withoutVersion()
            library("dua3-utility-logging-log4j", "com.dua3.utility", "utility-logging-log4j").withoutVersion()
            library("dua3-utility-logging-slf4j", "com.dua3.utility", "utility-logging-slf4j").withoutVersion()
            library("dua3-utility-swing", "com.dua3.utility", "utility-swing").withoutVersion()
            library("dua3-utility-fx", "com.dua3.utility", "utility-fx").withoutVersion()
            library("dua3-utility-fx-controls", "com.dua3.utility", "utility-fx-controls").withoutVersion()
            library("dua3-license", "com.dua3.license", "license").versionRef("dua3-license")
            library("jspecify", "org.jspecify", "jspecify").versionRef("jspecify")
            library("log4j-bom", "org.apache.logging.log4j", "log4j-bom").versionRef("log4j-bom")
            library("log4j-api", "org.apache.logging.log4j", "log4j-api").withoutVersion()
            library("log4j-core", "org.apache.logging.log4j", "log4j-core").withoutVersion()
            library("log4j-jul", "org.apache.logging.log4j", "log4j-jul").withoutVersion()
            library("log4j-jcl", "org.apache.logging.log4j", "log4j-jcl").withoutVersion()
            library("log4j-slf4j2", "org.apache.logging.log4j", "log4j-slf4j2-impl").withoutVersion()
            library("log4j-to-slf4j", "org.apache.logging.log4j", "log4j-to-slf4j").withoutVersion()
        }
    }

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {

        // Maven Central Repository
        mavenCentral()

        // Sonatype Releases
        maven {
            name = "central.sonatype.com-releases"
            url = java.net.URI("https://central.sonatype.com/content/repositories/releases/")
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
            println("snapshot version detected, adding Maven snapshot repositories")

            // Sonatype Snapshots
            maven {
                name = "Central Portal Snapshots"
                url = java.net.URI("https://central.sonatype.com/repository/maven-snapshots/")
                mavenContent {
                    snapshotsOnly()
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

        if (isReleaseCandidate) {
            println("release candidate version detected, adding Maven staging repositories")

            // Apache staging
            maven {
                name = "apache-staging"
                url = java.net.URI("https://repository.apache.org/content/repositories/staging/")
                mavenContent {
                    releasesOnly()
                }
            }
        }
    }

}
