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

import com.dua3.cabe.annotations.Nullable;
import com.dua3.fx.controls.Dialogs;
import com.dua3.utility.fx.FxUtil;
import com.dua3.utility.i18n.I18N;
import com.dua3.utility.lang.LangUtil;
import com.dua3.utility.text.TextUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.desktop.AboutEvent;
import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenURIEvent;
import java.awt.desktop.PreferencesEvent;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public abstract class FxApplication<A extends FxApplication<A, C>, C extends FxController<A, C, ?>>
        extends Application {

    /**
     * Logger
     */
    protected static final Logger LOG = LogManager.getLogger(FxApplication.class);
    /**
     * The command line argument to set the logging level (i.e. "--log=FINE").
     */
    protected static final String ARG_LOG_LEVEL = "log";
    /**
     * Marker to indicate modified state in title.
     */
    protected static final String MARKER_MODIFIED = "*";

    // - constants -
    /**
     * Marker to indicate unmodified state in title.
     */
    protected static final String MARKER_UNMODIFIED = " ";
    /**
     * The user's home folder.
     */
    protected static final Path USER_HOME = Paths.get(System.getProperty("user.home"));
    /**
     * The name of the default bundle that is used if the application does not provide its own bundle.
     */
    private static final String DEFAULT_BUNDLE_NAME = "fxapp";
    /**
     * List of Resource cleanup tasks to run on application stop.
     */
    private final List<Runnable> cleanupActions = new ArrayList<>();
    /**
     * The resource bundle
     */
    protected final I18N i18n;
    protected final Path applicationDataDir = initApplicationDataDir();

    // - instance -
    /**
     * Preferences
     */
    protected Preferences preferences = null;
    /**
     * The controller instance.
     */
    protected C controller;

    // - UI -

    // - static initialization -

    // - Code -
    /**
     * The main stage.
     */
    private Stage mainStage;

    /**
     * Constructor.
     *
     * @param i18n the I18N instance for retrieving resources
     */
    protected FxApplication(I18N i18n) {
        this.i18n = i18n;
    }

    /**
     * Get the fxapplication resource bundle.
     *
     * @return the fxapplication resource bundle
     */
    public static ResourceBundle getFxAppBundle(Locale locale) {
        // load resource bundle
        LOG.debug("current locale is: {}", locale);
        ResourceBundle resources = ResourceBundle.getBundle(FxApplication.class.getPackageName() + "." + DEFAULT_BUNDLE_NAME, locale);
        if (!Objects.equals(resources.getLocale(), locale)) {
            LOG.warn("resource bundle uses fallback locale: {}", resources.getLocale());
        }
        return resources;
    }

    /**
     * Convert a given URI to text.
     *
     * @param uri the URI to convert
     * @return the text representation of the URI
     */
    public static String asText(@Nullable URI uri) {
        return uri == null ? "" : URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8);
    }

    /**
     * Get application main resource bundle.
     *
     * @return the resource bundle or {@code null}
     */
    protected ResourceBundle getResourceBundle() {
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
    public void start(Stage stage) {
        LOG.info("starting application");

        try {
            // store reference to stage
            this.mainStage = stage;

            // create the parent
            Parent root = createParentAndInitController();
            Objects.requireNonNull(controller, "controller was not initialized in createParentAndInitController()");
            controller.setApp((A) this);

            // create scene
            Scene scene = new Scene(root);

            URL css = getCss();
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            // setup stage
            stage.setTitle(i18n.get("fx.application.name"));
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

            LOG.debug("application started");
        } catch (Exception e) {
            LOG.fatal("error during application start", e);
        }
    }

    protected abstract Parent createParentAndInitController() throws Exception;

    protected void setController(C controller) {
        if (this.controller != null) {
            throw new IllegalStateException("controller already set");
        }
        LOG.debug("setting controller");
        this.controller = controller;
    }

    /**
     * Get application main CSS file.
     *
     * @return the path to the CSS file to load, relative to the
     * application class, or {@code null}
     */
    protected URL getCss() {
        return null;
    }

    protected void updateApplicationTitle() {
        StringBuilder title = new StringBuilder();
        title.append(i18n.get("fx.application.name"));

        FxDocument document = controller.getCurrentDocument();

        if (document != null) {
            String locStr = document.hasLocation() ?
                    asText(document.getLocation()) :
                    i18n.get("fx.application.text.untitled");
            boolean dirty = document.isDirty();

            if (!locStr.isEmpty() || document.isDirty()) {
                title.append(" - ");
            }

            String marker = dirty ? MARKER_MODIFIED : MARKER_UNMODIFIED;

            title.append(marker).append(locStr);
        }

        mainStage.setTitle(title.toString());
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        cleanupActions.forEach(task -> {
            try {
                task.run();
            } catch (Exception e) {
                LOG.warn("error in cleanup task", e);
            }
        });
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
                LOG.warn("could not update preferences", e);
            }
        }

        mainStage.close();

        mainStage = null; // make it garbage collectable
    }

    /**
     * Check whether a preferences object for this class has been created.
     *
     * @return true, if a Preferences object has been created
     */
    protected final boolean hasPreferences() {
        return preferences != null;
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
            LOG.debug("creating preferences for class {}", cls.getName());
            preferences = Preferences.userRoot().node(getClass().getName());
        }
        return preferences;
    }

    /**
     * Get the stage.
     *
     * @return the application's primary stage, or null if the application has been closed
     */
    public Stage getStage() {
        return mainStage;
    }

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
     *
     * @return the data folder for this application
     */
    public Path getDataDir() {
        return applicationDataDir;
    }

    /**
     * Get the controller instance.
     *
     * @return the controller
     */
    protected C getController() {
        return controller;
    }

    /**
     * Show error dialog.
     *
     * @param header the header
     * @param text   the text
     */
    public void showErrorDialog(String header, String text) {
        Dialogs.error(mainStage)
                .title("%s", i18n.get("fx.application.dialog.error.title"))
                .header("%s", header)
                .text("%s", text)
                .build()
                .showAndWait();
    }

    /**
     * If this application uses preferences, set the value. Otherwise, do nothing.
     *
     * @param key   the key
     * @param value the value
     * @return true if the key was set in the preferences, otherwise false
     */
    public boolean setPreferenceOptional(String key, String value) {
        if (hasPreferences()) {
            LOG.debug("setting preference '{}' -> '{}'", key, value);
            setPreference(key, value);
            return true;
        }
        LOG.debug("not setting preference '{}': preferences not initialized", key);
        return false;
    }

    /**
     * Set preference value
     *
     * @param key   the key
     * @param value the value
     */
    public void setPreference(String key, String value) {
        getPreferences().put(key, value);
    }

    /**
     * Get the preference value.
     *
     * @param key the preference key
     * @param def the default value
     * @return the value stored in the preferences for this key if present, or the default value
     */
    public String getPreference(String key, String def) {
        return hasPreferences() ? getPreferences().get(key, def) : def;
    }

    /**
     * Get file extension filter for all files ('*.*').
     *
     * @return file extension filter accepting all files
     */
    public FileChooser.ExtensionFilter getExtensionFilterAllFiles() {
        return new FileChooser.ExtensionFilter(i18n.get("fx.application.filter.all_files"), "*.*");
    }

    /**
     * Get the user's home directory.
     *
     * @return the user's home directory
     */
    public Path getUserHome() {
        return USER_HOME;
    }

    public void openFiles(OpenFilesEvent e) {
        e.getFiles().forEach(f -> Platform.runLater(() -> {
            if (f.exists()) {
                mainStage.show();
                controller.open(f.toURI());
            } else {
                LOG.warn("openFiles: ignoring non-existent file: {}", f);
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

    /**
     * Show this application's about dialog.
     */
    public void showAboutDialog() {
        showAboutDialog(null);
    }

    /**
     * Show this application's about dialog.
     *
     * @param css URL to the CSS data
     */
    protected void showAboutDialog(URL css) {
        Dialogs.about(mainStage)
                .title(i18n.format("fx.application.about.title.{0.name}", i18n.get("fx.application.name")))
                .name(i18n.get("fx.application.name"))
                .version(getVersion())
                .copyright(i18n.get("fx.application.about.copyright"))
                .graphic(LangUtil.getResourceURL(
                        getClass(),
                        i18n.get("fx.application.about.graphic"),
                        i18n.getLocale()))
                .mail(
                        i18n.get("fx.application.about.email"),
                        TextUtil.generateMailToLink(
                                i18n.get("fx.application.about.email"),
                                i18n.get("fx.application.name")
                                        + " "
                                        + getVersion()))
                .expandableContent(i18n.get("fx.application.about.detail"))
                .css(css)
                .build()
                .showAndWait();
    }

    /**
     * Get this application's version string.
     *
     * @return version string
     */
    public abstract String getVersion();

    public void handlePreferences(PreferencesEvent e) {
        Platform.runLater(this::showPreferencesDialog);
    }

    /**
     * Show this application's preferences dialog.
     */
    public abstract void showPreferencesDialog();

    /**
     * Add a resource cleanup action to run when the application stops.
     *
     * @param task the action to perform
     */
    public void addCleanupAction(Runnable task) {
        cleanupActions.add(task);
    }

    /**
     * Remove a resource cleanup action.
     *
     * @param task the action to remove
     */
    public void removeCleanupAction(Runnable task) {
        cleanupActions.remove(task);
    }
}
