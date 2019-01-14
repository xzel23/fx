package com.dua3.fx.editors.intern;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Slider;

/**
 * Dialog to configure a editor settings.
 */
public class EditorSettingsDialog extends Dialog<ButtonType> {

	/** Logger instance */
    private static final Logger LOG = Logger.getLogger(EditorSettingsDialog.class.getName());

	private static final int FONT_SIZE_MIN = 5;

	private static final int FONT_SIZE_MAX = 30;

	private static final int FONT_SIZE_MAJOR_TICK = 10;
	
    // -- button types
	public final ButtonType OK = ButtonType.OK;
    public final ButtonType RESET = new ButtonType("RESET");

    // -- input controls
	@FXML ComboBox<String> comboTheme;
	@FXML Slider sliderFontSize;
	@FXML CheckBox toggleShowLineNumbers;

	private final EditorBase editor;
		
	/**
	 * Construct new dialog instance.
	 * 
	 * @param editor
	 *  the editor instance
	 * @param sampleText
	 *  the sample text to show as preview
	 * @param extension
	 *  the extension for syntax highlighting
	 */
	public EditorSettingsDialog(EditorBase editor) {
		this.editor = Objects.requireNonNull(editor);

		try {
    		// load FXML
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("editor_settings.fxml"));
	        loader.setController(this);
	        DialogPane dialogPane = loader.load();
	        
	        // define buttons
	        dialogPane.getButtonTypes().addAll(RESET, OK);
	        
	        // finally set the pane
	        setDialogPane(dialogPane);
    	} catch (IOException e) {
    		LOG.log(Level.WARNING, "could not create dialog", e);
    	}
	}

	@FXML
	private void initialize() {
		toggleShowLineNumbers.setSelected(editor.isShowLineNumbers());
		toggleShowLineNumbers.selectedProperty().addListener( (ov,o,n) -> {
        	editor.setShowLineNumbers(n);
        });	

		sliderFontSize.setMin(FONT_SIZE_MIN);
		sliderFontSize.setMax(FONT_SIZE_MAX);
		sliderFontSize.setMajorTickUnit(FONT_SIZE_MAJOR_TICK);
		sliderFontSize.setMinorTickCount(FONT_SIZE_MAJOR_TICK);
		sliderFontSize.setValue(editor.getFontSize());
		sliderFontSize.valueProperty().addListener( (ov,o,n) -> {
        	editor.setFontSize(n.intValue());
        });	
	}
}
