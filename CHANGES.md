Changelog
=========

### version 1.1.0

- update utility to 16.2.0 (breaking change in Font class)

### version 1.0.7

- i18n: merge the default bundle into the parent bundle
- update dependencies
- small documentation fixes

### version 1.0.6

- introduce constants for exit codes
- do not use System.exit(0) when --help is passed on the command line
- suppress warning when printing help message to the terminal
- code cleanup

### version 1.0.5 (unreleased)

- dependency updates

### version 1.0.4

- add FxApplicationHelper
- update dependencies, gradle

### version 1.0.1, 1.0.2, 1.0.3

- Javadoc
- update dependencies

### version 1.0

- encapsulate properties
- use log4j BOM

### version 0.45

- remove SLF4J
- improve/add Javadoc
- fix nullability problems

### version 0.44

- JavaFX 23

### version 0.43

- code cleanup
- update dependencies
- make FxController.selectedOpenFilter()/selectedSaveFilter() abstract

### version 0.42

- code cleanup
- update dependencies

### version 0.41

- use I18N class for internationalization
- add FxLogPane, FxLogWindow
- FxLauncher.run() to execute task after platform startup
- removed the Cleaner from FxApplication; use PlatformHelper.getCleaner() instead

### version 0.40

- update dependencies
- code cleanup
- fix possible NPE

### version 0.39

- change valdiator decoration to warning symbol
- use daemon thread for FxRefresh
- add Cleaner support to PlatformHelper

### version 0.39

- JavaFX 22

### version 0.38

- add Javadoc
- update gradle
- update dependencies
- use jvm-test-suite plugin

### version 0.37

- remove InputPaneBuilder.buttons()
- update dependencies
- code cleanups

### version 0.36

- add missing package-info.java and annotations
- update dependencies, cabe, gradle

### version 0.35

- fix file input validation
- fix cell sporadically displaying text before edit after focus lost

### version 0.34.0

- remove dependency on FXML from fx-controls
- fix standard dialogs not returning values
- fix validation issues
- remove obsolete null checks
- add Javadoc
- add logging to samples
- code cleanup
- update utility

### version 0.33.0

- move dependency on FXML from fx-application to new fx-application-fxml class to facilitate creating non-FXML apps

### version 0.32.0

- use log4j-api instead of slf4j for all internal logging
- update dependencies
- added TableCellAutoCommit
- FxUtil.convert(Rectangle2f r)
- fixes and improvements

### version 0.31.0

- BREAKING: WebViews.setupEngine() take a logger name instead of a logger instance

### version 0.30.1

- update plugins and dependencies

### version 0.30.0

- conversion of Value to ObservableValue
- re-enable cabe generated assertions after build problems are fixed
- switch to Java 21
- minor fixes and cleanups

### version 0.29.0

- update dependencies

### version 0.28.0

- fix PinBoard content clipping

### version 0.27.0

- FxUtil.createMenuItem() -> Controls.menuItem()

### version 0.26.0

- overhaul of Validator class
- Controls.checkbox()
- Controls.tooltipIcon()

#### Version 0.25.0

#### Version 0.26.0

#### Version 0.23.0

- update JavaFX to 19.0.2.1

#### Version 0.2x.x

- updates and bugfixes;
- use GitHub CI

#### Version 0.19.0

- use version catalog for dependencies
- update dependencies
- JavaFX 19
- fixes and improvements

#### Version 0.18.0

- update dependencies
- log through SLF4J
- fixes and improvements

#### Version 0.17.0

- requires Java 17!
- JavaFX 18
- remove FxSettings
- code cleanups
- move all classes from fx.util.controls to fx.controls (also implies moving from module fx_util to fx_controls)
- Controls.makeResizable()
- FxApplication provides a Cleaner to use by applications
- register cleanup actions in FxApplication to execute when application stops
- FxUtil.copyToClipboard() (supports texts, images, files)
- FxUtil.createMenuItem()
- FxUtil.dragEventHandler()/dropEventHandler()
- Controls.toggleButton()
- replace File by Path where possible

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
- update Gradle to 6.3 (for JDK 14 support)
- update SpotBugs and SpotBugs gradle plugin
- update utility

#### Version 0.12

- new class FxSettings
- FxUtil: added methods to list available fonts
- FxController.getCurrentDocumentLocation()
- FxController.onDocumentUriChanged() (protected)
- FxUtil: new class RecentlyUsedDocumentsList

#### Version 0.11

- new Validator class

#### Version 0.10

- do not try to open non-existent files
- code cleanup
- update utility to 5.3
- Controller.getCurrentDir() gets the current (document) directory
- fix IAE in open dialog

#### Version 0.9.5

- code cleanups, remove workarounds for JavaFX bugs (working on a patch for JavaFX instead)

#### Version 0.9.4

- new FxLauncher class

#### Version 0.9.3

- most Dialogs/DialogBuilders have a new  `parentWindow` parameter. Stage-Icons (Windows platform) are copied from the
  parent window.

#### Version 0.9.2

- new static method `FxApplication.launchApplication()` as replacement for `Application.launch()`. When using this
  method, the commandline is automatically reparsed (see next item).

- new static method `FxApplication.reparseCommandLine(String[])` to fix messing up command line arguments on Windows.

#### Version 0.9.1

- make About-dialog styleable
- update dependencies and plugins

#### Version 0.9

- lots of i18n changes
- i18n of dialogs
- FxApplication constructor takes a resource bundle
- BREAKING CHANGE: fx-editors subproject has been removed
- possible to set graphic and details for about dialog
- support resource bundles in FxApplication

#### Version 0.8.4

- new publish to local repository
- allow setting CSS for AboutDialog and Alert
- update utility to 5.1.2

#### Version 0.8.3

- allow about dialog to grow to allow for longer title text

#### Version 0.8.2

- added FileChooserBuilder.showOpenMultipleDialog()
- update JavaFX to 13.0.2
- update utility to 5.1.1
- update spotbugs to 4.0.0-RC1

#### Version 0.8.1

- fix scroll to first line in Markdown editor if first line is not labeled (i.e., when using `${toc}`).
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

- Implementations must implement FxApplication.getPreferencesUserRoot(). This is necessary to work around the missing
  implementation in current versions og GraalVM/Gluon Client. dua3_utility.prefs can be used as an alternative
  implementation.

#### Version 0.6.1

- change the FxApplication constructor argument to URL because of access problems in jlinked applications

#### Version 0.6.0

- use Gradle 6
- update dependencies
- fix markdown-editor formula rendering
- update dependencies
- fix duplicate files in jar files

#### Version 0.5.0

- removed the fx-editor subproject which contained two sample applications
- module descriptors changed from com.dua3.fx to dua3_fx
- fx-icons: IconView class, many fixes
- fx-web: redirecting JavaScript console to Java
- fx-editors: switch to Monaco Editor
- fx-editors: add markdown support
- fx-editors: math support in Markdown using kaTeX
- log filtering (use `--log=<global_level>,<package_name>:,...)

#### Version 0.4.2

- avoid deadlocks when methods are not called on the Fx-Application thread

#### Version 0.4.1

- fix Exception in `getLineCount()` and `getLineNumber()`

### Version 0.4.0

- fx-editors: rename `CodeEditor` to `TextEditor`
- fx-editors: use webpack bundle
