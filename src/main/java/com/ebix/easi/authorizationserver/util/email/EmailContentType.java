package com.ebix.easi.authorizationserver.util.email;

public enum EmailContentType {
	
	TEXT_HTML_UTF8("text/html; charset=UTF-8");
	
	private String value;
	
	private EmailContentType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
