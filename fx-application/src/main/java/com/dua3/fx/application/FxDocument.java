package com.dua3.fx.application;

import com.dua3.utility.io.IoUtil;
import com.dua3.utility.lang.LangUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Abstract class representing a document in the application.
 * This class provides fundamental operations such as getting the document's name,
 * location, and checking if it has been modified (dirty state).
 */
public abstract class FxDocument {
    /**
     * The void URI that represents "no document".
     */
    public static final URI VOID_URI = URI.create("");

    private final BooleanProperty dirtyProperty = new SimpleBooleanProperty(false);
    private final ObjectProperty<URI> locationProperty = new SimpleObjectProperty<>(VOID_URI);

    /**
     * Constructs a new FxDocument with the specified location.
     *
     * @param location the URI representing the document's location. This URI is used to set
     *                 the initial location property of the document.
     */
    protected FxDocument(URI location) {
        locationProperty().set(location);
    }

    /**
     * Retrieves the name of the document from its location.
     * If the document has no location, an empty string is returned.
     *
     * @return the name of the document or an empty string if the document lacks a location.
     */
    public String getName() {
        if (!hasLocation()) {
            return "";
        }

        return IoUtil.getFilename(getLocation().getPath());
    }

    /**
     * Checks whether the document has a valid location.
     * A document is considered to have a location if its location is not equal to VOID_URI.
     *
     * @return true if the document has a location, false otherwise.
     */
    public boolean hasLocation() {
        return !locationProperty().get().equals(VOID_URI);
    }

    /**
     * Retrieves the location of the document as a URI.
     *
     * @return the URI representing the location of the document.
     */
    public URI getLocation() {
        return locationProperty().get();
    }

    /**
     * Sets the location of the document.
     *
     * @param uri the URI representing the new location of the document.
     */
    public void setLocation(URI uri) {
        locationProperty().set(uri);
    }

    /**
     * Retrieves the location of the document as a filesystem Path.
     * This method converts the document's URI location into a Path object.
     *
     * @return the Path representing the location of the document.
     */
    public Path getPath() {
        return Paths.get(getLocation());
    }

    /**
     * Saves the current document to its specified location.
     * This method checks if the document has a location set and writes the document's
     * content to that location.
     *
     * @throws IOException if an I/O error occurs while writing the document.
     */
    public void save() throws IOException {
        LangUtil.check(hasLocation(), "location not set");
        write(locationProperty().get());
    }

    /**
     * Writes the document's content to the specified location represented by the given URI.
     * <p>
     * <strong>NOTE:</strong> Implementations should update the document URI on successful save.
     *
     * @param uri the location the document should be written to
     * @throws IOException on error
     * */
    @SuppressWarnings("RedundantThrows")
    protected abstract void write(URI uri) throws IOException;

    /**
     * Saves the current document to a new location specified by the provided URI
     * and updates the document's location to this new URI.
     *
     * @param uri the URI representing the new location to save the document to.
     * @throws IOException if an I/O error occurs while writing the document.
     */
    public void saveAs(URI uri) throws IOException {
        write(uri);
        setLocation(uri);
    }

    /**
     * Checks whether the document has been modified since it was last saved or opened.
     *
     * @return true if the document is in a "dirty" state (i.e., it has been modified), false otherwise.
     */
    public boolean isDirty() {
        return dirtyProperty().get();
    }

    @Override
    public String toString() {
        return getLocation().toString();
    }

    /**
     * Provides access to the BooleanProperty that represents the "dirty" state of the document.
     * This property can be observed or bound to determine whether the document has been modified
     * since it was last saved or opened.
     *
     * @return the BooleanProperty indicating the "dirty" state of the document.
     */
    public final BooleanProperty dirtyProperty() {
        return dirtyProperty;
    }

    /**
     * Provides access to the ObjectProperty representing the document's location.
     * This property can be observed or bound to track changes to the document's location.
     *
     * @return the ObjectProperty containing the URI that represents the document's location.
     */
    public final ObjectProperty<URI> locationProperty() {
        return locationProperty;
    }
}
