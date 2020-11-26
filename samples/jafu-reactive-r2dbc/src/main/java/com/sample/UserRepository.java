package com.sample;

import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class UserRepository {

	private final DatabaseClient client;

	public UserRepository(DatabaseClient client) {
		this.client = client;
	}

	public Mono<Long> count() {
		return client.sql("SELECT count(login) from users")
				.map(row -> row.get(0, Long.class))
				.first();
	}

	public Flux<User> findAll() {
		return client.sql("SELECT login, firstname, lastname from users")
				.map(row ->
						new User(
								row.get("login", String.class),
								row.get("firstname", String.class),
								row.get("lastname", String.class)))
				.all();
	}

	public Mono<User> findOne(String id) {
		return client.sql("SELECT login, firstname, lastname from users where login = :id")
				.bind("id", id)
				.map(row ->
						new User(
								row.get("login", String.class),
								row.get("firstname", String.class),
								row.get("lastname", String.class)))
				.first();
	}

	public Mono<Void> deleteAll() {
		return client.sql("DELETE FROM users").then();
	}

	public Mono<Void> insert(User user) {
		return client.sql("INSERT INTO users(login, firstname, lastname) values(:login, :firstname, :lastname)")
				.bind("login", user.getLogin())
				.bind("firstname", user.getFirstname())
				.bind("lastname", user.getLastname())
				.then();
	}
}
