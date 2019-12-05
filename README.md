# fx

## Changes

#### Version 0.5.0

- removed the `fx-editor` sub project which contained two sample applications
- module descriptors changed from `com.dua3.fx` to `dua3_fx`
- fx-icons: IconView class, many fixes
- fx-web: redirecting javaScript console to Java
- fx-editors: switch to Monaco Editor
- fx-editors: add markdown support
- fx-editors: math support in markdown using [kaTeX](https://www.katex.org)
- log filtering (use `--log=<global_level>,<package_name>:<level>,...)
 
#### Version 0.4.2

- avoid deadlocks when methods are not called on the Fx-Application thread

#### Version 0.4.1

- fix Exception in `getLineCount()` and `getLineNumber()`

### Version 0.4.0

- fx-editors: rename `CodeEditor` to `TextEditor`
- fx-editors: use webpack bundle
