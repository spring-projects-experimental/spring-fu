package org.springframework.boot.autoconfigure.data.r2dbc;

import java.time.Duration;

public class MssqlR2dbcProperties {

	private String host;

	private Integer port;

	private String database;

	private String username;

	private String password;

	private boolean preferCursoredExecution;

	private Duration connectTimeout;

	private boolean ssl;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
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

	public boolean isPreferCursoredExecution() {
		return preferCursoredExecution;
	}

	public void setPreferCursoredExecution(boolean preferCursoredExecution) {
		this.preferCursoredExecution = preferCursoredExecution;
	}

	public Duration getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(Duration connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}
}
