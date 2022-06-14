/*
 * Copyright (c) 2022. Axel Howind (axel@dua3.com)
 * This package is distributed under the Artistic License 2.0.
 */

package com.dua3.fx.controls;

import com.dua3.fx.util.FxRefresh;
import com.dua3.fx.util.FxUtil;
import com.dua3.fx.util.PlatformHelper;
import com.dua3.utility.data.Pair;
import com.dua3.utility.lang.LangUtil;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * A JavaFX component where items can be pinned at a position.
 */
public class PinBoard extends Control {

    public PinBoard() {
    }
    
    public void clear() {
        PlatformHelper.checkApplicationThread();
        items.clear();
        areaProperty.set(new Rectangle2D(0,0,0,0));
    }

    public void refresh() {
        if (getSkin() instanceof PinBoardSkin skin) {
            skin.refresh();
        }
    }

    public void dispose() {
        if (getSkin() instanceof PinBoardSkin skin) {
            skin.dispose();
        }
    }
    
    public record Item(String name, Rectangle2D area, Supplier<Node> nodeBuilder) {}
    
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
    
    public Pair<Double,Double> getScrollPosition() {
        if (getSkin() instanceof PinBoardSkin skin) {
            return skin.getScrollPosition();
        } else {
            return Pair.of(0.0,0.0);
        }
    }
    
    public void setScrollPosition(double hValue, double vValue) {
        if (getSkin() instanceof PinBoardSkin skin) {
            skin.setScrollPosition(hValue, vValue);
        }
    }

    public void setScrollPosition(Pair<Double,Double> scrollPosition) {
        setScrollPosition(scrollPosition.first(), scrollPosition.second());
    }

    public void pin(Item item) {
        pin(Collections.singleton(item));
    }

    public void pin(Collection<Item> itemsToPin) {
        PlatformHelper.checkApplicationThread();

        if (itemsToPin.isEmpty()) {
            return;
        }

        this.items.addAll(itemsToPin);

        itemsToPin.stream()
                .map(Item::area)
                .reduce(FxUtil::union)
                .map(r -> FxUtil.union(this.getArea(), r))
                .ifPresent( r -> {
                    if (!r.equals(getArea())) {
                        areaProperty.set(r);
                    }
                });
    }

    public record PositionInItem(Item item, double x, double y) {}

    /**
     * Get Item at point and coordinates trransformed to item coordinates.
     * @param x x-coordinate (relative to viewport)
     * @param y y-coordinate (relative to viewport)
     * @return Optional containing the item and the transformed coordinates
     */
    public Optional<PositionInItem> getPositionInItem(double x, double y) {
        if (getSkin() instanceof PinBoardSkin skin) {
            return skin.getPositionInItem(x,y);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Get Item at point.
     * @param x x-coordinate (relative to viewport)
     * @param y y-coordinate (relative to viewport)
     * @return Optional containing the item at (x,y)
     */
    public Optional<Item> getItemAt(double x, double y) {
        return getPositionInItem(x, y).map(PositionInItem::item);
    }

    /**
     * Add item at the bottom, centered horizontally.
     * @param name item name
     * @param nodeSupplier supplier (factory) for item node
     * @param dimension item dimensiuon
     */
    public void pinBottom(String name, Supplier<Node> nodeSupplier, Dimension2D dimension) {
        Rectangle2D boardArea = getArea();
        double xCenter = (boardArea.getMaxX()+boardArea.getMinX())/2.0;
        double y = boardArea.getMaxY();
        Rectangle2D area = new Rectangle2D(xCenter - dimension.getWidth() / 2, y, dimension.getWidth(), dimension.getHeight());
        pin(new Item(name, area, nodeSupplier));
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

    private static final Logger LOG = Logger.getLogger(PinBoardSkin.class.getName());
    private final FxRefresh refresher;
    private final AnchorPane pane = new AnchorPane();
    private final ScrollPane scrollPane = new ScrollPane(pane);

    PinBoardSkin(PinBoard pinBoard) {
        super(pinBoard);
        
        this.refresher = FxRefresh.create(
                LangUtil.defaultToString(this),
                () -> PlatformHelper.runLater(this::updateNodes),
                pinBoard
        );
        
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
        scrollPane.viewportBoundsProperty().addListener((v,o,n) -> refresh());
        
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
        LOG.fine("updatreNodes()");

        PlatformHelper.checkApplicationThread();

        PinBoard board = getSkinnable();

        Rectangle2D viewPort = getViewPort();
        Rectangle2D boardArea = board.getArea();

        double dx = Math.max(0, viewPort.getWidth() - boardArea.getWidth()) / 2.0;
        double dy = Math.max(0, viewPort.getHeight() - boardArea.getHeight()) / 2.0;

        // populate pane with nodes of visible items
        List<Node> nodes = new ArrayList<>(board.items) // copy list to avoid concurrent modification
                .stream()
                .filter(item -> item.area().intersects(viewPort))
                .map(item -> {
                    LOG.finer(() -> "item is visible: "+item.name());
                    Rectangle2D itemArea = item.area();
                    Node node = item.nodeBuilder().get();
                    node.setTranslateX(dx);
                    node.setTranslateY(dy + itemArea.getMinY());
                    return node;
                })
                .toList();

        pane.setMinWidth(boardArea.getWidth());
        pane.setMinHeight(boardArea.getHeight());
        pane.getChildren().setAll(nodes);
    }

    @Override
    public void dispose() {
        refresher.stop();
        super.dispose();
    }

    public Pair<Double,Double> getScrollPosition() {
        return Pair.of(scrollPane.getHvalue(), scrollPane.getVvalue());
    }

    public void setScrollPosition(double hValue, double vValue) {
        scrollPane.setHvalue(hValue);
        scrollPane.setVvalue(vValue);
    }

    /**
     * Get Item at point and coordinates relative to item.
     * @param xViewport x-coordinate (relative to board)
     * @param yViewport y-coordinate (relative to board)
     * @return Optional containing the item at (x,y) and the coordinates relative to the item area
     */
    public Optional<PinBoard.PositionInItem> getPositionInItem(double xViewport, double yViewport) {
        Rectangle2D vp = getViewPort();
        double x = xViewport + vp.getMinX();
        double y = yViewport + vp.getMinY();
        Rectangle2D b = getSkinnable().getArea();
        List<PinBoard.Item> items = new ArrayList<>(getSkinnable().getItems());
        for (PinBoard.Item item: items) {
            Rectangle2D a = item.area();
            if (a.contains(x, y)) {
                return Optional.of(new PinBoard.PositionInItem(item, x+b.getMinX()-a.getMinX(), y+b.getMinY()-a.getMinY()));
            }
        }
        return Optional.empty();
    }
}
