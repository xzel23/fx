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

package com.dua3.fx.util.controls;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts. 
 */
public class AboutDialogBuilder {
	private static Logger LOG = Logger.getLogger(AboutDialogBuilder.class.getName());
	
	private String title = "";
	private String name = "";
	private String copyright = "";
	private String version = "";
	private String mailText = "";
	private String mailAddress = "";
	
	private String css = null;
	private Node graphic = null;
	private Node expandableContent = null;

	public AboutDialogBuilder() {
	}
	
	public AboutDialogBuilder title(String value) {
		this.title = Objects.requireNonNull(value);
		return this;
	}

	public AboutDialogBuilder name(String value) {
		this.name = Objects.requireNonNull(value);
		return this;
	}

	public AboutDialogBuilder version(String value) {
		this.version = Objects.requireNonNull(value);
		return this;
	}

	public AboutDialogBuilder copyright(String value) {
		this.copyright = Objects.requireNonNull(value);
		return this;
	}

	public AboutDialogBuilder mail(String address) {
		this.mailText = address;
		this.mailAddress = "mailto:"+address;
		return this;
	}

	public AboutDialogBuilder mail(String text, String mailtoUri) {
		this.mailText = text;
		this.mailAddress = mailtoUri;
		return this;
	}

	/**
	 * Set supplemental CSS.
	 * @param css the name of the CSS resource to load ({@link URL#toExternalForm()}
	 * @return this
	 */
	public AboutDialogBuilder css(String css) {
		this.css = css;
		return this;
	}

	public AboutDialogBuilder graphic(URL url) {
		if (url==null) {
			this.graphic = null;
			return this;
		}

		try (var in = url.openStream()) {
			Image image = new Image(in);
			graphic(new javafx.scene.image.ImageView(image));
		} catch (IOException e) {
			LOG.log(Level.WARNING, "could not read image: "+url, e);
			this.graphic = null;
		}
		return this;
	}

	public AboutDialogBuilder graphic(Node graphic) {
		this.graphic = graphic;
		return this;
	}

	public AboutDialogBuilder expandableContent(Node c) {
		this.expandableContent = c;
		return this;
	}

	public AboutDialogBuilder expandableContent(String text) {
		if (text==null || text.isBlank()) {
			expandableContent = null;
			return this;
		}
		
		this.expandableContent = new StackPane(new Text(text));
		return this;
	}

	public AboutDialog build() {
		AboutDialog dlg = new AboutDialog();
		
		if(css!=null) {
			dlg.getDialogPane().getScene().getStylesheets().add(css);
		}
		if (graphic!=null) {
			dlg.setGraphic(graphic);
		}
		if (!title.isBlank()) {
			dlg.setTitle(title);
		}
		if (!name.isBlank()) {
			dlg.setName(name);
		}
		if (!copyright.isBlank()) {
			dlg.setCopyright(copyright);
		}
		if (!version.isBlank()) {
			dlg.setVersion(version);
		}
		if (!mailText.isBlank()) {
			dlg.setEmailText(mailText);
		}
		if (mailAddress!=null) {
			dlg.setEmailAddress(mailAddress);
		}
		if (expandableContent!=null) {
			dlg.getDialogPane().setExpandableContent(expandableContent);
		}
		
		return dlg;
	}
	
	public void showAndWait() {
		build().showAndWait();
	}
}
