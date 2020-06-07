package com.sample;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.fu.jafu.ConfigurationDsl;
import org.springframework.fu.jafu.mongo.ReactiveMongoDsl;

import java.io.IOException;
import java.util.function.Consumer;

import static org.springframework.fu.jafu.webflux.WebFluxServerDsl.webFlux;

public abstract class Configurations {

    public static Consumer<ConfigurationDsl> dataConfig = conf -> conf
            .beans(b -> b.bean(UserRepository.class))
            .listener(ApplicationReadyEvent.class, e -> {
                try {
                    conf.ref(UserRepository.class).init();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            })
            .enable(ReactiveMongoDsl.reactiveMongo(ReactiveMongoDsl::embedded));


    public static Consumer<ConfigurationDsl> webConfig = conf -> conf
            .beans(beans -> beans.bean(UserHandler.class))
            .enable(webFlux(server -> {
                if (conf.profiles().contains("test")) {
                    server.port(8181);
                }
                else {
                    server.port(8080);
                }
                server
                        .router(r -> {
                            r
                                    .GET("/", conf.ref(UserHandler.class)::listView)
                                    .GET("/api/users", conf.ref(UserHandler.class)::listApi)
                                    .GET("/conf", conf.ref(UserHandler.class)::conf);
                        })
                        .mustache()
                        .codecs(codecs -> codecs.string().jackson());
            }));
}
