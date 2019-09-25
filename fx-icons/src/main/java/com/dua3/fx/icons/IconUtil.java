package com.dua3.fx.icons;

import javafx.scene.Node;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class IconUtil {
    private static final Logger LOG = Logger.getLogger(IconUtil.class.getName());

    public static Optional<Node> iconFromName(String name) {
        Class<IconProvider> iconProviderClass = IconProvider.class;
        return ServiceLoader.load(iconProviderClass)
                .stream()
                .peek(provider -> LOG.fine(() -> "found "+iconProviderClass.getName()+" implementation: "+provider.getClass().getName()))
                .map(provider->provider.get().forName(name))
                .filter(Objects::nonNull)
                .findFirst();
    }

    public static Collection<String> iconProviders() {
        Class<IconProvider> iconProviderClass = IconProvider.class;
        return ServiceLoader.load(iconProviderClass)
                .stream()
                .map(p -> p.type().getName())
                .collect(Collectors.toUnmodifiableList());
    }

}
