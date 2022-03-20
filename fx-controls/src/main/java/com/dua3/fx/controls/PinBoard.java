/*
 * Copyright (c) 2022. Axel Howind (axel@dua3.com)
 * This package is distributed under the Artistic License 2.0.
 */

package com.dua3.fx.controls;

import com.dua3.fx.util.FxRefresh;
import com.dua3.fx.util.PlatformHelper;
import com.dua3.utility.data.Pair;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.AnchorPane;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * A JavaFX component where items can be pinned at a position.
 */
public class PinBoard extends Control {

    public PinBoard() {
    }
    
    public void clear() {
        items.clear();
        areaProperty.set(new Rectangle2D(0,0,0,0));
    }

    public void refresh() {
        ((PinBoardSkin) getSkin()).refresh();
    }

    public void dispose() {
        getSkin().dispose();    
    }
    
    record Item(Rectangle2D area, Supplier<Node> nodeBuilder) {}
    
    private final ObjectProperty<Rectangle2D> areaProperty = new SimpleObjectProperty<>(new Rectangle2D(0,0,0,0));
    
    final ObservableList<Item> items = FXCollections.observableArrayList();
    
    @Override
    protected Skin<PinBoard> createDefaultSkin() {
        return new PinBoardSkin(this);
    }

    public ReadOnlyObjectProperty<Rectangle2D> areaProperty() {
        return areaProperty;
    }
    
    public ObservableList<Item> getItems() {
        return FXCollections.unmodifiableObservableList(items);
    }
    
    public Rectangle2D getArea() {
        return areaProperty.get();
    }
    
    public void pin(Item... itemsToPin) {
        if (itemsToPin.length==0) {
            return;
        }

        Rectangle2D boardArea = getArea();
        
        Rectangle2D area = items.isEmpty() ? itemsToPin[0].area() : boardArea;
        double minX = area.getMinX();
        double maxX = area.getMaxX();
        double minY = area.getMinY();
        double maxY = area.getMaxY();

        for (var item: itemsToPin) {
            Rectangle2D itemArea = item.area();
            minX = Math.min(minX, itemArea.getMinX());    
            maxX = Math.max(maxX, itemArea.getMaxX());    
            minY = Math.min(minY, itemArea.getMinY());    
            maxY = Math.max(maxY, itemArea.getMaxY());    
        }
        
        Rectangle2D newArea = new Rectangle2D(minX, minY, maxX-minX, maxY-minY);

        if (!newArea.equals(boardArea)) {
            areaProperty.set(newArea);
            setWidth(newArea.getWidth());
            setHeight(newArea.getHeight());
        }

        items.addAll(Arrays.asList(itemsToPin));
    }
    
    /**
     * Add items at the bottom, centered horizontally.
     */
    @SafeVarargs
    public final void pinBottom(Pair<Supplier<Node>, Dimension2D>... itemsToPin) {
        pinBottom(Arrays.asList(itemsToPin));
    }
    
    public void pinBottom(List<Pair<Supplier<Node>, Dimension2D>> itemsToPin) {
        if (itemsToPin.isEmpty()) {
            return;
        }

        Rectangle2D boardArea = getArea();
        double xCenter = (boardArea.getMaxX()+boardArea.getMinX())/2.0;
        double y = boardArea.getMaxY();
        
        Item[] items = new Item[itemsToPin.size()];
        for (int i = 0; i < itemsToPin.size(); i++) {
            Pair<Supplier<Node>, Dimension2D> iter = itemsToPin.get(i);
            Supplier<Node> nodeBuilder = iter.first();
            Dimension2D size = iter.second();
            Rectangle2D area = new Rectangle2D(xCenter - size.getWidth() / 2, y, size.getWidth(), size.getHeight());
            items[i] = new Item(area, nodeBuilder);
            y += size.getHeight();
        }
        
        pin(items);
    }
    
    @Override
    public String toString() {
        return "PinBoard{" +
               "area=" + areaProperty.get() +
               ", items=" + items +
               '}';
    }
}

class PinBoardSkin extends SkinBase<PinBoard>  {

    private final FxRefresh refresher;
    private final AnchorPane pane = new AnchorPane();
    private final ScrollPane scrollPane = new ScrollPane(pane);

    public PinBoardSkin(PinBoard pinBoard) {
        super(pinBoard);
        
        this.refresher = FxRefresh.create(PinBoardSkin.class.getSimpleName(), this::updateNodes, pinBoard);
        
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);

        getChildren().setAll(scrollPane);

        pinBoard.areaProperty().addListener( (v,o,n) -> {
            pane.setMinWidth(n.getWidth()); 
            pane.setMinHeight(n.getHeight());
        });

        pinBoard.getItems().addListener((ListChangeListener.Change<?> c) -> refresh());
        pane.layoutBoundsProperty().addListener((o) -> refresh());
        scrollPane.hvalueProperty().addListener((h) -> refresh());
        scrollPane.vvalueProperty().addListener((v) -> refresh());
        scrollPane.widthProperty().addListener((e) -> refresh());
        scrollPane.heightProperty().addListener((e) -> refresh());
        
        // enable/disable refresher
        refresher.setActive(true);
    }
    
    void refresh() {
        refresher.refresh();
    }
    
    private Rectangle2D getViewPort() {
        Bounds vpBounds = scrollPane.getViewportBounds();
        return new Rectangle2D(-vpBounds.getMinX(), -vpBounds.getMinY(), vpBounds.getWidth(), vpBounds.getHeight());
    }
    
    private void updateNodes() {
        PinBoard board = getSkinnable();

        Rectangle2D viewPort = getViewPort();
        Rectangle2D boardArea = board.getArea();
        
        double dx = Math.max(0, viewPort.getWidth()-boardArea.getWidth())/2.0;
        double dy = Math.max(0, viewPort.getHeight()-boardArea.getHeight())/2.0;
        
        pane.setMinWidth(boardArea.getWidth());
        pane.setMinHeight(boardArea.getHeight());

        // populate pane with nodes of visible items
        List<Node> nodes = board.items.parallelStream()
                .filter(item -> item.area().intersects(viewPort))
                .map(item -> {
                    Rectangle2D itemArea = item.area();
                    Node node = item.nodeBuilder().get();
                    node.setTranslateX(dx);
                    node.setTranslateY(dy+itemArea.getMinY());
                    return node;
                })
                .toList();
        PlatformHelper.runAndWait(() -> pane.getChildren().setAll(nodes));
        
        // immediately start next refresh if viewport changed during updated
        if (!getViewPort().equals(viewPort)) {
            refresh();
        }
    }

    @Override
    public void dispose() {
        refresher.stop();
        super.dispose();
    }
}
