package com.dua3.fx.application;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.dua3.utility.lang.LangUtil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class FxApplication<A extends FxApplication<A, C>, C extends FxController<A, C>> extends Application {

	// - constants -

	/** The command line argument to set the logging level (i.e. "--log=FINE"). */
	public static final String ARG_LOG_LEVEL = "log";

	/** Marker to indicate modified state in title. */
    private static final String MARKER_MODIFIED = "*";
	/** Marker to indicate unmodified state in title. */
    private static final String MARKER_UNMODIFIED = " ";

	// - static -

	/** Logger */
	protected static final Logger LOG = Logger.getLogger(FxApplication.class.getSimpleName());

	/** Preferences */
	private Preferences preferences = null;
	
	// - instance -

	/** The application name. */
	private final String applicationName;

	/** Path to FXML file. */
	private final String fxmlFile;

	/** The controller instance. */
	private C controller;

	/** The main stage. */
	private Stage mainStage;
	
	// - UI -

	// - static initialization -

	// - Code -

	/**
	 * Constructor.
	 * 
	 * @param applicationName the name of the application to show in title bar
	 * @param fxmlFile        the path to the FXML file to load, relative to the
	 *                        application class
	 */
	protected FxApplication(String applicationName, String fxmlFile) {
		this.applicationName = Objects.requireNonNull(applicationName);
		this.fxmlFile = Objects.requireNonNull(fxmlFile);
	}

	/**
	 * Get named parameter value.
	 * 
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
		LOG.log(Level.FINE, () -> "starting application ...");

		// store reference to stage
		this.mainStage = stage;

		// handle program arguments
		getParameterValue("log").ifPresent(level -> LangUtil.setLogLevel(Level.parse(level)));

		// create a loader and load FXML
		LOG.log(Level.FINE, () -> "loading FXML ...");
		URL fxmlUrl = LangUtil.getResourceURL(getClass(), fxmlFile);

		LOG.log(Level.FINER, () -> "FXML URL: " + fxmlUrl);
		FXMLLoader loader = new FXMLLoader(fxmlUrl);

		Parent root = loader.load();

		// set controller
		LOG.log(Level.FINER, () -> "setting FXML controller ...");
		this.controller = Objects.requireNonNull(loader.getController(),
				"controller is null; set fx:controller in root element of FXML (" + fxmlFile + ")");
		this.controller.setApp((A) this);

		// create scene
		Scene scene = new Scene(root);

		// setup stage
		stage.setTitle(applicationName);
		stage.setScene(scene);
		stage.show();

		// automatically update title on document change
		controller.documentProperty().addListener((v,o,n) -> updateApplicationTitle());
		controller.dirtyProperty().addListener((v,o,n) -> updateApplicationTitle());
		
        stage.setOnCloseRequest(e -> {
            e.consume();
            controller.closeApplication();
        });
        
		LOG.fine(() -> "done.");
	}

    protected void updateApplicationTitle() {
    	String name = controller.getDisplayName(controller.getDocument());
    	boolean dirty = controller.isDirty();
    	
    	StringBuilder title = new StringBuilder();
    	title.append(applicationName);
    	
    	if (!name.isEmpty() || controller.isDirty()) {
    		title.append(" - ");
    	}
        
    	String marker = dirty ? MARKER_MODIFIED : MARKER_UNMODIFIED;
        
    	title.append(marker).append(name);
    	
        mainStage.setTitle(title.toString());
    }

    /**
     * Close the application.
     * 
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
	 * @return
	 *  the application's primary stage
	 */
	public Stage getStage() {
		return mainStage;
	}
	
	/**
	 * Get the Preferences instance for this application.
	 * 
	 * The Preferences instance will be created on demand if it doesn't exist yet,
	 * 
	 * @return
	 *  the preferences object for this application
	 */
	public Preferences getPreferences() {
		if (!hasPreferences()) {
			Class<?> cls = getClass();
			LOG.fine("creating preferences for class "+cls.getName());
			preferences = Preferences.userRoot().node(getClass().getName());
		}
		return preferences;
	}

	/**
	 * Check whether a preferences object for this class has been created.
	 * @return true, if a Preferences object has been created
	 */
	protected boolean hasPreferences() {
		return preferences != null;
	}
	
}
