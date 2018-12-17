package org.springframework.fu.jafu.mongo;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.data.mongo.MongoDataInitializer;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataInitializer;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveInitializer;
import org.springframework.context.ApplicationContextInitializer;
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

	MongoDsl(Consumer<MongoDsl> dsl) {
		this.dsl = dsl;
	}

	public static ApplicationContextInitializer<GenericApplicationContext> mongo() {
		return new MongoDsl(mongoDsl -> {});
	}

	public static ApplicationContextInitializer<GenericApplicationContext> mongo(Consumer<MongoDsl> dsl) {
		return new MongoDsl(dsl);
	}

	public MongoDsl uri(String uri) {
		properties.setUri(uri);
		return this;
	}

	public MongoDsl embedded() {
		new EmbeddedMongoDsl(properties, it -> {}).initialize(context);
		embedded = true;
		return this;
	}

	public MongoDsl embedded(Consumer<EmbeddedMongoDsl> dsl) {
		new EmbeddedMongoDsl(properties, dsl).initialize(context);
		embedded = true;
		return this;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		super.initialize(context);
		this.dsl.accept(this);
		if (properties.getUri() == null) {
			properties.setUri("mongodb://localhost/test");
		}
		new MongoDataInitializer(properties).initialize(context);
		new MongoReactiveDataInitializer(properties).initialize(context);
		new MongoReactiveInitializer(properties, embedded).initialize(context);
	}
}
