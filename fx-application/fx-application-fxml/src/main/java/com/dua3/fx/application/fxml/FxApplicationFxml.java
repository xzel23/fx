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
import com.dua3.utility.application.LicenseData;
import com.dua3.utility.i18n.I18N;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Abstract class for JavaFX applications that use FXML to define the user interface.
 *
 * @param <A> the concrete application class extending this class
 * @param <C> the concrete controller class extending FxController
 */
public abstract class FxApplicationFxml<A extends FxApplicationFxml<A, C>, C extends FxController<A, C, ?>>
        extends FxApplication<A, C> {

    /**
     * Logger
     */
    private static final Logger LOG = LogManager.getLogger(FxApplicationFxml.class);

    /**
     * Constructor.
     * <p>
     * Note: i18n must contain the following mappings:
     * <ul>
     *     <li>"fx.application.fxml.url": the {@link URL} pointing to the FXML</li>
     *     <li>"fx.application.fxml.bundle": the {@link ResourceBundle} to use</li>
     * </ul>
     *
     * @param i18n the I18N instance for retrieving resources
     * @param license the license, if software is licensed
     */
    protected FxApplicationFxml(I18N i18n, @Nullable LicenseData license) {
        super(i18n, license);
    }

    /**
     * Initialize User Interface. The layout is defined in FXML.
     */
    @Override
    protected Parent createParentAndInitController() throws Exception {
        // create a loader and load FXML
        URL fxml = (URL) i18n.getObject("fx.application.fxml.url");
        ResourceBundle fxmlBundle = (ResourceBundle) i18n.getObject("fx.application.fxml.bundle");
        FXMLLoader loader = new FXMLLoader(fxml, fxmlBundle);

        Parent root = loader.load();

        // set controller
        LOG.debug("setting FXML controller");
        C controller = Objects.requireNonNull(loader.getController(),
                () -> "controller is null; set fx:controller in root element of FXML (" + fxml + ")");
        setController(controller);

        return root;
    }

}
