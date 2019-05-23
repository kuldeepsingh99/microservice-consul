package com.portal.gateway.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * JWT Token Entity
 * @author Kuldeep
 *
 */
@Document
public class JwtToken {

	@Id
    private String token;

	public JwtToken(String token) {
        this.token = token;
    }
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
