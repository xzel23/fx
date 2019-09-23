package com.dua3.fx.icons;

import javafx.scene.Node;

import java.util.Objects;
import java.util.ServiceLoader;
import java.util.logging.Logger;

public class IconUtil {
    private static final Logger LOG = Logger.getLogger(IconUtil.class.getName());

    public static Node iconFromName(String name) {
        Class<IconProvider> iconProviderClass = IconProvider.class;
        return ServiceLoader.load(iconProviderClass)
                .stream()
                .peek(provider -> LOG.fine(() -> "found "+iconProviderClass.getName()+" implementation: "+provider.getClass().getName()))
                .map(provider->provider.get().forName(name))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

}
