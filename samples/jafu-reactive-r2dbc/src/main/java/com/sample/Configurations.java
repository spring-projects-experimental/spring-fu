package com.sample;

import java.util.function.Consumer;

import org.springframework.fu.jafu.ConfigurationDsl;

import static org.springframework.fu.jafu.r2dbc.H2R2dbcDsl.r2dbcH2;
import static org.springframework.fu.jafu.webflux.WebFluxServerDsl.webFlux;

public abstract class Configurations {

	public static Consumer<ConfigurationDsl> dataConfig = conf -> conf
			.beans(b -> b.bean(UserRepository.class)).enable(r2dbcH2());

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
						.router(r -> r.GET("/", conf.ref(UserHandler.class)::listApi))
						.codecs(codecs -> codecs.string().jackson());
			}));
}
