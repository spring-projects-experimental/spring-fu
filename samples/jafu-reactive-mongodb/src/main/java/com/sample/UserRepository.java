package com.sample;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveRemoveOperation;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public class UserRepository {

    private final ReactiveMongoOperations mongo;
    private final ObjectMapper objectMapper;

    public UserRepository(ReactiveMongoOperations mongo, ObjectMapper objectMapper) {
        this.mongo = mongo;
        this.objectMapper = objectMapper;
    }

    public Mono<Long> count() {
        return mongo.count(new Query(), User.class);
    }

    public Flux<User> findAll() {
        return mongo.findAll(User.class);
    }

    public Mono<User> findOne(String id) {
       return mongo.findById(id, User.class);
    }

    public ReactiveRemoveOperation.ReactiveRemove<User> deleteAll() {
        return mongo.remove(User.class);
    }

    public Mono<User> save(User user) {
        return mongo.save(user);
    }

    public void init() throws IOException {
        var eventsResource = new ClassPathResource("data/users.json");
        List<User> users = objectMapper.readValue(eventsResource.getInputStream(), new TypeReference<List<User>>() {});
        users.forEach(user -> save(user).subscribe());
    }
}
