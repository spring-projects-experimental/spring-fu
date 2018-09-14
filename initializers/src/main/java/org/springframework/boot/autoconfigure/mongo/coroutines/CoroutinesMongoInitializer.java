package org.springframework.boot.autoconfigure.mongo.coroutines;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.fu.coroutines.mongodb.data.mongodb.core.CoroutinesMongoTemplate;

public class CoroutinesMongoInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean(CoroutinesMongoTemplate.class, () -> new CoroutinesMongoTemplate(context.getBean(ReactiveMongoOperations.class)));
	}
}
