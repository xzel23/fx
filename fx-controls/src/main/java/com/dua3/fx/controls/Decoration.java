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

    private static final String DECORATION_LIST = Decoration.class.getName()+".decoration_list";

    private Decoration() {
    }

    public static ObservableList<Decoration> getDecorations(Node node) {
        @SuppressWarnings("unchecked")
        ObservableList<Decoration> decorations = (ObservableList<Decoration>) node.getProperties().get(DECORATION_LIST);
        if (decorations==null) {
            decorations = FXCollections.observableArrayList();
            node.getProperties().put(DECORATION_LIST, decorations);
        }
        return decorations;
    }

    private static final String MASTER = Decoration.class.getName()+":master";
    private static final String POSITION = Decoration.class.getName()+":position";
    
    public static void addDecoration(Node node, Pos position, Node decoration, String id) {
        DecorationPane decorationPane = DecorationPane.getDecorationPane(node);
        
        decoration.getProperties().put(MASTER, node);
        decoration.getProperties().put(POSITION, position);
        updateDecorationPosition(decoration);
        Object oldDecoration = node.getProperties().put(getDecorationId(id), decoration);
        
        if (oldDecoration!=null) {
            decorationPane.removeDecoration(oldDecoration);
        }
        
        decorationPane.getChildren().add(decoration);
    }

    public static void removeDecoration(Node node, String id) {
        Object oldDecoration = node.getProperties().remove(getDecorationId(id));
        if (oldDecoration!=null) {
            DecorationPane decorationPane = DecorationPane.getDecorationPane(node);
            decorationPane.removeDecoration(oldDecoration);
        }
    }

    private static final String PREFIX = Decoration.class.getName()+":";

    private static String getDecorationId(String id) {
        return PREFIX+id;
    }

    static void updateDecorationPosition(Node decoration) {
        Node node = (Node) decoration.getProperties().get(MASTER);
        
        if (node == null) {
            return;
        }
        
        Pos position = (Pos) decoration.getProperties().get(POSITION);
        
        Bounds bounds = node.getLayoutBounds();
        Bounds decorationbounds = decoration.getLayoutBounds();
        
        double x = switch (position.getHpos()) {
            case LEFT -> bounds.getMinX() - decorationbounds.getWidth() / 2.0;
            case CENTER -> bounds.getCenterX() - decorationbounds.getWidth() / 2.0;
            case RIGHT -> bounds.getMaxX() - decorationbounds.getWidth() / 2.0;
            default -> throw new IllegalArgumentException("position: " + position);
        };

        double y = switch (position.getVpos()) {
            case TOP -> bounds.getMinY() - decorationbounds.getHeight() / 2.0;
            case CENTER -> bounds.getCenterY() - decorationbounds.getHeight() / 2.0;
            case BOTTOM -> bounds.getMaxY() - decorationbounds.getHeight() / 2.0;
            case BASELINE -> bounds.getMaxY() - decorationbounds.getHeight() / 2.0;
            default -> throw new IllegalArgumentException("position: " + position);
        };

        decoration.setLayoutX(x+node.getLayoutX());
        decoration.setLayoutY(y+node.getLayoutY());
    }

}

class DecorationPane extends AnchorPane {

    static final String DECORATION_PANE = "com.dua3.decoration_pane";

    /**
     * Get DecorationPane scene. If no DecorationPane has been set up, inject a new DecorationPane
     * between the scene and its root.
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
    
    DecorationPane(Parent sceneRoot) {
        setBackground(null);
        getChildren().setAll(sceneRoot);
    }
    
    static DecorationPane getDecorationPane(Node node) {
        DecorationPane decorationPane = (DecorationPane) node.getProperties().get(DECORATION_PANE);
        if (decorationPane==null) {
            decorationPane = getDecorationPane(node.getScene());
            node.getProperties().put(DECORATION_PANE, decorationPane);
        }
        return decorationPane;
    }
    
    private void updateDecorationLayout() {
        getChildren().forEach(Decoration::updateDecorationPosition);
    }

    void removeDecoration(Object oldDecoration) {
        this.getChildren().remove(oldDecoration);
    }
}
