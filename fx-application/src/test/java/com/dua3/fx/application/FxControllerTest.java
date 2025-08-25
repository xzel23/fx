package com.dua3.fx.application;

import com.dua3.utility.i18n.I18N;
import javafx.stage.FileChooser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * Test class for FxController.
 * This class tests basic functionality of the FxController class.
 */
class FxControllerTest extends FxTestBase {

    /**
     * Test the getCurrentDocument and setCurrentDocument methods.
     */
    @Test
    void testGetAndSetCurrentDocument() {
        TestController controller = new TestController();
        TestApplication app = new TestApplication();

        // Set the app instance for the controller
        controller.setApp(app);

        // Initially, there should be no current document
        Assertions.assertFalse(controller.getCurrentDocument().isPresent(), "Initially, there should be no current document");

        // Create a document and set it as current
        TestDocument doc = new TestDocument(URI.create("file:///test/document.txt"));
        controller.setCurrentDocument(doc);

        // Check that the current document is set correctly
        Assertions.assertTrue(controller.getCurrentDocument().isPresent(), "After setting, there should be a current document");
        Assertions.assertEquals(doc, controller.getCurrentDocument().get(), "The current document should be the one we set");
    }

    /**
     * Test the createDocument method.
     */
    @Test
    void testCreateDocument() {
        TestController controller = new TestController();
        TestApplication app = new TestApplication();

        // Set the app instance for the controller
        controller.setApp(app);

        // Create a new document
        controller.createDocument();

        // Check that a document was created and set as current
        Assertions.assertTrue(controller.getCurrentDocument().isPresent(), "After creating, there should be a current document");
        Assertions.assertEquals(FxDocument.VOID_URI, controller.getCurrentDocument().get().getLocation(), "The new document should have VOID_URI as location");
    }

    /**
     * Test the loadDocument method.
     */
    @Test
    void testLoadDocument() {
        TestController controller = new TestController();
        TestApplication app = new TestApplication();

        // Set the app instance for the controller
        controller.setApp(app);

        URI testUri = URI.create("file:///test/document.txt");

        // Load a document
        TestDocument doc = controller.loadDocument(testUri);

        // Check that the document was loaded correctly
        Assertions.assertEquals(testUri, doc.getLocation(), "The loaded document should have the specified URI as location");
        Assertions.assertEquals(doc, controller.getCurrentTestDocument(), "The loaded document should be set as the current document");
    }

    /**
     * Test the dirtyDocuments method.
     * <p>
     * FIXME: This test is limited because we can't easily add documents to the controller's list.
     * In a real application, we would need a more comprehensive test that adds documents to the list
     * and checks that dirty documents are correctly identified.
     */
    @Test
    void testDirtyDocuments() {
        TestController controller = new TestController();
        TestApplication app = new TestApplication();

        // Set the app instance for the controller
        controller.setApp(app);

        // Initially, there should be no dirty documents
        List<? extends TestDocument> dirtyDocs = controller.dirtyDocuments();
        Assertions.assertTrue(dirtyDocs.isEmpty(), "Initially, there should be no dirty documents");
    }

    /**
     * A simple implementation of FxDocument for testing.
     */
    static class TestDocument extends FxDocument {
        private boolean writeWasCalled = false;
        private boolean isDirty = false;

        TestDocument() {
            super(VOID_URI);
        }

        TestDocument(URI location) {
            super(location);
        }

        @Override
        protected void write(URI uri) {
            // Just mark that write was called
            writeWasCalled = true;
            System.out.println("Writing document to: " + uri);
        }

        @Override
        public boolean isDirty() {
            return isDirty;
        }

        void setDirty(boolean dirty) {
            isDirty = dirty;
            dirtyProperty().set(dirty);
        }

        boolean wasWriteCalled() {
            return writeWasCalled;
        }
    }

    /**
     * A simple implementation of FxApplication for testing.
     */
    static class TestApplication extends FxApplication<TestApplication, TestController> {
        TestApplication() {
            super(I18N.getInstance(), null);
        }

        @Override
        protected javafx.scene.Parent createParentAndInitController() {
            // This method won't be called in our tests
            return null;
        }

        @Override
        public FileChooser.ExtensionFilter getExtensionFilterAllFiles() {
            return new FileChooser.ExtensionFilter("All Files", "*.*");
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
        private final List<TestDocument> documents = Collections.emptyList();
        private TestDocument currentDocument;

        @Override
        public List<TestDocument> dirtyDocuments() {
            return documents.stream().filter(TestDocument::isDirty).toList();
        }

        @Override
        protected FileChooser.ExtensionFilter selectedOpenFilter() {
            return new FileChooser.ExtensionFilter("All Files", "*.*");
        }

        @Override
        protected FileChooser.ExtensionFilter selectedSaveFilter() {
            return new FileChooser.ExtensionFilter("All Files", "*.*");
        }

        @Override
        protected void createDocument() {
            TestDocument doc = new TestDocument();
            setCurrentDocument(doc);
            currentDocument = doc;
        }

        @Override
        protected TestDocument loadDocument(URI uri) {
            TestDocument doc = new TestDocument(uri);
            currentDocument = doc;
            return doc;
        }

        TestDocument getCurrentTestDocument() {
            return currentDocument;
        }
    }
}
