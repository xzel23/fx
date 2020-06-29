package com.dua3.fx.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import javax.swing.text.Position;
import java.util.Objects;

public class Decoration {

    private static final String DECORATION_LIST = Decoration.class.getName()+".decoration_list";
    
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
        
        double x;
        switch (position.getHpos()) {
            case LEFT:
                x = bounds.getMinX()-decorationbounds.getWidth()/2.0;
                break;
            case CENTER:
                x = bounds.getCenterX()-decorationbounds.getWidth()/2.0;
                break;
            case RIGHT:
                x = bounds.getMaxX()-decorationbounds.getWidth()/2.0;
                break;
            default:
                throw new IllegalArgumentException("position: "+position);
        }
        
        double y;
        switch (position.getVpos()) {
            case TOP:
                y = bounds.getMinY()-decorationbounds.getHeight()/2.0;
                break;
            case CENTER:
                y = bounds.getCenterY()-decorationbounds.getHeight()/2.0;
                break;
            case BOTTOM:
                y = bounds.getMaxY()-decorationbounds.getHeight()/2.0;
                break;
            case BASELINE:
                y = bounds.getMaxY()-decorationbounds.getHeight()/2.0;
                break;
            default:
                throw new IllegalArgumentException("position: "+position);
        }

        decoration.setLayoutX(x+node.getLayoutX());
        decoration.setLayoutY(y+node.getLayoutY());
    }

}

class DecorationPane extends AnchorPane {

    public static final String DECORATION_PANE = "com.dua3.decoration_pane";

    private final Parent sceneRoot;

    /**
     * Get DecorationPane scene. If no DecorationPane has been set up, inject a new DecorationPane
     * between the scene and its root.
     * @param scene the Scene
     * @return the scene's DecorationPane
     */
    public static DecorationPane getDecorationPane(Scene scene) {
        Parent sceneRoot = scene.getRoot();
        
        if (sceneRoot instanceof DecorationPane) {
            return (DecorationPane) sceneRoot;
        }
        
        DecorationPane decorationPane = new DecorationPane(sceneRoot);
        scene.setRoot(decorationPane);
        scene.addPostLayoutPulseListener(decorationPane::updateDecorationLayout);
        return decorationPane;
    }
    
    public DecorationPane(Parent sceneRoot) {
        this.sceneRoot = Objects.requireNonNull(sceneRoot);
        setBackground(null);
        getChildren().setAll(sceneRoot);
    }
    
    public static DecorationPane getDecorationPane(Node node) {
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

    public void removeDecoration(Object oldDecoration) {
        this.getChildren().remove(oldDecoration);
    }
}
