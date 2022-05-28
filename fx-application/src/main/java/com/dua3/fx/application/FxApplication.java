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

package com.dua3.fx.application;

import com.dua3.fx.controls.Dialogs;
import com.dua3.fx.util.FxUtil;
import com.dua3.utility.data.Pair;
import com.dua3.utility.lang.LangUtil;
import com.dua3.utility.text.TextUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.desktop.AboutEvent;
import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenURIEvent;
import java.awt.desktop.PreferencesEvent;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.ref.Cleaner;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FxApplication<A extends FxApplication<A, C>, C extends FxController<A, C, ?>> 
        extends Application  {

    /**
     * Logger
     */
    private static final Logger LOG = Logger.getLogger(FxApplication.class.getName());

    /**
     * Cleaner
     */
    private static Cleaner cleaner = null;

    /**
     * List of Resource cleanup tasks to run on application stop.
     */
    private final List<Runnable> cleanupActions = new ArrayList<>();
    
    // - constants -

    /**
     * The command line argument to set the logging level (i.e. "--log=FINE").
     */
    public static final String ARG_LOG_LEVEL = "log";

    /**
     * Marker to indicate modified state in title.
     */
    private static final String MARKER_MODIFIED = "*";
    
    /**
     * Marker to indicate unmodified state in title.
     */
    private static final String MARKER_UNMODIFIED = " ";

    /**
     * The user's home folder.
     */
    private static final Path USER_HOME = Paths.get(System.getProperty("user.home"));

    /**
     * The name of the default bundle that is used if the application does not provide its own bundle.
     */
    private static final String DEFAULT_BUNDLE = "fxapp";

    /**
     * The resource bundle
     */
    private final ResourceBundle resources;
    
    /**
     * Preferences
     */
    private Preferences preferences = null;

    // - instance -

    /**
     * The controller instance.
     */
    private C controller;

    /**
     * The main stage.
     */
    private Stage mainStage;

    // - UI -

    // - static initialization -

    // - Code -

    /**
     * Get default resource bundle.
     * @return the default resource bundle
     */
    public static ResourceBundle getDefaultBundle() {
        // load resource bundle
        Locale locale = Locale.getDefault();
        LOG.fine(() -> "current locale is: "+locale);
        ResourceBundle resources = ResourceBundle.getBundle(FxApplication.class.getPackageName()+"."+DEFAULT_BUNDLE, locale);
        LOG.fine(() -> "resource bundle uses locale: "+ resources.getLocale());
        return resources;
    }

    /**
     * Get default Cleaner.
     * @return the {@link Cleaner} instance
     */
    public static synchronized Cleaner getCleaner() {
        if (cleaner == null) {
            cleaner = Cleaner.create();
        }
        return cleaner;
    }
    
    /**
     * Constructor.
     */
    protected FxApplication() {
        this(getDefaultBundle());
    }

    /**
     * Constructor.
     * 
     * @param resourceBundle the resource bundle for retrieving resources
     */
    protected FxApplication(ResourceBundle resourceBundle) {
        this.resources = Objects.requireNonNull(resourceBundle);
    }
    
    /**
     * Get application main FXML file.
     * @return the path to the FXML file to load, relative to the
     *                 application class
     */
    protected abstract URL getFxml();

    /**
     * Get application main resource bundle.
     * @return the resource bundle or {@code null}
     */
    protected ResourceBundle getResourceBundle() {
        return null;
    }
    
    /**
     * Get application main CSS file.
     * @return the path to the CSS file to load, relative to the
     *                 application class, or {@code null}
     */
    protected URL getCss() {
        return null;
    }

    /**
     * Get named parameter value.
     * <p>
     * Named parameters are command line arguments of the form "--parameter=value".
     *
     * @param name the parameter name
     * @return an Optional holding the parameter value if present
     */
    public Optional<String> getParameterValue(String name) {
        return Optional.ofNullable(getParameters().getNamed().get(name));
    }

    /**
     * Check if an unnamed parameter is present.
     *
     * @param name the parameter name
     * @return true, if the parameter is present
     */
    public boolean hasParameter(String name) {
        return getParameters().getUnnamed().contains(name);
    }

    /**
     * Initialize User Interface. The layout is defined in FXML.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage stage) throws IOException {
        LOG.log(Level.INFO, () -> "starting application ...");

        // store reference to stage
        this.mainStage = stage;

        // handle program arguments
        getParameterValue("log").ifPresent(this::setLogLevel);

        // create a loader and load FXML
        URL fxml = getFxml();
        LOG.log(Level.FINER, () -> "FXML URL: " + fxml);
        FXMLLoader loader = new FXMLLoader(fxml, resources);

        Parent root = loader.load();

        // set controller
        LOG.log(Level.FINER, () -> "setting FXML controller ...");
        this.controller = Objects.requireNonNull(loader.getController(),
                () -> "controller is null; set fx:controller in root element of FXML (" + fxml + ")");
        this.controller.setApp((A) this);

        // create scene
        Scene scene = new Scene(root);

        URL css = getCss();
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        // setup stage
        stage.setTitle(resources.getString("fx.application.name"));
        stage.setScene(scene);

        // automatically update title on document change
        final ChangeListener<Boolean> dirtyStateListener = (v, o, n) -> updateApplicationTitle();

        final ChangeListener<URI> locationListener = (v, o, n) -> updateApplicationTitle();

        controller.currentDocumentProperty.addListener(
                (v, o, n) -> {
                    updateApplicationTitle();
                    if (o != null) {
                        o.dirtyProperty.removeListener(dirtyStateListener);
                        o.locationProperty.removeListener(locationListener);
                    }
                    if (n != null) {
                        n.dirtyProperty.addListener(dirtyStateListener);
                        n.locationProperty.addListener(locationListener);
                    }
                });

        stage.setOnCloseRequest(e -> {
            e.consume();
            controller.closeApplicationWindow();
        });

        stage.show();

        LOG.fine(() -> "done.");
    }
    
    /**
     * Pattern for parsing the log configuration string.
     * Example:
     * {@code
     *     --log=INFO,com.dua3:FINE,com.sun:WARNING
     * }
     */
    private static final Pattern patternLogCfg = Pattern.compile("(?:(?<PACKAGE>(?:\\w|\\.)+):)?(?<LEVEL>[A-Z0-9]+)(?:,|$)");

    private final Map<String,Level> logLevel = new ConcurrentHashMap<>();
    private Level globalLogLevel = Level.INFO;
    
    private void setLogLevel(String logStr) {
        // determine the global and minimum log levels and store mapping package -> level
        Matcher matcher = patternLogCfg.matcher(logStr);
        Level minLevel = Level.OFF;
        while (matcher.find()) {
            String levelStr = matcher.group("LEVEL");
            if (levelStr!=null) {
                String p = matcher.group("PACKAGE");
                Level l = Level.parse(levelStr);
                if (p != null) {
                    var old = logLevel.put(p, l);
                    LangUtil.check(old == null, "log level for package '%s' defined twice", p);
                } else {
                    globalLogLevel = l;
                }
                if (l.intValue()<minLevel.intValue()) {
                    minLevel = l;
                }
            }
        }

        globalLogLevel = logLevel.getOrDefault("", globalLogLevel);

        if (globalLogLevel.intValue()<minLevel.intValue()) {
            minLevel = globalLogLevel;
        }
        
        // set root level
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(minLevel);

        // set filter
        Filter f = record -> {
            String loggerName = record.getLoggerName();
            Level level = globalLogLevel;
            for (var entry: logLevel.entrySet()) {
                if (loggerName.startsWith(entry.getKey())) {
                    level = entry.getValue();
                }
            }
            return record.getLevel().intValue()>=level.intValue();
        };

        for (Handler h : rootLogger.getHandlers()) {
            h.setFilter(f);
            h.setLevel(minLevel);
        }

        LOG.info(() -> "log level set to "+logLevel);
    }

    protected void updateApplicationTitle() {
        StringBuilder title = new StringBuilder();
        title.append(resources.getString("fx.application.name"));

        FxDocument document = controller.getCurrentDocument();

        if (document != null) {
            String locStr = document.hasLocation() ?
                    FxUtil.asText(document.getLocation()) : 
                    resources.getString("fx.application.text.untitled");
            boolean dirty = document.isDirty();

            if (!locStr.isEmpty() || document.isDirty()) {
                title.append(" - ");
            }

            String marker = dirty ? MARKER_MODIFIED : MARKER_UNMODIFIED;

            title.append(marker).append(locStr);
        }

        mainStage.setTitle(title.toString());
    }

    /**
     * Close the application.
     * <p>
     * Don't ask the user if he wants to save his work first - this should be handled by the controller.
     */
    public void closeApplicationWindow() {
        if (hasPreferences()) {
            try {
                getPreferences().flush();
            } catch (BackingStoreException e) {
                LOG.log(Level.WARNING, "could not update preferences", e);
            }
        }

        mainStage.close();
        
        mainStage = null; // make it garbage collectable
    }

    /**
     * Get the stage.
     *
     * @return the application's primary stage, or null if the application has been closed
     */
    public Stage getStage() {
        return mainStage;
    }

    /**
     * Get the Preferences instance for this application.
     * <p>
     * The Preferences instance will be created on demand if it doesn't exist yet,
     *
     * @return the preferences object for this application
     */
    public final Preferences getPreferences() {
        if (!hasPreferences()) {
            Class<?> cls = getClass();
            LOG.fine("creating preferences for class " + cls.getName());
            preferences = Preferences.userRoot().node(getClass().getName());
        }
        return preferences;
    }
    
    /**
     * Check whether a preferences object for this class has been created.
     *
     * @return true, if a Preferences object has been created
     */
    protected final boolean hasPreferences() {
        return preferences != null;
    }

    private final Path applicationDataDir = initApplicationDataDir();

    private Path initApplicationDataDir() {
        try {
            String dirName = getClass().getName();

            // try to determine location by evaluating standard windows settings
            String appData = System.getenv("LOCALAPPDATA");
            if (appData == null) {
                appData = System.getenv("APPLICATION_DATA_DIR");
            }
            if (appData != null) {
                Path dir = Paths.get(appData).resolve(dirName);
                Files.createDirectories(dir);
                return dir;
            }

            // then check for macOS
            Path home = Paths.get(System.getProperty("user.home"));
            Path macosBase = home.resolve(Paths.get("Library", "Application Support"));
            if (Files.isDirectory(macosBase) && Files.isWritable(macosBase)) {
                Path dir = macosBase.resolve(dirName);
                Files.createDirectories(dir);
                return dir;
            }

            // as last resort, use a dot file in user's home directory
            Path dir = home.resolve(dirName.replace(' ', '_').toLowerCase(Locale.ROOT));
            Files.createDirectories(dir);
            return dir;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Get this applications data folder.
     * @return  the data folder for this application
     */
    public Path getDataDir() {
        return applicationDataDir;
    }

    /**
     * Get the controller instance.
     * @return the controller
     */
    protected C getController() {
        return controller;
    }

    /**
     * Show error dialog.
     * @param header	the header
     * @param text		the text
     */
    public void showErrorDialog(String header, String text) {
        Dialogs.error(mainStage)
                .title("%s", resources.getString("fx.application.dialog.error.title"))
                .header("%s", header)
                .text("%s", text)
                .build()
                .showAndWait();
    }

    /**
     * If this application uses preferences, set the value. Otherwise, do nothing.
     * @param key   the key
     * @param value the value
     * @return  true if the key was set in the preferences, otherwise false
     */
    public boolean setPreferenceOptional(String key, String value) {
        if (hasPreferences()) {
            LOG.fine(() -> String.format("setting preference '%s'", key));
            setPreference(key, value);
            return true;
        }
        LOG.fine(() -> String.format("not setting preference '%s': preferences not initialised", key));
        return false;
    }

    /**
     * Set preference value
     * @param key   the key
     * @param value the value
     */
    public void setPreference(String key, String value) {
        getPreferences().put(key, value);
    }

    /**
     * Get the preference value.
     * @param key   the preference key
     * @param def   the default value
     * @return the value stored in the preferences for this key if present, or the default value
     */
    public String getPreference(String key, String def) {
        return hasPreferences() ? getPreferences().get(key, def) : def;
    }

    /**
     * Show this application's preferences dialog.
     */
    public abstract void showPreferencesDialog();

    /**
     * Show this application's about dialog.
     */
    public void showAboutDialog() {
        showAboutDialog(null);
    }

    /**
     * Show this application's about dialog.
     * 
     * @param css   URL to the CSS data
     */
    protected void showAboutDialog(URL css) {
        Dialogs.about(mainStage)
                .title(resources.getString("fx.application.about.title"))
                .name(resources.getString("fx.application.name"))
                .version(getVersion())
                .copyright(resources.getString("fx.application.about.copyright"))
                .graphic(LangUtil.getResourceURL(
                        getClass(),
                        resources.getString("fx.application.about.graphic"),
                        resources.getLocale()))
                .mail(
                        resources.getString("fx.application.about.email"),
                        TextUtil.generateMailToLink(
                                resources.getString("fx.application.about.email"),
                                resources.getString("fx.application.name")
                                        + " "
                                        + getVersion()))
                .expandableContent(resources.getString("fx.application.about.detail"))
                .css(css)
                .build()
                .showAndWait();
    }

    /**
     * Get this application's version string.
     * @return version string
     */
    public abstract String getVersion();

    /**
     * Get file extension filter for all files ('*.*').
     * @return file extension filter accepting all files
     */
    public FileChooser.ExtensionFilter getExtensionFilterAllFiles() {
        return new FileChooser.ExtensionFilter(resources.getString("fx.application.filter.all_files"), "*.*");
    }

    /**
     * Get the user's home directory. 
     * @return the user's home directory
     */
    public Path getUserHome() {
        return USER_HOME;
    }
    
    @SafeVarargs
    public final String getMessage(String key, Pair<String,String>... substitutions) {
        return TextUtil.transform(resources.getString(key), substitutions);
    }

    public void openFiles(OpenFilesEvent e) {
        e.getFiles().forEach(f -> Platform.runLater(() -> {
            if (f.exists()) {
                mainStage.show();
                controller.open(f.toURI());
            } else {
                LOG.warning("openFiles: ignoring non-existent file: "+f );
            }
        }));
    }

    public void openURI(OpenURIEvent e) {
        Platform.runLater(() -> {
            mainStage.show();
            controller.open(e.getURI());
        });
    }

    public void handleAbout(AboutEvent e) {
        Platform.runLater(this::showAboutDialog);
    }

    public void handlePreferences(PreferencesEvent e) {
        Platform.runLater(this::showPreferencesDialog);
    }

    /**
     * Add a resource cleanup action to run when the application stops.
     * @param task the action to perform
     */
    public void addCleanupAction(Runnable task) {
        cleanupActions.add(task);
    }

    /**
     * Remove a resource cleanup action.
     * @param task the action to remove
     */
    public void removeCleanupAction(Runnable task) {
        cleanupActions.remove(task);
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        cleanupActions.forEach(task -> {
            try {
                task.run();
            } catch (Exception e) {
                LOG.log(Level.WARNING, "error in cleanup task", e);
            }
        });
    }
}
