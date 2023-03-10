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

package com.dua3.fx.controls;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

public class AboutDialog extends Dialog<Void> {
    /**
     * Logger instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(AboutDialog.class);
    @FXML
    Label lTitle;
    @FXML
    Label lVersion;
    @FXML
    Label lCopyright;
    @FXML
    Hyperlink hlMail;
    @FXML
    Button btnOk;
    /**
     * The email URI, i.e. {@code "mailto:info@domain.com"}.
     */
    private String mailAddress = "";

    public AboutDialog() {
        this(null);
    }

    public AboutDialog(URL css) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("about.fxml"));
            loader.setController(this);
            DialogPane dialogPane = loader.load();
            URL dialogCss = css != null ? css : AboutDialog.class.getResource("about.css");
            assert dialogCss != null;
            dialogPane.getStylesheets().add(dialogCss.toExternalForm());
            setDialogPane(dialogPane);
            dialogPane.getButtonTypes().addAll(ButtonType.OK);
        } catch (IOException e) {
            LOG.warn("could not create dialog", e);
        }
    }

    @FXML
    public void initialize() {
        // no content. may be overridden.
    }

    public void mail() {
        if (mailAddress.isBlank()) {
            LOG.warn("email not configured");
            return;
        }

        if (!Desktop.isDesktopSupported()) {
            LOG.warn("Desktop API is not supported");
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.MAIL)) {
            try {
                LOG.info("opening mail application");
                desktop.mail(URI.create(mailAddress));
            } catch (IOException | IllegalArgumentException e) {
                LOG.warn("could not open mail application", e);
            }
        }
    }

    /**
     * Set name.
     *
     * @param value the text to display
     */
    public void setName(String value) {
        lTitle.setText(value);
    }

    /**
     * Set version text.
     *
     * @param value the text to display
     */
    public void setVersion(String value) {
        lVersion.setText(value);
    }

    /**
     * Set copyright text.
     *
     * @param value the text to display
     */
    public void setCopyright(String value) {
        lCopyright.setText(value);
    }

    /**
     * Set the email text.
     *
     * @param value the text to display for the email
     */
    public void setEmailText(String value) {
        hlMail.setText(value);
    }

    /**
     * Set the email URI.
     *
     * @param value the mail URI to set
     */
    public void setEmailAddress(String value) {
        mailAddress = Objects.requireNonNull(value);
    }
}
