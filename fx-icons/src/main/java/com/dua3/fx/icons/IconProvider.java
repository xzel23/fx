package com.dua3.fx.icons;

import javafx.scene.Node;

public interface IconProvider {
    /**
     * Get this provider*s name.
     * @return
     *  provider name
     */
    String name();

    /**
     * Get icon.
     * @param name
     *  name of the requested icon
     * @return
     *  icon or {@code null} if this provider does not offer the requested icon
     */
    Node forName(String name);
}
