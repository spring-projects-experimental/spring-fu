package com.sample;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.function.DatabaseClient;

public class UserRepository {

	private final DatabaseClient client;

	private final ObjectMapper objectMapper;

	public UserRepository(DatabaseClient client, ObjectMapper objectMapper) {
		this.client = client;
		this.objectMapper = objectMapper;
	}

	public Mono<Integer> count() {
		return client.execute().sql("SELECT COUNT(*) FROM users").as(Integer.class).fetch().one();
	}

	public Flux<User> findAll() {
		return client.select().from("users").as(User.class).fetch().all();
	}

	public Mono<User> findOne(String id) {
		return client.execute().sql("SELECT * FROM users WHERE login = $1").bind(1, id).as(User.class).fetch().one();
	}

	public Mono<Void> deleteAll() {
		return client.execute().sql("DELETE FROM users").fetch().one().then();
	}

	public Mono<String> save(User user) {
		return client.insert().into(User.class).table("users").using(user).exchange()
				.flatMap(it -> it.extract((r, m) -> r.get("login", String.class)).one());
	}

	public void init() {
			client.execute().sql("CREATE TABLE IF NOT EXISTS users (login varchar PRIMARY KEY, firstname varchar, lastname varchar);").fetch().one().block();
			deleteAll().block();
			ClassPathResource eventsResource = new ClassPathResource("data/users.json");
			try {
				List<User> users = this.objectMapper.readValue(eventsResource.getInputStream(), new TypeReference<List<User>>() {});
				for (User user : users) {
					save(user).subscribe();
				}
			} catch(IOException ex) { }
	}
}
