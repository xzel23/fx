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
import com.dua3.fx.editors.EditorSettings;

import java.util.logging.Logger;

public class MarkdownEditor extends EditorBase {
	/** Logger */
    private static final Logger LOG = Logger.getLogger(MarkdownEditor.class.getName());

    /**
     * Default constructor.
     */
	public MarkdownEditor() {
		super(MarkdownEditor.class.getResource("editor.fxml"),
			  MarkdownEditor.class.getResource("editor.html"));
	}

	@Override
	public MarkdownEditorSettings getSettings() {
		MarkdownEditorSettings setting = new MarkdownEditorSettings();
		return setting;
	}

	@Override
	public void apply(EditorSettings settings) {
	}

	@Override
	public void cut() {

	}

	@Override
	public void copy() {

	}

	@Override
	public void paste() {

	}

	@Override
	public int getLineCount() {
		return 0;
	}

	@Override
	public int getLineNumber() {
		return 0;
	}

	@Override
	public String getLine(int idx) {
		return null;
	}

	@Override
	public void addLine(String s) {

	}

	@Override
	public void setLine(int i, String s) {

	}

	@Override
	public void search() {

	}

	public MarkdownEditorSettingsDialog settingsDialog() {
		return new MarkdownEditorSettingsDialog(this);
	}
}
