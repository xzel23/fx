package com.dua3.fx.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

public class AboutDialog extends Dialog<Void> {
    /** Logger instance */
    private static final Logger LOG = Logger.getLogger(AboutDialog.class.getName());

    /** The email URI, i.e. "mailto:info@domain.com". */
    private String mailAddress = "";

    @FXML
    Label labelName;
    
    @FXML
    Label labelVersion;
    
    @FXML
    Label labelCopyright;
    
    @FXML
    Hyperlink hlMail;

    @FXML
    Button btnOk;

    @FXML
    public void initialize() {
    }

    public AboutDialog() {
    	try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("about.fxml"));
	        loader.setController(this);
	        DialogPane dialogPane = loader.load();
	        setDialogPane(dialogPane);
	        dialogPane.getButtonTypes().addAll(ButtonType.OK);
    	} catch (IOException e) {
    		LOG.log(Level.WARNING, "could not create dialog", e);
    	}
     }

    public void mail() {
    	if (mailAddress.isBlank()) {
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
                desktop.mail(URI.create(mailAddress));
            } catch (IOException e) {
                LOG.log(Level.WARNING, "could not open mail application", e);
            }
        }
    }

    /**
     * Set name.
     * @param value
     *  the text to display
     */
	public void setName(String value) {
		labelName.setText(value);
	}
	
	/**
	 * Set version text.
	 * @param value
	 *  the text to display
	 */
	public void setVersion(String value) {
		labelVersion.setText(value);
	}
	
    /**
     * Set copyright text.
     * @param value
     *  the text to display
     */
    public void setCopyright(String value) {
    	labelCopyright.setText(value);
    }
    
    /**
     * Set the email text.
     * @param value the text to display for the email
     */
    public void setEmailText(String value) {
    	hlMail.setText(value);
    }

    /**
     * Set the email URI.
     * @param value the mail URI to set
     */
    public void setEmailAddress(String value) {
        mailAddress = Objects.requireNonNull(value);
    }
}
