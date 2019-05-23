package com.portal.gateway.service;

import com.portal.gateway.bean.User;

public interface LoginService {

	String login(String username, String password);
	
    User saveUser(User user);

    boolean logout(String token);

    Boolean isValidToken(String token);

    String createNewToken(String token);
}
