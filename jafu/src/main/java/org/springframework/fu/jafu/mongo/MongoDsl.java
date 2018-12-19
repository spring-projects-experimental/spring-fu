package org.springframework.fu.jafu.mongo;

import java.util.function.Consumer;

import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;

import org.springframework.boot.autoconfigure.data.mongo.MongoDataInitializer;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataInitializer;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveInitializer;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoInitializer;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

/**
 * Jafu DSL for MongoDB configuration.
 *
 * Enable and configure Reactive MongoDB support by registering a {@link org.springframework.data.mongodb.core.ReactiveMongoTemplate} bean.
 *
 * Required dependencies can be retrieve using {@code org.springframework.boot:spring-boot-starter-data-mongodb-reactive}.
 *
 * @author Sebastien Deleuze
 */
public class MongoDsl extends AbstractDsl {

	private final Consumer<MongoDsl> dsl;

	private final MongoProperties properties = new MongoProperties();

	private boolean embedded = false;

	MongoDsl(Consumer<MongoDsl> dsl) {
		this.dsl = dsl;
	}

	/**
	 * Configure Reactive MongoDB support with default properties.
	 * @see org.springframework.fu.jafu.ConfigurationDsl#enable(ApplicationContextInitializer)
	 * @see org.springframework.data.mongodb.core.ReactiveMongoTemplate
	 */
	public static ApplicationContextInitializer<GenericApplicationContext> mongo() {
		return new MongoDsl(mongoDsl -> {});
	}

	/**
	 * Configure Reactive MongoDB support with customized properties.
	 * @see org.springframework.fu.jafu.ConfigurationDsl#enable(ApplicationContextInitializer)
	 * @see org.springframework.data.mongodb.core.ReactiveMongoTemplate
	 */
	public static ApplicationContextInitializer<GenericApplicationContext> mongo(Consumer<MongoDsl> dsl) {
		return new MongoDsl(dsl);
	}

	/**
	 * Configure the database uri. By default set to `mongodb://localhost/test`.
	 */
	public MongoDsl uri(String uri) {
		properties.setUri(uri);
		return this;
	}

	/**
	 * Enable MongoDB embedded server with default properties.
	 *
	 * Require {@code de.flapdoodle.embed:de.flapdoodle.embed.mongo} dependency.
	 */
	public MongoDsl embedded() {
		new EmbeddedMongoDsl(properties, it -> {}).initialize(context);
		embedded = true;
		return this;
	}

	/**
	 * Enable MongoDB embedded server with customized properties.
	 *
	 * Require {@code de.flapdoodle.embed:de.flapdoodle.embed.mongo} dependency.
	 */
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
			properties.setUri(MongoProperties.DEFAULT_URI);
		}
		new MongoDataInitializer(properties).initialize(context);
		new MongoReactiveDataInitializer(properties).initialize(context);
		new MongoReactiveInitializer(properties, embedded).initialize(context);
	}

	/**
	 * Jafu DSL for embedded MongoDB configuration.
	 */
	public static class EmbeddedMongoDsl extends AbstractDsl {

		private final Consumer<EmbeddedMongoDsl> dsl;

		private final MongoProperties mongoProperties;
		private final EmbeddedMongoProperties embeddedMongoProperties = new EmbeddedMongoProperties();

		EmbeddedMongoDsl(MongoProperties properties, Consumer<EmbeddedMongoDsl> dsl) {
			this.dsl = dsl;
			this.mongoProperties = properties;
		}

		/**
		 * Version of Mongo to use
		 */
		public EmbeddedMongoDsl version(IFeatureAwareVersion version) {
			this.embeddedMongoProperties.setVersion(version.asInDownloadPath());
			return this;
		}

		@Override
		public void initialize(GenericApplicationContext context) {
			super.initialize(context);
			this.dsl.accept(this);
			new EmbeddedMongoInitializer(mongoProperties, embeddedMongoProperties).initialize(context);
		}

	}
}
