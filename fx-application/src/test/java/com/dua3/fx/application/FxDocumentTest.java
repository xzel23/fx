package com.dua3.fx.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;

/**
 * Test class for FxDocument.
 * This class tests basic functionality of the FxDocument class.
 */
class FxDocumentTest extends FxTestBase {

    /**
     * Test the constructor and location property.
     */
    @Test
    void testConstructorAndLocation() {
        // Test with VOID_URI
        TestDocument doc1 = new TestDocument();
        Assertions.assertEquals(FxDocument.VOID_URI, doc1.getLocation(), "Location should be VOID_URI");
        Assertions.assertFalse(doc1.hasLocation(), "Document should not have a location");

        // Test with a specific URI
        URI testUri = URI.create("file:///test/document.txt");
        TestDocument doc2 = new TestDocument(testUri);
        Assertions.assertEquals(testUri, doc2.getLocation(), "Location should match the provided URI");
        Assertions.assertTrue(doc2.hasLocation(), "Document should have a location");
    }

    /**
     * Test the getName method.
     */
    @Test
    void testGetName() {
        // Test with VOID_URI
        TestDocument doc1 = new TestDocument();
        Assertions.assertEquals("", doc1.getName(), "Name should be empty for VOID_URI");

        // Test with a specific URI
        URI testUri = URI.create("file:///test/document.txt");
        TestDocument doc2 = new TestDocument(testUri);
        Assertions.assertEquals("document.txt", doc2.getName(), "Name should be the filename part of the URI");
    }

    /**
     * Test the dirty property.
     */
    @Test
    void testDirtyProperty() {
        TestDocument doc = new TestDocument();

        // Initially, document should not be dirty
        Assertions.assertFalse(doc.isDirty(), "Document should not be dirty initially");

        // Set dirty property to true
        doc.dirtyProperty().set(true);
        Assertions.assertTrue(doc.isDirty(), "Document should be dirty after setting property");

        // Set dirty property back to false
        doc.dirtyProperty().set(false);
        Assertions.assertFalse(doc.isDirty(), "Document should not be dirty after clearing property");
    }

    /**
     * Test the save and saveAs methods.
     */
    @Test
    void testSaveAndSaveAs() throws IOException {
        TestDocument doc = new TestDocument();
        URI testUri = URI.create("file:///test/document.txt");

        // Test saveAs
        doc.saveAs(testUri);
        Assertions.assertTrue(doc.wasWriteCalled(), "Write method should have been called");
        Assertions.assertEquals(testUri, doc.getLocation(), "Location should be updated after saveAs");

        // Reset the write flag
        TestDocument doc2 = new TestDocument(testUri);

        // Test save
        doc2.save();
        Assertions.assertTrue(doc2.wasWriteCalled(), "Write method should have been called");
    }

    /**
     * Test that save throws an exception when location is not set.
     */
    @Test
    void testSaveWithoutLocation() {
        TestDocument doc = new TestDocument(); // No location set

        // Attempting to save without a location should throw an exception
        Assertions.assertThrows(IllegalStateException.class, doc::save,
                "Save should throw an exception when location is not set");
    }

    /**
     * A simple implementation of FxDocument for testing.
     */
    static class TestDocument extends FxDocument {
        private boolean writeWasCalled = false;

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

        boolean wasWriteCalled() {
            return writeWasCalled;
        }
    }
}