package com.dua3.fx.controls;

import javafx.geometry.Point2D;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Code based on
 *   https://github.com/grubbcc/anagrams/blob/browser/client/java/client/DragResizer.java
 * and
 *   https://gist.github.com/andytill/4369729
 */

class DragResizer {

    /**
     * The margin (in pixels) around the control that a user can click to resize the region.
     */

    private final Region region;
    private final Set<Border> borders;
    private final int resizeMargin;

    private boolean draggingTop;
    private boolean draggingRight;
    private boolean draggingBottom;
    private boolean draggingLeft;

    /**
     *
     */

    private DragResizer(Region region, int resizeMargin, Border... borders) {
        this.region = Objects.requireNonNull(region);
        this.resizeMargin = resizeMargin;
        this.borders = EnumSet.noneOf(Border.class);
        this.borders.addAll(Arrays.asList(borders));
    }

    /**
     *
     */
    public static void makeResizable(Region region, int resizeMargin, Border... borders) {
        final DragResizer resizer = new DragResizer(region, resizeMargin, borders);

        region.setOnMousePressed(resizer::mousePressed);
        region.setOnMouseDragged(resizer::mouseDragged);
        region.setOnMouseMoved(resizer::mouseOver);
        region.setOnMouseReleased(resizer::mouseReleased);
    }

    /**
     * Sets the cursor to the appropriate type.
     */

    private void mouseOver(MouseEvent event) {
        if (isInDraggableZoneTop(event) || draggingTop) {
            if(isInDraggableZoneRight(event) || draggingRight) {
                region.setCursor(Cursor.NE_RESIZE);
            } else if(isInDraggableZoneLeft(event) || draggingLeft) {
                region.setCursor(Cursor.NW_RESIZE);
            } else {
                region.setCursor(Cursor.N_RESIZE);
            }
        }
        else if (isInDraggableZoneBottom(event) || draggingBottom) {
            if(isInDraggableZoneRight(event) || draggingRight) {
                region.setCursor(Cursor.SE_RESIZE);
            } else if(isInDraggableZoneLeft(event) || draggingLeft) {
                region.setCursor(Cursor.SW_RESIZE);
            } else {
                region.setCursor(Cursor.S_RESIZE);
            }
        } else if (isInDraggableZoneRight(event) || draggingRight) {
            region.setCursor(Cursor.E_RESIZE);
        } else if (isInDraggableZoneLeft(event) || draggingLeft) {
            region.setCursor(Cursor.W_RESIZE);
        } else {
            region.setCursor(Cursor.DEFAULT);
        }
    }

    /**
     *
     */

    private void mousePressed(MouseEvent event) {
        event.consume();

        draggingTop = isInDraggableZoneTop(event);
        draggingRight = isInDraggableZoneRight(event);
        draggingBottom = isInDraggableZoneBottom(event);
        draggingLeft = isInDraggableZoneLeft(event);
    }

    /**
     *
     */

    private boolean isInDraggableZoneTop(MouseEvent event) {
        return borders.contains(Border.TOP) && event.getY() < resizeMargin;
    }

    /**
     *
     */

    private boolean isInDraggableZoneLeft(MouseEvent event) {
        return borders.contains(Border.LEFT) && event.getX() < resizeMargin;
    }

    /**
     *
     */

    private boolean isInDraggableZoneBottom(MouseEvent event) {
        return borders.contains(Border.BOTTOM) && event.getY() > (region.getHeight() - resizeMargin);
    }

    /**
     *
     */

    private boolean isInDraggableZoneRight(MouseEvent event) {
        return borders.contains(Border.RIGHT) && event.getX() > (region.getWidth() - resizeMargin);
    }

    /**
     *
     */

    private void mouseDragged(MouseEvent event) {
        event.consume();

        if (draggingBottom) {
            resizeBottom(event);
        }
        if (draggingRight) {
            resizeRight(event);
        }
        if (draggingTop) {
            resizeTop(event);
        }
        if (draggingLeft) {
            resizeLeft(event);
        }
    }

    /**
     *
     */

    private void resizeTop(MouseEvent event) {
        double prevMin = region.getMinHeight();
        region.setMinHeight(region.getMinHeight() - event.getY());
        
        if (region.getMinHeight() < region.getPrefHeight()) {
            region.setMinHeight(region.getPrefHeight());
            region.setTranslateY(region.getTranslateY() - (region.getPrefHeight() - prevMin));
            return;
        }
        
        if (region.getMinHeight() > region.getPrefHeight() || event.getY() < 0) {
            region.setTranslateY(region.getTranslateY() + event.getY());
        }
    }

    /**
     *
     */

    private void resizeRight(MouseEvent event) {
        region.setMinWidth(event.getX());
    }

    /**
     *
     */

    private void resizeBottom(MouseEvent event) {
        region.setMinHeight(event.getY());
    }

    /**
     *
     */

    private void resizeLeft(MouseEvent event) {
        double prevMin = region.getMinWidth();
        region.setMinWidth(region.getMinWidth() - event.getX());
        
        if (region.getMinWidth() < region.getPrefWidth()) {
            region.setMinWidth(region.getPrefWidth());
            region.setTranslateX(region.getTranslateX() - (region.getPrefWidth() - prevMin));
            return;
        }
        
        if (region.getMinWidth() > region.getPrefWidth() || event.getX() < 0) {
            region.setTranslateX(region.getTranslateX() + event.getX());
        }
    }

    /**
     *
     */

    protected void mouseReleased(MouseEvent event) {
        draggingTop = draggingRight = draggingBottom = draggingLeft = false;
        region.setCursor(Cursor.DEFAULT);
    }
}
