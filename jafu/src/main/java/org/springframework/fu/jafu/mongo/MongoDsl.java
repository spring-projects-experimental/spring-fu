package org.springframework.fu.jafu.mongo;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.data.mongo.MongoDataInitializer;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataInitializer;
import org.springframework.boot.autoconfigure.data.r2dbc.DatabaseClientInitializer;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcProperties;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

/**
 * Jafu DSL for MongoDB configuration.
 * @author Sebastien Deleuze
 */
public class MongoDsl extends AbstractDsl {

	private final Consumer<MongoDsl> dsl;

	private final MongoProperties properties = new MongoProperties();

	private boolean embedded = false;

	public MongoDsl(Consumer<MongoDsl> dsl) {
		this.dsl = dsl;
	}

	public MongoDsl uri(String uri) {
		properties.setUri(uri);
		return this;
	}

	public MongoDsl embedded() {
		addInitializer(new EmbeddedMongoDsl(properties, it -> {}));
		embedded = true;
		return this;
	}

	public MongoDsl embedded(Consumer<EmbeddedMongoDsl> dsl) {
		addInitializer(new EmbeddedMongoDsl(properties, dsl));
		embedded = true;
		return this;
	}

	@Override
	public void register(GenericApplicationContext context) {
		this.dsl.accept(this);

		if (properties.getUri() == null) {
			properties.setUri("mongodb://localhost/test");
		}

		new MongoDataInitializer(properties).initialize(context);
		new MongoReactiveDataInitializer(properties).initialize(context);
		new MongoReactiveInitializer(properties, embedded).initialize(context);
	}
}
