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
