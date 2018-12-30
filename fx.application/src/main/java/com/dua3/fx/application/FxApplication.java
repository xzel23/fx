package com.dua3.fx.application;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dua3.utility.lang.LangUtil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
		
		LOG.fine(() -> "done.");
	}

    protected void updateApplicationTitle() {
    	URI doc = controller.getDocument();
    	String name = controller.hasDocument() ? doc.getPath() : "";
    	boolean dirty = controller.isDirty();
    	
    	StringBuilder title = new StringBuilder();
    	title.append(applicationName);
    	
    	if (!name.isEmpty()) {
    		title.append(" - ");
    	}
        
    	String marker = dirty ? MARKER_MODIFIED : MARKER_UNMODIFIED;
        
    	title.append(marker).append(name).append(marker);
    	
        mainStage.setTitle(title.toString());
    }

	public void close() {
		mainStage.fireEvent(new WindowEvent(mainStage, javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST));
	}
	
	/**
	 * Get the stage.
	 * @return
	 *  the application's primary stage
	 */
	public Stage getStage() {
		return mainStage;
	}
}
