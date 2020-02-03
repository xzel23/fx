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

import com.dua3.utility.lang.LangUtil;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FxApplication<A extends FxApplication<A, C>, C extends FxController<A, C>> extends Application {

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

    // - static -

    /**
     * Logger
     */
    protected static final Logger LOG = Logger.getLogger(FxApplication.class.getName());

    /**
     * Preferences
     */
    private Preferences preferences = null;

    // - instance -

    /**
     * The application name.
     */
    private String applicationName = "";

    /**
     * The application name.
     */
    private String versionString = "snapshot version";

    /**
     * The contact email.
     */
    private String contactMail = "";

    /**
     * The copyright text.
     */
    private String copyright = "";

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
     * Constructor.
     */
    protected FxApplication() {
    }

    /**
     * Get application main FXML file.
     * @return the path to the FXML file to load, relative to the
     *                 application class
     */
    protected abstract URL getFxml();

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
        FXMLLoader loader = new FXMLLoader(fxml);

        Parent root = loader.load();

        // set controller
        LOG.log(Level.FINER, () -> "setting FXML controller ...");
        this.controller = Objects.requireNonNull(loader.getController(),
                "controller is null; set fx:controller in root element of FXML (" + fxml + ")");
        this.controller.setApp((A) this);

        // create scene
        Scene scene = new Scene(root);

        URL css = getCss();
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        // setup stage
        stage.setTitle(applicationName);
        stage.setScene(scene);
        stage.show();

        // automatically update title on document change
        final ChangeListener<Boolean> dirtyStateListener = (v, o, n) -> updateApplicationTitle();

        final ChangeListener<URI> documentLocationListener = (v, o, n) -> updateApplicationTitle();

        controller.currentDocumentProperty.addListener(
                (v, o, n) -> {
                    updateApplicationTitle();
                    if (o != null) {
                        o.dirtyProperty.removeListener(dirtyStateListener);
                        o.locationProperty.removeListener(documentLocationListener);
                    }
                    if (n != null) {
                        n.dirtyProperty.addListener(dirtyStateListener);
                        n.locationProperty.addListener(documentLocationListener);
                    }
                });

        stage.setOnCloseRequest(e -> {
            e.consume();
            controller.closeApplication();
        });

        LOG.fine(() -> "done.");
    }

    /**
     * Pattern for parsing the log configuration string.
     * Example:
     * <code>
     *     --log=INFO,com.dua3:FINE,com.sun:WARNING
     * </code>
     */
    private static final Pattern patternLogCfg = Pattern.compile("(?:(?<PACKAGE>(?:\\w|\\.)+):)?(?<LEVEL>[A-Z0-9]+)(?:,|$)");

    private final Map<String,Level> logLevel = new ConcurrentHashMap<>();
    private Level globalLogLevel = Level.INFO;
    
    private void setLogLevel(String logStr) {
        // determine the global and minumum log levels and store mapping package -> level
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
        title.append(applicationName);

        FxDocument document = controller.getCurrentDocument();

        if (document != null) {
            String locStr = document.getLocationString();
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
    public void close() {
        if (hasPreferences()) {
            try {
                getPreferences().flush();
            } catch (BackingStoreException e) {
                LOG.log(Level.WARNING, "could not update preferences", e);
            }
        }

        mainStage.close();
    }

    /**
     * Get the stage.
     *
     * @return the application's primary stage
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
    public Preferences getPreferences() {
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
    protected boolean hasPreferences() {
        return preferences != null;
    }

    /**
     * Get application name.
     *
     * @return the name of the application
     */
    public String getApplicationName() {
        return !applicationName.isEmpty() ? applicationName : getClass().getSimpleName();
    }

    protected void setContactMail(String value) {
        this.contactMail = value;
    }

    protected void setCopyright(String value) {
        this.copyright = value;
    }

    protected void setVersionString(String value) {
        this.versionString = value;
    }

    protected void setApplicationName(String value) {
        this.applicationName = value;
    }

    public String getVersionString() {
        return versionString;
    }

    public String getContactMail() {
        return contactMail;
    }

    public String getCopyright() {
        return copyright;
    }

    private final File APPLICATION_DATA_DIR = initApplicationDataDir();

    private File initApplicationDataDir() {
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
                return dir.toFile();
            }

            // then check for macos
            Path home = Paths.get(System.getProperty("user.home"));
            Path macosBase = home.resolve(Paths.get("Library", "Application Support"));
            if (Files.isDirectory(macosBase) && Files.isWritable(macosBase)) {
                Path dir = macosBase.resolve(dirName);
                Files.createDirectories(dir);
                return dir.toFile();
            }

            // as last resort, use a dot file in user's home directory
            Path dir = home.resolve(dirName.replaceAll(" ", "_").toLowerCase(Locale.ROOT));
            Files.createDirectories(dir);
            return dir.toFile();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public File getDataDir() {
        return APPLICATION_DATA_DIR;
    }

    protected C getController() {
        return controller;
    }

    /**
     * Get graphic to display in about dialog.
     * @return the graphic to show
     */
    public URL getAboutGraphic() {
        return null;
    }
}
