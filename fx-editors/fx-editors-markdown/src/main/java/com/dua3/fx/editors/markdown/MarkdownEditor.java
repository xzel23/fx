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

import com.dua3.fx.controls.ControlsUtil;
import com.dua3.fx.editors.EditorBase;
import com.dua3.fx.editors.EditorSettings;
import com.dua3.utility.json.JsonUtil;
import javafx.scene.Node;
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
	public Node[] toolbarControls() {
		URL url = MarkdownEditor.class.getResource("commands.json");
		try {
			JSONArray jsa = JsonUtil.read(url).getJSONArray("commands");

			List<Control> list = new LinkedList<>();
			for (var it: jsa) {
				if (it instanceof JSONObject) {
					JSONObject jso = (JSONObject) it;

					String id = jso.getString("id");
					Button button = new Button();

					JSONObject buttonDef = jso.getJSONObject("button");
					if (buttonDef.has("text")) {
						button.setText(buttonDef.getString("text"));
					}
					if (buttonDef.has("tooltip")) {
						button.setTooltip(new Tooltip(buttonDef.getString("tooltip")));
					}
					if (buttonDef.has("graphic")) {
						Node graphic = ControlsUtil.iconFromName(buttonDef.getString("graphic"));
						button.setGraphic(graphic);
					}

					JSONObject command = jso.getJSONObject("command");
					String function = command.getString("function");
					final Object[] args;
					if (command.has("args")) {
						JSONArray jsargs = command.getJSONArray("args");
						args = new Object[jsargs.length()];
						for (int i = 0; i < jsargs.length(); i++) {
							args[i] = jsargs.get(i);
						}
					} else {
						args = new Object[0];
					}

					button.setOnAction(evt -> callJS(function, args));

					list.add(button);
				} else if (it instanceof String) {
					String text = String.valueOf(it);
					switch (text) {
						case "---":
							list.add(new Separator());
							break;
						default:
							LOG.warning(String.format("[in %s] ignoring invalid entry: %s", url, text));
							break;
					}
				}
			}
			return list.toArray(Node[]::new);
		} catch (IOException e) {
			// should not happen
			throw new IllegalStateException(e);
		} catch (JSONException e) {
			LOG.warning("could not load button data");
			throw new IllegalStateException(e);
		}
	}

}
