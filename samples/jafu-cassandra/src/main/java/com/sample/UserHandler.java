package com.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class UserHandler {

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

    private final UserRepository repository;

    public UserHandler(UserRepository repository) {
        this.repository = repository;
    }

    public ServerResponse users(ServerRequest request) {
        log.info("retrieving all users");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(repository.findAll());
    }

    public ServerResponse save(ServerRequest request) throws ServletException, IOException {
        log.info("saving a user");
        final User save = repository.save(request.body(User.class));
        return ServerResponse.created(URI.create("/" + save.getId())).body(save);
    }

    public ServerResponse findOne(UUID id) {
        log.info("finding a user with id: {}", id);
        return repository.fineOne(id).map(u ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(u)
        ).orElse(ServerResponse.notFound().build());
    }
}
