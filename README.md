# fx

## Changes

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
