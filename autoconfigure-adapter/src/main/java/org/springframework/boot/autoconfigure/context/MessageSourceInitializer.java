package org.springframework.boot.autoconfigure.context;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.MessageSource;
import org.springframework.context.support.GenericApplicationContext;

public class MessageSourceInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private final MessageSourceProperties properties;

	public MessageSourceInitializer() {
		this.properties = new MessageSourceProperties();
	}

	public MessageSourceInitializer(MessageSourceProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean(MessageSource.class, () -> new MessageSourceAutoConfiguration().messageSource(this.properties));
	}
}
