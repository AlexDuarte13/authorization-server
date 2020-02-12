package com.ebix.easi.authorizationserver.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

public class LoginDTO implements Serializable {

	private static final long serialVersionUID = -8840711663139038503L;

	@NotBlank
	private String username;
	@NotBlank
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
