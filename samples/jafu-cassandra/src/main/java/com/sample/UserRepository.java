package com.sample;

import org.springframework.data.cassandra.core.CassandraOperations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepository {

    private final CassandraOperations operations;

    public UserRepository(CassandraOperations operations) {
        this.operations = operations;
    }

    public List<User> findAll() {
        return operations.select("select * from users", User.class);
    }

    public User save(User user) {
        user.setId(UUID.randomUUID());
       return operations.insert(user);
    }

    public Optional<User> fineOne(UUID id) {
        return Optional.ofNullable(operations.selectOneById(id, User.class));
    }
}
