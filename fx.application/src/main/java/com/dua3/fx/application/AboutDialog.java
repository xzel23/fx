package com.dua3.fx.application;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

public class AboutDialog extends Dialog<Void> {
    /** Logger instance */
    private static final Logger LOG = Logger.getLogger(AboutDialog.class.getName());

    private URI mailUri = null;

    @FXML
    Label labelCopyright;
    
    @FXML
    Label labelVersion;
    
    @FXML
    Button btnOk;

    @FXML
    public void initialize() {
    }

    public AboutDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("about.fxml"));
        loader.setController(this);
        DialogPane dialogPane = loader.load();
        setDialogPane(dialogPane);
        dialogPane.getButtonTypes().addAll(ButtonType.OK);
     }

    public void mail() {
    	if (mailUri==null) {
            LOG.warning("email not configured");
            return;    		
    	}
    	
        if (!Desktop.isDesktopSupported()) {
            LOG.warning("Dekstop API is not supported");
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.MAIL)) {
            try {
                LOG.info("opening mail application");
                desktop.mail(mailUri);
            } catch (IOException e) {
                LOG.log(Level.WARNING, "could not open mail application", e);
            }
        }
    }
}
