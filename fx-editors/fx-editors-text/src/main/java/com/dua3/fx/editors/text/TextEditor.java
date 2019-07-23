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
import com.dua3.fx.editors.EditorSettings;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

public class TextEditor extends EditorBase {
    /**
     * Logger
     */
    private static final Logger LOG = Logger.getLogger(TextEditor.class.getName());

    /**
     * Default constructor.
     */
    public TextEditor() {
        super(TextEditor.class.getResource("editor_text.fxml"),
              TextEditor.class.getResource("node/editor_text.html"));
    }

    /**
     * Set editing mode from file extension.
     *
     * @param extension the file extension
     */
    public void setModeFromExtension(String extension) {
        LOG.fine(() -> String.format("setting mode by file extension: %s", extension));
        getBridge().call("setModeFromExtension", extension);
    }

    public boolean isHighlightCurrentLine() {
        return Boolean.TRUE.equals(getBridge().call("isHighlightCurrentLine"));
    }

    public void setHighlightCurrentLine(Boolean flag) {
        LOG.fine(() -> String.format("setting highlight current line mode: %s", flag));
        getBridge().call("setHighlightCurrentLine", flag);
    }

    public boolean isShowLineNumbers() {
        return Boolean.TRUE.equals(getBridge().call("isShowLineNumbers()"));
    }

    public void setShowLineNumbers(Boolean flag) {
        LOG.fine(() -> String.format("setting line number mode: %s", flag));
        getBridge().call("setShowLineNumbers", flag);
    }

    public int getFontSize() {
        int size = ((Number) getBridge().call("getFontSize")).intValue();
        LOG.fine(() -> String.format("font size: %d", size));
        return size;
    }

    public void setFontSize(int size) {
        LOG.fine(() -> String.format("setting font size: %d", size));
        getBridge().call("setFontSize", size);
    }

    public String getTheme() {
        return String.valueOf(getBridge().call("getTheme"));
    }

    public void setTheme(String theme) {
        LOG.fine(() -> String.format("setting theme: %s", theme));
        getBridge().call("setTheme", theme);
    }

    public Iterator<String> lineIterator() {
        return new Iterator<String>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < getLineCount();
            }

            @Override
            public String next() {
                if (i >= getLineCount()) {
                    throw new NoSuchElementException();
                }
                return getLine(i++);
            }
        };
    }

    public String getLine(int idx) {
        return (String) callJS("getLine", idx);
    }

    public int getLineCount() {
        return (int) callJS("getLineCount");
    }

    public int getLineNumber() {
        return (int) callJS("getLineNumber");
    }

    @Override
    public TextEditorSettings getSettings() {
        TextEditorSettings setting = new TextEditorSettings();
        setting.setTheme(getTheme());
        setting.setFontSize(getFontSize());
        setting.setShowLineNumbers(isShowLineNumbers());
        setting.setHighlightCurrentLine(isHighlightCurrentLine());
        return setting;
    }

    @Override
    public void apply(EditorSettings settings) {
        TextEditorSettings s = (TextEditorSettings) settings;
        setTheme(s.getTheme());
        setFontSize(s.getFontSize());
        setShowLineNumbers(s.isShowLineNumbers());
        setHighlightCurrentLine(s.isHighlightCurrentLine());
    }

    @Override
    public TextEditorSettingsDialog settingsDialog() {
        return new TextEditorSettingsDialog(this);
    }

}
