package com.sample;

import static org.springframework.fu.jafu.r2dbc.H2R2dbcDsl.r2dbcH2;
import static org.springframework.fu.jafu.web.WebFluxServerDsl.server;

import java.util.function.Consumer;

import org.springframework.fu.jafu.ConfigurationDsl;

public abstract class Configurations {

	public static Consumer<ConfigurationDsl> dataConfig = conf ->
			conf.beans(beans -> beans.bean(UserRepository.class)).enable(r2dbcH2());

	public static Consumer<ConfigurationDsl> webConfig = conf ->
			conf.beans(beans -> beans.bean(UserHandler.class))
			.enable(server(server -> {
				if (conf.profiles().contains("test")) {
					server.port(8181);
				}
				else {
					server.port(8080);
				}
				server.router(router -> {
					var userHandler = conf.ref(UserHandler.class);
					router.GET("/", userHandler::listApi);
				}).codecs(codecs -> codecs.string().jackson());
			}));
}
