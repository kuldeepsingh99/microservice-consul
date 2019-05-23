package com.portal.gateway.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.portal.gateway.bean.AuthResponse;
import com.portal.gateway.bean.LoginRequest;
import com.portal.gateway.bean.RegisterUserRequest;
import com.portal.gateway.bean.User;
import com.portal.gateway.service.LoginService;

/**
 * Login Controller
 * @author Kuldeep
 *
 */
@Controller
@RequestMapping("/api")
public class LoginController {

	private static final Logger LOG = Logger.getLogger(LoginController.class.getName());
	
	@Autowired
    private LoginService loginService;

	/**
	 * Sign in to generate a new token
	 * @param loginRequest LoginRequest
	 * @return
	 */
    @CrossOrigin("*")
    @PostMapping("/signin")
    @ResponseBody
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
    	LOG.info("Inside login");
        String token = loginService.login(loginRequest.getUsername(),loginRequest.getPassword());
        HttpHeaders headers = new HttpHeaders();
        List<String> headerlist = new ArrayList<>();
        List<String> exposeList = new ArrayList<>();
        headerlist.add("Content-Type");
        headerlist.add(" Accept");
        headerlist.add("X-Requested-With");
        headerlist.add("Authorization");
        headers.setAccessControlAllowHeaders(headerlist);
        exposeList.add("Authorization");
        headers.setAccessControlExposeHeaders(exposeList);
        headers.set("Authorization", token);
        return new ResponseEntity<AuthResponse>(new AuthResponse(token), headers, HttpStatus.CREATED);
    }
    
    /**
     * Sign out
     * @param token JWT Token
     * @return
     */
    @CrossOrigin("*")
    @PostMapping("/signout")
    @ResponseBody
    public ResponseEntity<AuthResponse> logout (@RequestHeader(value="Authorization") String token) {
        HttpHeaders headers = new HttpHeaders();
      if (loginService.logout(token)) {
          headers.remove("Authorization");
          return new ResponseEntity<AuthResponse>(new AuthResponse("logged out"), headers, HttpStatus.CREATED);
      }
        return new ResponseEntity<AuthResponse>(new AuthResponse("Logout Failed"), headers, HttpStatus.NOT_MODIFIED);
    }

    /**
     * This method generates a new token
     * @param token JWT Token
     * @return
     */
    @PostMapping("/signin/token")
    @CrossOrigin("*")
    @ResponseBody
    public ResponseEntity<AuthResponse> createNewToken (@RequestHeader(value="Authorization") String token) {
        String newToken = loginService.createNewToken(token);
        HttpHeaders headers = new HttpHeaders();
        List<String> headerList = new ArrayList<>();
        List<String> exposeList = new ArrayList<>();
        headerList.add("Content-Type");
        headerList.add(" Accept");
        headerList.add("X-Requested-With");
        headerList.add("Authorization");
        headers.setAccessControlAllowHeaders(headerList);
        exposeList.add("Authorization");
        headers.setAccessControlExposeHeaders(exposeList);
        headers.set("Authorization", newToken);
        return new ResponseEntity<AuthResponse>(new AuthResponse(newToken), headers, HttpStatus.CREATED);
    }
    
    
	@CrossOrigin("*")
	@PostMapping("/register")
	@ResponseBody
	public User createUser(@RequestBody RegisterUserRequest registerBean) {
		User user = new User();
		user.setEmail(registerBean.getEmail());
		user.setFirstName(registerBean.getFirstName());
		user.setLastName(registerBean.getLastName());
		user.setPassword(registerBean.getPassword());
		user.setRole("ROLE_"+registerBean.getRoleName());
		
		User newUser = loginService.saveUser(user);

		return newUser;
	}
}
