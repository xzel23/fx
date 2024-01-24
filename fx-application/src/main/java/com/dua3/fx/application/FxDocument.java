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
import java.util.Objects;

public abstract class FxDocument {
    /**
     * The void URI that represents "no document".
     */
    public static final URI VOID_URI = URI.create("");

    protected final BooleanProperty dirtyProperty = new SimpleBooleanProperty(false);
    protected final ObjectProperty<URI> locationProperty = new SimpleObjectProperty<>(VOID_URI);

    protected FxDocument(URI location) {
        this.locationProperty.set(location);
    }

    public String getName() {
        if (!hasLocation()) {
            return "";
        }

        return IoUtil.getFilename(getLocation().getPath());
    }

    public boolean hasLocation() {
        return !locationProperty.get().equals(VOID_URI);
    }

    public URI getLocation() {
        return locationProperty.get();
    }

    public void setLocation(URI uri) {
        this.locationProperty.set(uri);
    }

    public Path getPath() {
        return Paths.get(getLocation());
    }

    public void save() throws IOException {
        LangUtil.check(hasLocation(), "location not set");
        write(locationProperty.get());
    }

    protected abstract void write(URI uri) throws IOException;

    public void saveAs(URI uri) throws IOException {
        write(uri);
        setLocation(uri);
    }

    public boolean isDirty() {
        return dirtyProperty.get();
    }

    @Override
    public String toString() {
        return getLocation().toString();
    }
}
