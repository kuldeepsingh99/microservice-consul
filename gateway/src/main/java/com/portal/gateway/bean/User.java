package com.portal.gateway.bean;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User Object
 * @author Kuldeep
 *
 */
@Document(collection = "user")
public class User {
	@Id
	private String id;
	
	@Email(message = "*Please provide a valid email")
	@NotEmpty(message = "*Please provide an email")
	private String email;
	
	@NotEmpty(message = "*Please provide password")
	private String password;
	
	@NotEmpty(message = "*Please provide your name")
	private String firstName;
	
	@NotEmpty(message = "*Please provide your last name")
	private String lastName;
	
	private Integer active = 1;
	private boolean isLoacked = false;
	private boolean isExpired = false;
	private boolean isEnabled = true;
	private String role;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Integer getActive() {
		return active;
	}
	public void setActive(Integer active) {
		this.active = active;
	}
	public boolean isLoacked() {
		return isLoacked;
	}
	public void setLoacked(boolean isLoacked) {
		this.isLoacked = isLoacked;
	}
	public boolean isExpired() {
		return isExpired;
	}
	public void setExpired(boolean isExpired) {
		this.isExpired = isExpired;
	}
	public boolean isEnabled() {
		return isEnabled;
	}
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

	
}
