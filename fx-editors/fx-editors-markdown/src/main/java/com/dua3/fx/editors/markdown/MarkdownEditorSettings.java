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

import com.dua3.fx.editors.EditorSettings;

import java.util.prefs.Preferences;

public class MarkdownEditorSettings implements EditorSettings {

	public MarkdownEditorSettings() {
	}

	public static MarkdownEditorSettings copyOf(MarkdownEditorSettings other) {
		MarkdownEditorSettings inst = new MarkdownEditorSettings();
		inst.assign(other);
		return inst;
	}

	public static MarkdownEditorSettings fromPreference(Preferences node) {
		MarkdownEditorSettings cs = new MarkdownEditorSettings();
		cs.load(node);
		return cs;
	}

	@Override
	public void load(Preferences node) {
	}

	@Override
	public void store(Preferences node) {
	}

	public void assign(EditorSettings other) {
		MarkdownEditorSettings s = (MarkdownEditorSettings) other;
	}

}