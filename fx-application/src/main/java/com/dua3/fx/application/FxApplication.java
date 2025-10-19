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

import com.dua3.utility.application.LicenseData;
import com.dua3.utility.fx.controls.AboutDialogBuilder;
import com.dua3.utility.fx.controls.Dialogs;
import com.dua3.utility.i18n.I18N;
import com.dua3.utility.io.IoUtil;
import com.dua3.utility.lang.LangUtil;
import com.dua3.utility.lang.Platform;
import com.dua3.utility.text.TextUtil;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

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

/**
 * This abstract class represents a JavaFX application. It extends the javafx.application.Application class.
 * <p>
 * This class is meant to be extended by concrete implementations of JavaFX applications, and provides a basic structure and functionality for them.
 * The concrete implementation should provide its own resource bundle and implement the createParentAndInitController method to define the UI layout.
 *
 * @param <A> the application class
 * @param <C> the controller class
 */
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
     * The name of the default bundle that is used if the application does not provide its own bundle.
     */
    private static final String DEFAULT_BUNDLE_NAME = "fxapp";
    /**
     * Represents the key for retrieving the application name from the resource bundle.
     */
    private static final String FX_APPLICATION_NAME = "fx.application.name";
    /**
     * List of Resource cleanup tasks to run on application stop.
     */
    private final List<Runnable> cleanupActions = new ArrayList<>();
    /**
     * The resource bundle
     */
    protected final I18N i18n;
    /**
     * The directory containing application data.
     */
    protected final Path dataDir = initApplicationDataDir();
    /**
     * The current license used for the application.
     */
    private @Nullable LicenseData license;

    // - instance -
    /**
     * Preferences
     */
    protected @Nullable Preferences preferences;
    /**
     * The controller instance.
     */
    protected @Nullable C controller;

    // - UI -

    // - static initialization -

    // - Code -
    /**
     * The main stage.
     */
    private @Nullable Stage mainStage;

    /**
     * Constructor.
     *
     * @param i18n the I18N instance for retrieving resources
     * @param license the license, if software is licensed
     */
    protected FxApplication(I18N i18n, @Nullable LicenseData license) {
        this.i18n = i18n;
        this.i18n.mergeBundle(FxApplication.class.getPackageName() + ".application", i18n.getLocale());
        this.license = license;
    }

    /**
     * Retrieves the license associated with this application, if available.
     *
     * @return an {@link Optional} containing the license if it has been set, or an empty {@link Optional} otherwise
     */
    protected Optional<LicenseData> getLicense() {
        return Optional.ofNullable(license);
    }

    /**
     * Sets the license for the application.
     *
     * @param license the {@link LicenseData} instance to associate with the application
     */
    protected void setLicense(LicenseData license) {
        this.license = license;
    }

    /**
     * Get the fxapplication resource bundle.
     *
     * @param locale the {@link Locale} for the bundle
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
    protected Optional<ResourceBundle> getResourceBundle() {
        return Optional.empty();
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
    public void start(Stage primaryStage) {
        LOG.info("starting application");

        try {
            // store reference to stage
            this.mainStage = primaryStage;

            // create the parent
            Parent root = createParentAndInitController();
            Objects.requireNonNull(controller, "controller was not initialized in createParentAndInitController()");
            controller.setApp((A) this);

            // create scene
            Scene scene = new Scene(root);

            // load CSS
            getCss().ifPresent(css -> scene.getStylesheets().add(css.toExternalForm()));

            // setup stage
            primaryStage.setTitle(i18n.get(FX_APPLICATION_NAME));
            primaryStage.setScene(scene);

            // automatically update title on document change
            final ChangeListener<Boolean> dirtyStateListener = (v, o, n) -> updateApplicationTitle();

            final ChangeListener<@Nullable URI> locationListener = (v, o, n) -> updateApplicationTitle();

            controller.currentDocumentProperty.addListener(
                    (ObservableValue<? extends FxDocument> observable, @Nullable FxDocument o, @Nullable FxDocument n) -> {
                        updateApplicationTitle();
                        if (o != null) {
                            o.dirtyProperty().removeListener(dirtyStateListener);
                            o.locationProperty().removeListener(locationListener);
                        }
                        if (n != null) {
                            n.dirtyProperty().addListener(dirtyStateListener);
                            n.locationProperty().addListener(locationListener);
                        }
                    });

            primaryStage.setOnCloseRequest(e -> {
                e.consume();
                controller.closeApplicationWindow();
            });

            primaryStage.show();

            LOG.debug("application started");
        } catch (Exception e) {
            LOG.fatal("error during application start", e);
        }
    }

    /**
     * Creates the parent node for the User Interface and initializes the controller.
     * This method must be implemented by subclasses.
     *
     * @return the parent node of the UI
     * @throws Exception if an error occurs during the creation or initialization process
     */
    protected abstract Parent createParentAndInitController() throws Exception;

    /**
     * Sets the controller for this application. The controller is responsible for handling the logic and
     * actions of the user interface.
     *
     * @param controller the controller to set
     * @throws IllegalStateException if the controller has already been set
     */
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
     * @return an Optional holding the path to the CSS file to load, relative to the application class
     */
    protected Optional<URL> getCss() {
        return Optional.empty();
    }

    /**
     * Update the application title based on the current document.
     * The application title is constructed using the following logic:
     * 1. The title starts with the localized application name retrieved from a resource bundle.
     * 2. If there is a current document, the title is appended with the document's location or a localized "untitled" text.
     * 3. If the document is dirty (modified), a dirty marker is appended to the title.
     * Finally, the updated title is set as the title of the main stage.
     */
    protected void updateApplicationTitle() {
        StringBuilder title = new StringBuilder();
        title.append(i18n.get(FX_APPLICATION_NAME));

        FxDocument document = getController().getCurrentDocument().orElse(null);

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

        if (mainStage != null) {
            mainStage.setTitle(title.toString());
        }
    }

    /**
     * Stops the application.
     * This method is called when the application is being stopped.
     * It executes any cleanup actions that have been registered, by running each action in the cleanupActions list.
     * If an exception occurs during the execution of a cleanup action, a warning message is logged.
     *
     * @throws Exception if an exception occurs during the stop process
     */
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

        if (mainStage != null) {
            mainStage.close();
        }

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
     * The Preferences instance will be created on demand if it doesn't exist yet.
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
        LangUtil.check(mainStage != null, "no main stage");
        return mainStage;
    }

    private Path initApplicationDataDir() {
        try {
            String dirName = getClass().getName();
            Path home = IoUtil.getUserHome();

            switch (Platform.currentPlatform()) {
                case WINDOWS -> {
                    // try to determine location by evaluating standard windows settings
                    //noinspection CallToSystemGetenv
                    String appData = System.getenv("LOCALAPPDATA");
                    if (appData == null) {
                        //noinspection CallToSystemGetenv
                        appData = System.getenv("APPLICATION_DATA_DIR");
                    }
                    if (appData != null) {
                        Path dir = Paths.get(appData).resolve(dirName);
                        Files.createDirectories(dir);
                        return dir;
                    }
                }
                case MACOS -> {
                    // macOS
                    Path macosBase = home.resolve(Paths.get("Library", "Application Support"));
                    if (Files.isDirectory(macosBase) && Files.isWritable(macosBase)) {
                        Path dir = macosBase.resolve(dirName);
                        Files.createDirectories(dir);
                        return dir;
                    }
                }
                default -> {
                    // handled below switch
                }
            }

            // in case standard locations are not available, use a dot file in user's home directory
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
        return dataDir;
    }

    /**
     * Get the controller instance.
     *
     * @return the controller
     */
    protected C getController() {
        LangUtil.check(controller != null, "controller not set");
        return controller;
    }

    /**
     * Show error dialog.
     *
     * @param header the header
     * @param text   the text
     */
    public void showErrorDialog(String header, String text) {
        Dialogs.alert(mainStage, AlertType.ERROR)
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
    protected void showAboutDialog(@Nullable URL css) {
        AboutDialogBuilder aboutDialogBuilder = Dialogs.about(mainStage)
                .title(i18n.format("fx.application.about.title.{0.name}", i18n.get(FX_APPLICATION_NAME)))
                .applicationName(i18n.get(FX_APPLICATION_NAME))
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
                                i18n.get(FX_APPLICATION_NAME)
                                        + " "
                                        + getVersion()))
                .expandableContent(i18n.get("fx.application.about.detail"));

        getLicense().ifPresent(license -> {
            aboutDialogBuilder.license(license);
        });

        if (css != null) {
            aboutDialogBuilder.css(css);
        }

        aboutDialogBuilder.build().showAndWait();
    }

    /**
     * Get this application's version string.
     *
     * @return version string
     */
    public abstract String getVersion();

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

    /**
     * Get the user home path.
     *
     * @return the user home path
     */
    public static Path getUserHome() {
        return IoUtil.getUserHome();
    }
}
