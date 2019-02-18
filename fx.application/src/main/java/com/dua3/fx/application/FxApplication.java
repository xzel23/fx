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
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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
	private String applicationName = "";

	/** The application name. */
	private String versionString = "snapshot version";

	/** The contact email. */
	private String contactMail = "";

	/** The copyright text. */
	private String copyright = "";
	
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
	 * @param fxmlFile        the path to the FXML file to load, relative to the
	 *                        application class
	 */
	protected FxApplication(String fxmlFile) {
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
		final ChangeListener<Boolean> dirtyStateListener = (v,o,n) -> {
			updateApplicationTitle();
		};
		controller.currentDocumentProperty.addListener(
			(v,o,n) -> { 
				updateApplicationTitle();
				if (o!= null) {
					o.dirtyProperty.removeListener(dirtyStateListener);
				}
				if (n!=null) {
					n.dirtyProperty.addListener(dirtyStateListener);
				}
			});
		
        stage.setOnCloseRequest(e -> {
            e.consume();
            controller.closeApplication();
        });
        
		LOG.fine(() -> "done.");
	}

    protected void updateApplicationTitle() {
    	FxDocument document = controller.getCurrentDocument();
		String name = document.toString();
    	boolean dirty = document.isDirty();
    	
    	StringBuilder title = new StringBuilder();
    	title.append(applicationName);
    	
    	if (!name.isEmpty() || document.isDirty()) {
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

	public static void checkThread() {
		LangUtil.check(Platform.isFxApplicationThread(), "not on FX Application Thread");
	}

}
