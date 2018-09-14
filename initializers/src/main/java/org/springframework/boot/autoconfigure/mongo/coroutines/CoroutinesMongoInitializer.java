package org.springframework.boot.autoconfigure.mongo.coroutines;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.mongodb.core.CoroutinesMongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

public class CoroutinesMongoInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean(CoroutinesMongoTemplate.class, () -> new CoroutinesMongoTemplate(context.getBean(ReactiveMongoOperations.class)));
	}
}
