package com.sample;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.r2dbc.core.DatabaseClient;


public class UserRepository {

	private final DatabaseClient client;

	public UserRepository(DatabaseClient client) {
		this.client = client;
	}

	public Mono<Long> count() {
		return client.execute("SELECT COUNT(*) FROM users").as(Long.class).fetch().one();
	}

	public Flux<User> findAll() {
		return client.select().from("users").as(User.class).fetch().all();
	}

	public Mono<User> findOne(String id) {
		return client.execute("SELECT * FROM users WHERE login = :login").bind("login", id).as(User.class).fetch().one();
	}

	public Mono<Void> deleteAll() {
		return client.execute("DELETE FROM users").fetch().one().then();
	}

	public Mono<String> save(User user) {
		return client.insert().into(User.class).table("users").using(user)
				.map((r, m) -> r.get("login", String.class)).one();
	}

	public void init() {
		client.execute("CREATE TABLE IF NOT EXISTS users (login varchar PRIMARY KEY, firstname varchar, lastname varchar);").then()
				.then(deleteAll())
				.then(save(new User("smaldini", "Stéphane","Maldini")))
				.then(save(new User("sdeleuze", "Sébastien","Deleuze")))
				.then(save(new User("bclozel", "Brian","Clozel")))
				.block();
	}
}
