// Copyright 2019 Axel Howind
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.dua3.fx.application.fxml;

import com.dua3.fx.application.FxApplication;
import com.dua3.fx.application.FxController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public abstract class FxApplicationFxml<A extends FxApplicationFxml<A, C>, C extends FxController<A, C, ?>>
        extends FxApplication<A, C> {

    /**
     * Logger
     */
    private static final Logger LOG = LogManager.getLogger(FxApplicationFxml.class);
    /**
     * The name of the default bundle that is used if the application does not provide its own bundle.
     */
    private static final String DEFAULT_BUNDLE = "fxapp";

    /**
     * Constructor.
     */
    protected FxApplicationFxml() {
    }

    /**
     * Constructor.
     *
     * @param resourceBundle the resource bundle for retrieving resources
     */
    protected FxApplicationFxml(ResourceBundle resourceBundle) {
        super(resourceBundle);
    }

    /**
     * Get default resource bundle.
     *
     * @return the default resource bundle
     */
    public static ResourceBundle getDefaultBundle() {
        // load resource bundle
        Locale locale = Locale.getDefault();
        LOG.debug("current locale is: {}", locale);
        ResourceBundle resources = ResourceBundle.getBundle(FxApplicationFxml.class.getPackageName() + "." + DEFAULT_BUNDLE, locale);
        LOG.debug("resource bundle uses locale: {}", resources.getLocale());
        return resources;
    }

    /**
     * Initialize User Interface. The layout is defined in FXML.
     */
    @Override
    protected Parent createParentAndInitController() throws Exception {
        // create a loader and load FXML
        URL fxml = getFxml();
        LOG.debug("FXML URL: {}", fxml);
        FXMLLoader loader = new FXMLLoader(fxml, resources);

        Parent root = loader.load();

        // set controller
        LOG.debug("setting FXML controller");
        C controller = Objects.requireNonNull(loader.getController(),
                () -> "controller is null; set fx:controller in root element of FXML (" + fxml + ")");
        setController(controller);

        return root;
    }

    /**
     * Get application main FXML file.
     *
     * @return the path to the FXML file to load, relative to the
     * application class
     */
    protected abstract URL getFxml();
}
