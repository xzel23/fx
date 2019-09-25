package com.dua3.fx.icons.ikonli;

import com.dua3.fx.icons.Icon;
import com.dua3.fx.icons.IconProvider;
import javafx.scene.Node;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.IkonHandler;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ServiceLoader;
import java.util.logging.Logger;

public class IkonliIconProvider implements IconProvider {

    private static final Logger LOG = Logger.getLogger(IkonliIconProvider.class.getName());

    static class IkonliIcon extends FontIcon implements Icon {
        public IkonliIcon(Ikon ikon) {
            super(ikon);
        }

        @Override
        public Node node() {
            return this;
        }
    }

    @Override
    public IkonliIcon forName(String name) {
        for (var handler : ServiceLoader.load(IkonHandler.class)) {
            if (handler.supports(name)) {
                LOG.fine(() -> "using: " + handler.getClass().getCanonicalName());
                var ikon = handler.resolve(name);
                return new IkonliIcon(ikon);
            }
        }

        LOG.fine(() -> "icon not found: " + name);
        return null;
    }

    @Override
    public String name() {
        return getClass().getSimpleName();
    }

}
