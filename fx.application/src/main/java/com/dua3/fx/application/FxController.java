package com.dua3.fx.application;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Logger;

import com.dua3.utility.lang.LangUtil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;

public abstract class FxController<A extends FxApplication<A, C>, C extends FxController<A, C>> {

	// - static -

	/** Logger */
	protected static final Logger LOG = Logger.getLogger(FxController.class.getSimpleName());

	/** The application instance. */
	private A app;

	private static final URI VOID_URI = URI.create("");
	
	/** The URI of the currently opened document. */
	protected ObjectProperty<URI> documentProperty = new SimpleObjectProperty<URI>(VOID_URI);
	
	/** The URI of the currently opened document. */
	protected BooleanProperty dirtyProperty = new SimpleBooleanProperty(false);
	
	/**
	 * The Default constructor. Just declared here to reduce visibility.
	 */
	protected FxController() {
	}

	/**
	 * Set application instance.
	 * 
	 * This method must be called exactly once!
	 * 
	 * @param app
	 *  the application instance
	 */
	public void setApp(A app) {
		LangUtil.check(this.app==null, "app instance was already set");
		this.app = Objects.requireNonNull(app, "app instance must not be null");
		init(app);
	}

	protected void init(A app) {
		// nop
	}

	/**
	 * Get the App instance.
	 * 
	 * @return 
	 *  the App instance
	 * @throws IllegalStateException
	 *  if called before the App instance was set
	 */
	public A getApp() {
		if ( app == null ) {
			throw new IllegalStateException("App instance was not yet set");
		}
		return app;
	}
	/**
	 * Request application close as if the close-window-button was clicked.
	 * Called from FXML.
	 */
	@FXML
	public void closeApplication() {
		app.close();
	}
	
	/**
	 * Get current document.
	 * 
	 * @return
	 *  URI of the current document
	 */
	public URI getDocument() {
		return documentProperty.get();
	}
	
	/**
	 * Set current document.
	 * 
	 * @param uri
	 *  URI of the document
	 */
	protected void setDocument(URI uri) {
		documentProperty.set(uri);
	}
	
	/**
	 * Set current document.
	 * 
	 * @param url
	 *  URL of the document
	 */
	protected void setDocument(URL url) {
		try {
			documentProperty.set(url.toURI());
		} catch (URISyntaxException e) {
			documentProperty.set(VOID_URI);
			LOG.warning("could not set document URL");
		}
	}
	
	protected void clearDocument() {
		documentProperty.set(VOID_URI);
	}
	
	/**
	 * Get document property.
	 * 
	 * @return
	 *  read only document property
	 */
	public ReadOnlyObjectProperty<URI> documentProperty() {
		return documentProperty;
	}
	
	/**
	 * Get current dirty state.
	 * 
	 * @return
	 *  dirty state of the current document
	 */
	public boolean isDirty() {
		return dirtyProperty.get();
	}
	
	/**
	 * Set current document's dirty state.
	 * 
	 * @param flag
	 *  dirty state of the document
	 */
	protected void setDirty(boolean flag) {
		dirtyProperty.set(flag);
	}
	
	/**
	 * Get dirty property.
	 * 
	 * @return
	 *  read only dirty property
	 */
	public ReadOnlyBooleanProperty dirtyProperty() {
		return dirtyProperty;
	}
	
	/** 
	 * Test if document is set.
	 * @return
	 *  true, if document is set
	 */
	public boolean hasDocument() {
		return documentProperty.get() != VOID_URI;
	}
}
