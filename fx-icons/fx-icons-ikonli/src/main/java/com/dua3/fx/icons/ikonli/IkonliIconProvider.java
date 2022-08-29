package com.dua3.fx.icons.ikonli;

import com.dua3.fx.icons.Icon;
import com.dua3.fx.icons.IconProvider;
import javafx.scene.Node;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.IkonHandler;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.ServiceLoader;


public class IkonliIconProvider implements IconProvider {

    private static final Logger LOG = LoggerFactory.getLogger(IkonliIconProvider.class);

    static class IkonliIcon extends FontIcon implements Icon {
        private final String name;

        IkonliIcon(Ikon ikon, String name) {
            super(ikon);
            this.name = Objects.requireNonNull(name);
        }

        @Override
        public Node node() {
            return this;
        }

        @Override
        public String getIconIdentifier() {
            return name;
        }
        
    }
    
    public IkonliIconProvider() {}

    @Override
    public IkonliIcon forName(String name) {
        for (var handler : ServiceLoader.load(IkonHandler.class)) {
            if (handler.supports(name)) {
                LOG.debug("using: {}", handler.getClass().getName());
                var ikon = handler.resolve(name);
                return new IkonliIcon(ikon, name);
            }
        }

        LOG.debug("icon not found: {}", name);
        return null;
    }

    @Override
    public String name() {
        return getClass().getSimpleName();
    }

}
