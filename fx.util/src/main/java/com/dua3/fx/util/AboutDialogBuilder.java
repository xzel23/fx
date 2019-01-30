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

package com.dua3.fx.util;

import java.util.Objects;

/** 
 * Builder for Alert Dialogs.
 * 
 * Provides a fluent interface to create Alerts. 
 */
public class AboutDialogBuilder {
	private String title = "";
	private String name = "";
	private String copyright = "";
	private String version = "";
	private String mailText = "";
	private String mailAddress = "";

	AboutDialogBuilder() {
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

	public AboutDialog build() {
		AboutDialog dlg = new AboutDialog();
		
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
		
		return dlg;
	}
	
	public void showAndWait() {
		build().showAndWait();
	}
}
