package org.springframework.boot.autoconfigure.data.r2dbc;

public class H2R2dbcProperties {

	private String url;

	private String username;

	private String password;

	private boolean coroutines;


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

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

	public boolean getCoroutines() {
		return coroutines;
	}

	public void setCoroutines(boolean coroutines) {
		this.coroutines = coroutines;
	}
}
