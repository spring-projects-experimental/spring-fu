package com.sample;

import org.springframework.boot.logging.LogLevel;
import org.springframework.fu.jafu.ConfigurationDsl;
import org.springframework.fu.jafu.cassandra.CassandraDsl;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.springframework.fu.jafu.cassandra.CassandraDsl.cassandra;
import static org.springframework.fu.jafu.webmvc.WebMvcServerDsl.webMvc;

public class Configurations {

    static int port;

    private static final Consumer<CassandraDsl> cassandraProperties = properties -> properties
            .contactPoints(Collections.singletonList("127.0.0.1:" + port))
            .localDatacenter("datacenter1")
            .keyspaceName("test")
            .port(port);

    public static final Consumer<ConfigurationDsl> cassandraConfig = conf -> conf
            .beans(b -> b.bean(UserRepository.class)).enable(cassandra(cassandraProperties));

    public static final Consumer<ConfigurationDsl> webConfig = conf -> conf
            .beans(beans -> beans.bean(UserHandler.class))
            .logging(l -> l.level(LogLevel.INFO))
            .logging(l -> l.level("com.sample", LogLevel.DEBUG))
            .enable(webMvc(server -> {
                if (conf.profiles().contains("test")) {
                    server.port(8181);
                } else {
                    server.port(8080);
                }
                server.router(r -> r
                        .GET("/users", conf.ref(UserHandler.class)::users)
                        .POST("/users", conf.ref(UserHandler.class)::save)
                        .GET("/users/{id}", request ->
                            Optional.of(request.pathVariable("id"))
                                    .map(UUID::fromString)
                                    .map(conf.ref(UserHandler.class)::findOne)
                                    .orElseThrow(RuntimeException::new)
                        ))
                        .converters(c -> c.string().jackson());
            }));

}
