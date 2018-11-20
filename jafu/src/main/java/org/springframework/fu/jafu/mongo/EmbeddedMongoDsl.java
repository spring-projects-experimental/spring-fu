package org.springframework.fu.jafu.mongo;

import java.util.function.Consumer;

import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoInitializer;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

/**
 * Jafu DSL for embedded MongoDB configuration.
 */
public class EmbeddedMongoDsl extends AbstractDsl {

	private final Consumer<EmbeddedMongoDsl> dsl;

	private final MongoProperties mongoProperties;
	private final EmbeddedMongoProperties embeddedMongoProperties = new EmbeddedMongoProperties();

	public EmbeddedMongoDsl(MongoProperties properties, Consumer<EmbeddedMongoDsl> dsl) {
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
	public void register(GenericApplicationContext context) {
		this.dsl.accept(this);
		new EmbeddedMongoInitializer(mongoProperties, embeddedMongoProperties).initialize(context);
	}
}
