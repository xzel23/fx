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

package com.dua3.fx.editors.markdown;

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
public class MarkdownEditorSettingsDialog extends EditorSettingsDialog {

    /**
     * Logger instance
     */
    private static final Logger LOG = Logger.getLogger(MarkdownEditorSettingsDialog.class.getName());

    private MarkdownEditorSettings oldSettings;

    // -- button types
    public static final ButtonType OK = ButtonType.OK;
    public static final ButtonType RESET = new ButtonType("RESET");

    // -- input controls

    private final EditorBase editor;

    /**
     * Construct new dialog instance.
     *
     * @param editor the editor instance
     */
    public MarkdownEditorSettingsDialog(MarkdownEditor editor) {
        this.editor = Objects.requireNonNull(editor);

        try {
            // load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("editor_settings.fxml"));
            loader.setController(this);
            DialogPane dialogPane = loader.load();

            // define buttons
            dialogPane.getButtonTypes().addAll(RESET, OK);

            // store current seeting
            oldSettings = MarkdownEditorSettings.copyOf(editor.getSettings());

            // finally set the pane
            setDialogPane(dialogPane);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "could not create dialog", e);
        }
    }

    @FXML
    private void initialize() {
    }

    public MarkdownEditorSettings getSettings() {
        MarkdownEditorSettings s = new MarkdownEditorSettings();
        return s;
    }

    public MarkdownEditorSettings getOldSettings() {
        return oldSettings;
    }
}
