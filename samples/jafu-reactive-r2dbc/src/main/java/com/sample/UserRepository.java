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
		return client.sql("SELECT COUNT(*) FROM users").fetch().one().cast(Long.class);
	}

	public Flux<User> findAll() {
		return client.sql("SELECT * FROM users").fetch().all().cast(User.class);
	}

	public Mono<User> findOne(String id) {
		return client.sql("SELECT * FROM users WHERE login = :login").bind("login", id).fetch().one().cast(User.class);
	}

	public Mono<Void> deleteAll() {
		return client.sql("DELETE FROM users").fetch().one().then();
	}

	public Mono<String> save(User user) {
		return client.sql("INSERT INTO users (login, firstname, lastname) VALUES(:login, :firstname, :lastname)")
				.bind("login", user.getLogin())
				.bind("firstname", user.getFirstname())
				.bind("lastname", user.getLastname())
				.fetch()
				.one()
				.cast(String.class);
	}

	public void init() {
		client.sql("CREATE TABLE IF NOT EXISTS users (login varchar PRIMARY KEY, firstname varchar, lastname varchar);")
				.then()
				.then(deleteAll())
				.then(save(new User("smaldini", "Stéphane","Maldini")))
				.then(save(new User("sdeleuze", "Sébastien","Deleuze")))
				.then(save(new User("bclozel", "Brian","Clozel")))
				.block();
	}
}
