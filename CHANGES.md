Changelog
=========

#### Version 0.17 (not released yet)
 - requires Java 17!
 - JavaFX 16 (update to 17 when it is released)
 - remove FxSettings
 - code cleanups
 - move all classes from fx.util.controls to fx.controls (also implies moving from module fx_util to fx_controls)
 - Controls.makeResizable()

#### Version 0.16
(unreleased)

#### Version 0.15
 - stop publishing to bintray
 - publish builds to GitLab packages
 - publish releases to Sonatype OSSRH 
 - Catch SecurityException when setting initial directory in FileChooserBuilder to an inaccessible path
 - update dependencies

#### Version 0.14.1

 - fix exception after setting invalid initial directory in FileChooserBuilder 

#### Version 0.14

 - update to JavaFX 15
 - update plugins
 - update dependencies
 
#### Version 0.13

 - update to JavaFX 14
 - update to ikonli 11.5.0
 - remove dependency on ControlsFX
 - update gradle to 6.3 (for JDK 14 support)
 - update SpotBugs and SpotBugs gradle plugin
 - update utility

#### Version 0.12

 - new class Fxsettings
 - FxUtil: added methods to list availbale fonts
 - FxControllergetCurrentDocumentLocation()
 - FxController.onDocumentUriChanged() (protected)
 - FxUtil: new class RecentlyUsedDocumentsList
 
#### Version 0.11

 - new Validator class

#### Version 0.10

 - do not try to open non-existent files
 - code cleanup
 - update utility to 5.3
 - Controller.getCurrentDir() gets the current (document) directory
 - fix IAE in open diualog
 
#### Version 0.9.5

 - code cleanups, remove workarounds for JavaFX bugs (working on a patch for JavaFX instead)
 
#### Version 0.9.4

 - new FxLauncher class
 
#### Version 0.9.3

 - most Dialogs/DialogBuilders have a new  `parentWindow` parameter. Stage-Icons (Windows platform) are copied from the parent window.
 
#### Version 0.9.2

 - new static method `FxApplication.launchApplication()` as replacement for `Application.launch()`. When using this method, the commandline is automatically re-parsed (see next item).
 
 - new static method `FxApplication.reparseCommandLine(String[])` to fix messing up comand line arguments on windows.
 
#### Version 0.9.1

 - make About-dialog styleable
 - update dependencies and plugins

#### Version 0.9

 - lots of i18n changes
 - i18n of dialogs
 - FxApplicationconstructor takes a resource bundle
 - BREAKING CHANGE: fx-editors subproject has been removed 
 - possible to set graphic and details for about dialog
 - support resource bundles in FxApplication
 
#### Version 0.8.4

 - new publish to local repository
 - allow to set CSS for AboutDialog and Alert
 - update utility to 5.1.2

#### Version 0.8.3

 - allow about dialog to grow to allow for longer title text

#### Version 0.8.2

 - added FileChooserBuilder.showOpenMultipleDialog()
 - update JavaFX to 13.0.2
 - update utility to 5.1.1
 - update spotbugs to 4.0.0-RC1
 
#### Version 0.8.1

 - fix scroll to first line in markdowneditor if first line is not labeled (i.e. when using `${toc}`).
 - set graphics in AboutDialogBuilder
 - set expandableContent in AboutDialogBuilder
 
#### Version 0.8

 - update utility to 5.0
 - fix NPE in initialDir
 - fix entry in META-INF for non-modular apps
 - add IconView constructor taking id, size and color
 - CSS loading
 
#### Version 0.7

 - don't export JavaFX dependencies
 - revert accidentally switching back to OpenJavaFX 11
 - cleanup the JavaFX dependency tree
 
#### Version 0.6.4

 - use the OpenJFX Gradle plugin; this should remove platform dependencies
 
#### Version 0.6.3

 - merge code for determining initial dir for saveAs() and open() and add some logging
 - FIX: don't overwrite last location when creating a new document
 - FxApplication.getController() (protected)
 
#### Version 0.6.2

- Implementations must implement FxApplication.getPreferencesUserRoot(). This is necessary to work around the missing implementation in current versions og GraalVM/Gluon Client. dua3_utility.prefs can be used as an alternative implementation.

#### Version 0.6.1

- change the FxApplication constructor argument to URL because of access problems in jlinked applications

#### Version 0.6.0

- use gradle 6
- update dependencies
- fix markdown-editor formula rendering
- update dependencies
- fix duplicate files in jar files

#### Version 0.5.0

- removed the fx-editor sub project which contained two sample applications
- module descriptors changed from com.dua3.fx to dua3_fx
- fx-icons: IconView class, many fixes
- fx-web: redirecting javaScript console to Java
- fx-editors: switch to Monaco Editor
- fx-editors: add markdown support
- fx-editors: math support in markdown using kaTeX
- log filtering (use `--log=<global_level>,<package_name>:,...)
 
#### Version 0.4.2

- avoid deadlocks when methods are not called on the Fx-Application thread

#### Version 0.4.1

- fix Exception in `getLineCount()` and `getLineNumber()`

### Version 0.4.0

- fx-editors: rename `CodeEditor` to `TextEditor`
- fx-editors: use webpack bundle
