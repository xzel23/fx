# fx Application Framework
[![Apache License](https://img.shields.io/badge/license-Apache-blue)](LICENSE)
[![Language](https://img.shields.io/badge/language-Java-blue.svg?style=flat-square)](https://github.com/topics/java)
[![build](https://github.com/xzel23/fx/actions/workflows/CI.yml/badge.svg)](https://github.com/xzel23/fx/actions/workflows/CI.yml)

The framework consists of the following libraries:

- **fxApplication:** base classes for building applications

- **fx-controls:** JavaFX controls

- **fx-icons:** Icons to be used in JavaFX applications

- **fx-util:** JavaFX related utility classes

    - **fx-util-db:** JavaFX classes related to databases

- **fx-web:** classes related to the JavaFX WebView component

- **fx-samples:** sample applications

fx-application
--------------

Derive the application class from `FxApplication` and the controller from `FxController`.

### Logging

FxApplication supports a `--log` command line argument to control logging.

Examples:

- set general log level to `INFO`:

  `--log=INFO`

- set level for different packages:

  `--log=INFO,com.dua3:FINE,com.sun:WARNING`

Issues
------

- Cannot open documents on macOS by double-clicking unless the application is already running. Bug filed with
  review ID 9063702.
