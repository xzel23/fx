fx Application Framework
========================

The framework consists of the following libraries:

 - **fxApplication:** base classes for building applications
 
 - **fx-controls:** JavaFX controls
 
 - **fx-editors:** editor components for text and code
 
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

