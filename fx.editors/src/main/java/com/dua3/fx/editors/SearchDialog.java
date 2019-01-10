package com.dua3.fx.editors;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dua3.fx.editors.intern.EditorBase;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SearchDialog {

	/** Logger instance */
	private static final Logger LOG = Logger.getLogger(SearchDialog.class.getName());

	@FXML
	TextField inputSearchPattern;
	@FXML
	TextField inputReplacement;
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
	Button btnClose;

	private final EditorBase editor;

	private final VBox root;

	public SearchDialog(EditorBase editor) {
		this.editor = Objects.requireNonNull(editor);

		try {
			// load FXML
			FXMLLoader loader = new FXMLLoader(getClass().getResource("intern/search_dialog.fxml"));
			loader.setController(this);
			root = loader.load();

			Scene scene = new Scene(root);

			// setup stage
			Stage stage = new Stage();
			stage.setTitle("search");
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			throw new UncheckedIOException("could not create dialog", e);
		}
	}

	public String getPattern() {
		return inputSearchPattern.getText();
	}

	public String getReplacement() {
		return inputReplacement.getText();
	}

	public boolean isIgnoreCase() {
		return ctlIgnoreCase.isSelected();
	}

	public boolean isRegExp() {
		return ctlRegExp.isSelected();
	}

	public boolean isWrapAround() {
		return ctlWrapAround.isSelected();
	}

	@FXML
	public void search() {
		editor.search(getPattern(), isIgnoreCase(), isRegExp(), isWrapAround());
	}
	
	@FXML
	public void search() {
		editor.search(getPattern(), isIgnoreCase(), isRegExp(), isWrapAround());
	}

	@FXML
	public void replace() {
	}
	
	@FXML 
	public void close() {
		Stage stage = (Stage) root.getScene().getWindow();
		stage.close();
	}

	@FXML public void close() {
		Stage stage = (Stage) root.getScene().getWindow();
		stage.close();
	}
}
