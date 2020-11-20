package com.sample;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.fu.jafu.ConfigurationDsl;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import java.util.function.Consumer;

import static org.springframework.fu.jafu.r2dbc.DataR2dbcDsl.dataR2dbc;
import static org.springframework.fu.jafu.webflux.WebFluxServerDsl.webFlux;

public abstract class Configurations {

	public static Consumer<ConfigurationDsl> dataConfig = conf -> conf
			.beans(b -> b.bean(UserRepository.class).bean(ConnectionFactoryInitializer.class, () -> {
				ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
				initializer.setConnectionFactory(b.ref(ConnectionFactory.class));
				initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("db/tables.sql")));
				return initializer;
			})).enable(dataR2dbc(dataR2dbcDsl -> dataR2dbcDsl.r2dbc(dsl -> dsl.url("r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1"))));

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
							r.GET("/", conf.ref(UserHandler.class)::listApi);
							r.GET("/api/user", conf.ref(UserHandler.class)::listApi);
							r.GET("/api/user/{login}", conf.ref(UserHandler.class)::userApi);
						})
						.codecs(codecs -> codecs.string().jackson());
			}));
}
