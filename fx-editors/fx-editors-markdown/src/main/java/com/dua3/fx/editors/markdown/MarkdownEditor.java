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
import com.dua3.utility.json.JsonUtil;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
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

	public MarkdownEditorSettingsDialog settingsDialog() {
		return new MarkdownEditorSettingsDialog(this);
	}

	@Override
	public List<Control> toolbarControls() {
		URL url = MarkdownEditor.class.getResource("markdown.properties");
		try {
			JSONArray jsa = JsonUtil.read(url).getJSONArray("commands");

			List<Control> list = new LinkedList<>();
			for (var it: jsa) {
				if (it instanceof JSONObject) {
					JSONObject jso = (JSONObject) it;
					String id = jso.getString("id");
					String buttonText = jso.getString("button_text");
					String text = jso.getString("text");
					String command = jso.getString("command");
					JSONArray args = jso.getJSONArray("args");

					Button button = new Button(buttonText);
					button.setTooltip(new Tooltip(text));
					button.setOnAction(evt -> callJS(command, args));

					list.add(button);
				} else if (it instanceof String) {
					String text = String.valueOf(it);
					switch (text) {
						case "---":
							list.add(new Separator());
							break;
						default:
							LOG.warning(String.format("in file %s: ignoring invalid entry: %s", url, text));
							break;
					}
				}
			}
			return list;
		} catch (IOException e) {
			// should not happen
			throw new IllegalStateException(e);
		} catch (JSONException e) {
			LOG.warning("could not load button data");
			throw new IllegalStateException(e);
		}
	}

}
