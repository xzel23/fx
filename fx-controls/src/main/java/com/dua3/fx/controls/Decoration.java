package com.dua3.fx.controls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public final class Decoration {

    private static final String DECORATION_LIST = Decoration.class.getName() + ".decoration_list";
    private static final String OWNER = Decoration.class.getName() + ":owner";
    private static final String POSITION = Decoration.class.getName() + ":position";
    private static final String PREFIX = Decoration.class.getName() + ":";

    private Decoration() {
    }

    public static ObservableList<Decoration> getDecorations(Node node) {
        @SuppressWarnings("unchecked")
        ObservableList<Decoration> decorations = (ObservableList<Decoration>) node.getProperties().get(DECORATION_LIST);
        if (decorations == null) {
            decorations = FXCollections.observableArrayList();
            node.getProperties().put(DECORATION_LIST, decorations);
        }
        return decorations;
    }

    public static void addDecoration(Node node, Pos position, Node decoration, String id) {
        DecorationPane decorationPane = DecorationPane.getDecorationPane(node);

        decoration.getProperties().put(OWNER, node);
        decoration.getProperties().put(POSITION, position);
        updateDecorationPosition(decoration);
        Object oldDecoration = node.getProperties().put(getDecorationId(id), decoration);

        if (oldDecoration != null) {
            decorationPane.removeDecoration(oldDecoration);
        }

        decorationPane.getChildren().add(decoration);
    }

    static void updateDecorationPosition(Node decoration) {
        Node node = (Node) decoration.getProperties().get(OWNER);

        if (node == null) {
            return;
        }

        Pos position = (Pos) decoration.getProperties().get(POSITION);

        Bounds bounds = node.getLayoutBounds();
        Bounds decorationBounds = decoration.getLayoutBounds();

        double x = switch (position.getHpos()) {
            case LEFT -> bounds.getMinX() - decorationBounds.getWidth() / 2.0;
            case CENTER -> bounds.getCenterX() - decorationBounds.getWidth() / 2.0;
            case RIGHT -> bounds.getMaxX() - decorationBounds.getWidth() / 2.0;
            default -> throw new IllegalArgumentException("position: " + position);
        };

        double y = switch (position.getVpos()) {
            case TOP -> bounds.getMinY() - decorationBounds.getHeight() / 2.0;
            case CENTER -> bounds.getCenterY() - decorationBounds.getHeight() / 2.0;
            case BOTTOM -> bounds.getMaxY() - decorationBounds.getHeight() / 2.0;
            case BASELINE -> bounds.getMaxY() - decorationBounds.getHeight() / 2.0;
            default -> throw new IllegalArgumentException("position: " + position);
        };

        decoration.setLayoutX(x + node.getLayoutX());
        decoration.setLayoutY(y + node.getLayoutY());
    }

    private static String getDecorationId(String id) {
        return PREFIX + id;
    }

    public static void removeDecoration(Node node, String id) {
        Object oldDecoration = node.getProperties().remove(getDecorationId(id));
        if (oldDecoration != null) {
            DecorationPane decorationPane = DecorationPane.getDecorationPane(node);
            decorationPane.removeDecoration(oldDecoration);
        }
    }

}

class DecorationPane extends AnchorPane {

    static final String DECORATION_PANE = "com.dua3.decoration_pane";

    DecorationPane(Parent sceneRoot) {
        setBackground(null);
        getChildren().setAll(sceneRoot);
    }

    static DecorationPane getDecorationPane(Node node) {
        DecorationPane decorationPane = (DecorationPane) node.getProperties().get(DECORATION_PANE);
        if (decorationPane == null) {
            decorationPane = getDecorationPane(node.getScene());
            node.getProperties().put(DECORATION_PANE, decorationPane);
        }
        return decorationPane;
    }

    /**
     * Get DecorationPane scene. If no DecorationPane has been set up, inject a new DecorationPane
     * between the scene and its root.
     *
     * @param scene the Scene
     * @return the scene's DecorationPane
     */
    private static DecorationPane getDecorationPane(Scene scene) {
        Parent sceneRoot = scene.getRoot();

        if (sceneRoot instanceof DecorationPane) {
            return (DecorationPane) sceneRoot;
        }

        DecorationPane decorationPane = new DecorationPane(sceneRoot);
        scene.setRoot(decorationPane);
        scene.addPostLayoutPulseListener(decorationPane::updateDecorationLayout);
        return decorationPane;
    }

    private void updateDecorationLayout() {
        getChildren().forEach(Decoration::updateDecorationPosition);
    }

    void removeDecoration(Object oldDecoration) {
        this.getChildren().remove(oldDecoration);
    }
}
