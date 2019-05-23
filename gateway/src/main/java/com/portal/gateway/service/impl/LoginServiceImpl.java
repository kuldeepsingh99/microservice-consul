package com.portal.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.portal.gateway.bean.JwtToken;
import com.portal.gateway.bean.User;
import com.portal.gateway.exception.CustomException;
import com.portal.gateway.repository.JwtTokenRepository;
import com.portal.gateway.repository.UserRepository;
import com.portal.gateway.security.JwtTokenProvider;
import com.portal.gateway.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtTokenRepository jwtTokenRepository;

	@Override
	public String login(String username, String password) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			User user = userRepository.findByEmail(username);
			if (user == null || user.getRole() == null || user.getRole().isEmpty()) {
				throw new CustomException("Invalid username or password.", HttpStatus.UNAUTHORIZED);
			}
			// NOTE: normally we dont need to add "ROLE_" prefix. Spring does automatically
			// for us.
			// Since we are using custom token using JWT we should add ROLE_ prefix
			String token = jwtTokenProvider.createToken(username, user.getRole());
			return token;

		} catch (AuthenticationException e) {
			throw new CustomException("Invalid username or password.", HttpStatus.UNAUTHORIZED);
		}
	}

	@Override
	public User saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public boolean logout(String token) {
		jwtTokenRepository.delete(new JwtToken(token));
		return true;
	}

	@Override
	public Boolean isValidToken(String token) {
		return jwtTokenProvider.validateToken(token);
	}

	@Override
	public String createNewToken(String token) {
		String username = jwtTokenProvider.getUsername(token);
		String roleName = jwtTokenProvider.getRoleList(token);
		String newToken = jwtTokenProvider.createToken(username, roleName);
		return newToken;
	}

}
