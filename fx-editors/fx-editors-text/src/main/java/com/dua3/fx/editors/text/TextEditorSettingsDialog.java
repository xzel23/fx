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

package com.dua3.fx.editors.text;

import com.dua3.fx.editors.EditorBase;
import com.dua3.fx.editors.EditorSettingsDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dialog to configure a editor settings.
 */
public class TextEditorSettingsDialog extends EditorSettingsDialog {

    /**
     * Logger instance
     */
    private static final Logger LOG = Logger.getLogger(TextEditorSettingsDialog.class.getName());

    private static final int FONT_SIZE_MIN = 5;

    private static final int FONT_SIZE_MAX = 30;

    private static final int FONT_SIZE_MAJOR_TICK = 10;

    private TextEditorSettings oldSetting;

    // -- button types
    public static final ButtonType OK = ButtonType.OK;
    public static final ButtonType RESET = new ButtonType("RESET");

    // -- input controls
    @FXML
    ComboBox<String> comboTheme;
    @FXML
    Slider sliderFontSize;
    @FXML
    CheckBox toggleShowLineNumbers;
    @FXML
    CheckBox toggleHighlightCurrentLine;

    private final EditorBase editor;

    /**
     * Construct new dialog instance.
     *
     * @param editor the editor instance
     */
    public TextEditorSettingsDialog(TextEditor editor) {
        this.editor = Objects.requireNonNull(editor);

        try {
            // load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("editor_settings.fxml"));
            loader.setController(this);
            DialogPane dialogPane = loader.load();

            // define buttons
            dialogPane.getButtonTypes().addAll(RESET, OK);

            // store current seeting
            oldSetting = TextEditorSettings.copyOf(editor.getSettings());

            // finally set the pane
            setDialogPane(dialogPane);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "could not create dialog", e);
        }
    }

    @FXML
    private void initialize() {
        // theme
        String theme = editor.getTheme();
        comboTheme.getItems().setAll("default", "xq-light", "xq-dark");
        comboTheme.setValue(theme);
        comboTheme.valueProperty().addListener((ov, o, n) -> editor.setTheme(n));

        // line numbers
        toggleShowLineNumbers.setSelected(editor.isShowLineNumbers());
        toggleShowLineNumbers.selectedProperty().addListener((ov, o, n) -> editor.setShowLineNumbers(n));

        // highlight current line
        toggleHighlightCurrentLine.setSelected(editor.isHighlightCurrentLine());
        toggleHighlightCurrentLine.selectedProperty().addListener((ov, o, n) -> editor.setHighlightCurrentLine(n));

        // font size
        sliderFontSize.setMin(FONT_SIZE_MIN);
        sliderFontSize.setMax(FONT_SIZE_MAX);
        sliderFontSize.setMajorTickUnit(FONT_SIZE_MAJOR_TICK);
        sliderFontSize.setMinorTickCount(FONT_SIZE_MAJOR_TICK);
        sliderFontSize.setValue(editor.getFontSize());
        sliderFontSize.valueProperty().addListener((ov, o, n) -> {
            int oldSize = o.intValue();
            int newSize = n.intValue();
            if (newSize != oldSize) {
                editor.setFontSize(newSize);
            }
        });
    }

    public TextEditorSettings getSetting() {
        TextEditorSettings s = new TextEditorSettings();
        s.setTheme(comboTheme.getValue());
        s.setFontSize((int) sliderFontSize.getValue());
        s.setShowLineNumbers(toggleShowLineNumbers.isSelected());
        s.setHighlightCurrentLine(toggleHighlightCurrentLine.isSelected());
        return s;
    }

    public TextEditorSettings getSettings() {
        TextEditorSettings s = new TextEditorSettings();
        s.setTheme(comboTheme.getValue());
        s.setFontSize((int) sliderFontSize.getValue());
        s.setShowLineNumbers(toggleShowLineNumbers.isSelected());
        s.setHighlightCurrentLine(toggleHighlightCurrentLine.isSelected());
        return s;
    }

    public TextEditorSettings getOldSettings() {
        return oldSetting;
    }
}
