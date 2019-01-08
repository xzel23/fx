package com.dua3.fx.samples.editor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SearchDialog extends Pane {

	/** Logger instance */
    private static final Logger LOG = Logger.getLogger(SearchDialog.class.getName());

	@FXML 
	TextField inputSearch;
	@FXML 
	TextField inputReplace;
	@FXML
	CheckBox ctlIgnoreCase;
	@FXML
	CheckBox ctlRegExp;
	@FXML
	CheckBox ctlWrapAround;
	@FXML
	Button btnSearch;
	@FXML
	Button btnReplace;
	@FXML
	Button btnCancel;
	
	SearchDialog(Window parent) {
    	try {
    		// load FXML
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("search.fxml"));
	        loader.setController(this);
	        VBox root = loader.load();

			Scene scene = new Scene(root);

			// setup stage
			Stage stage = new Stage();
			stage.setTitle("search");
			stage.setScene(scene);
			stage.show();
			
			} catch (IOException e) {
    		LOG.log(Level.WARNING, "could not create dialog", e);
    	}
	}
	
	@FXML
	public void search() {
	}

	@FXML
	public void replace() {
	}
}
