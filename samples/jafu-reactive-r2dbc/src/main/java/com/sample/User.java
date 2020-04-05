package com.sample;

public class User {

	private String login;

	private String firstname;

	private String lastname;

	public User() {
	}

	public User(String login, String firstname, String lastname) {
		this.login = login;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public String getLogin() {
		return login;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Override
	public String toString() {
		return "User{" +
				"login='" + login + '\'' +
				", firstname='" + firstname + '\'' +
				", lastname='" + lastname + '\'' +
				'}';
	}
}
