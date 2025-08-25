package com.dua3.fx.application;

import com.dua3.utility.i18n.I18N;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.net.URI;
import java.util.Locale;

/**
 * Test class for FxApplication.
 * This class tests basic functionality of the FxApplication class.
 */
class FxApplicationTest extends FxTestBase {

    /**
     * A simple implementation of FxApplication for testing.
     */
    static class TestApplication extends FxApplication<TestApplication, TestController> {
        TestApplication() {
            super(I18N.getInstance(), null);
        }

        @Override
        protected Parent createParentAndInitController() {
            // Create a simple UI with a label
            VBox root = new VBox();
            Label label = new Label("Test Application");
            root.getChildren().add(label);

            // Create and set the controller
            TestController controller = new TestController();
            setController(controller);

            return root;
        }

        @Override
        public String getVersion() {
            return "1.0.0-TEST";
        }

        @Override
        public void showPreferencesDialog() {
            // No-op for testing
        }
    }

    /**
     * A simple implementation of FxController for testing.
     */
    static class TestController extends FxController<TestApplication, TestController, TestDocument> {
        @Override
        public java.util.List<TestDocument> dirtyDocuments() {
            return java.util.Collections.emptyList();
        }

        @Override
        protected javafx.stage.FileChooser.ExtensionFilter selectedOpenFilter() {
            return new javafx.stage.FileChooser.ExtensionFilter("All Files", "*.*");
        }

        @Override
        protected javafx.stage.FileChooser.ExtensionFilter selectedSaveFilter() {
            return new javafx.stage.FileChooser.ExtensionFilter("All Files", "*.*");
        }

        @Override
        protected void createDocument() {
            TestDocument doc = new TestDocument();
            setCurrentDocument(doc);
        }

        @Override
        protected TestDocument loadDocument(URI uri) {
            return new TestDocument(uri);
        }
    }

    /**
     * A simple implementation of FxDocument for testing.
     */
    static class TestDocument extends FxDocument {
        TestDocument() {
            super(VOID_URI);
        }

        TestDocument(URI location) {
            super(location);
        }

        @Override
        protected void write(URI uri) {
            // No-op for testing
            System.out.println("Writing document to: " + uri);
        }
    }

    /**
     * Test the static method getFxAppBundle.
     */
    @Test
    void testGetFxAppBundle() {
        // Test that we can get the resource bundle
        java.util.ResourceBundle bundle = FxApplication.getFxAppBundle(Locale.ENGLISH);
        Assertions.assertNotNull(bundle, "Resource bundle should not be null");
    }

    /**
     * Test the asText method.
     */
    @Test
    void testAsText() {
        // Test with a null URI
        String nullText = FxApplication.asText(null);
        Assertions.assertEquals("", nullText, "asText should return empty string for null URI");

        // Test with a valid URI
        URI uri = URI.create("file:///test/path/file.txt");
        String text = FxApplication.asText(uri);
        Assertions.assertEquals("file:///test/path/file.txt", text, "asText should return the URI as a string");
    }
}
