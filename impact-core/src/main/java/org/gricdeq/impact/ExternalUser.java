package org.gricdeq.impact;

import java.util.ArrayList;
import java.util.List;

public class ExternalUser {
	
	private String userName;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private List<ExternalRole> roles = new ArrayList<ExternalRole>(0);

	public ExternalUser() {
		super();
	}

	public ExternalUser(String userName) {
		super();
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<ExternalRole> getRoles() {
		return roles;
	}

	public void setRoles(List<ExternalRole> roles) {
		this.roles = roles;
	}

	
}
