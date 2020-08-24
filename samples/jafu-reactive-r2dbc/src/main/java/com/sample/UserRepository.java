package com.sample;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.r2dbc.core.R2dbcEntityOperations;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.empty;
import static org.springframework.data.relational.core.query.Query.query;

public class UserRepository {

	private final R2dbcEntityOperations operations;

	public UserRepository(R2dbcEntityOperations operations) {
		this.operations = operations;
	}

	public Mono<Long> count() {
		return operations.count(empty(), User.class);
	}

	public Flux<User> findAll() {
		return operations.select(empty(), User.class);
	}

	public Mono<User> findOne(String id) {
		return operations.select(User.class).matching(query(where("login").is(id))).one();
	}

	public Mono<Void> deleteAll() {
		return operations.delete(User.class).all().then();
	}

	public Mono<User> insert(User user) {
		return operations.insert(User.class).using(user);
	}
}
