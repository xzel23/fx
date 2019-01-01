package com.dua3.fx.samples.editor;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

public class AboutDialog extends Dialog<Void> {
    /** Logger instance */
    private static final Logger LOG = Logger.getLogger(AboutDialog.class.getName());

    private static final URI URI_MAIL_ME = URI.create("mailto:axel@dua3.com?subject=Document%20Viewer");

    @FXML
    Button btnOk;

    @FXML
    public void initialize() {
    }

    public AboutDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("about_editor.fxml"));
        loader.setController(this);
        DialogPane dialogPane = loader.load();
        setDialogPane(dialogPane);
        dialogPane.getButtonTypes().addAll(ButtonType.OK);
     }

    public void mailMe() {
        if (!Desktop.isDesktopSupported()) {
            LOG.warning("Dekstop API is not supported");
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.MAIL)) {
            try {
                LOG.info("opening mail application");
                desktop.mail(URI_MAIL_ME);
            } catch (IOException e) {
                LOG.warning("could not open mail application");
            }
        }
    }
}
