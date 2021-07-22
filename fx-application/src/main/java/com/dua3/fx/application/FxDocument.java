package com.dua3.fx.application;

import com.dua3.utility.io.IOUtil;
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
	/** The void URI that represents "no document". */
	public static final URI VOID_URI = URI.create("");
	
	protected final BooleanProperty dirtyProperty = new SimpleBooleanProperty(false);
	protected final ObjectProperty<URI> locationProperty = new SimpleObjectProperty<>(VOID_URI);
	
	protected FxDocument(URI location) {
		this.locationProperty.set(Objects.requireNonNull(location));
	}

	public URI getLocation() {
		return locationProperty.get();
	}
	
	public void setLocation(URI uri) {
		this.locationProperty.set(uri);
	}

	public boolean hasLocation() {
		return !locationProperty.get().equals(VOID_URI);
	}
	
	public String getName() {
		if (!hasLocation()) {
			return "";
		}

		return IOUtil.getFilename(getLocation().getPath());
	}

	public Path getPath() {
		return Paths.get(getLocation());
	}
	
	public void save() throws IOException {
		LangUtil.check(hasLocation());
		write(locationProperty.get());
	}
	
	public void saveAs(URI uri) throws IOException {
		write(uri);
		setLocation(uri);
	}
	
	protected abstract void write(URI uri) throws IOException;
	
	public boolean isDirty() {
		return dirtyProperty.get();
	}

	@Override
	public String toString() {
		return getLocation().toString();
	}
}
