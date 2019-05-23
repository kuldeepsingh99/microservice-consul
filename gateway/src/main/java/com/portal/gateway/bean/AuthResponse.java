package com.portal.gateway.bean;

/**
 * Auth Response Bean
 * @author Kuldeep
 *
 */
public class AuthResponse {

	private String accessToken;

	public AuthResponse(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

}
