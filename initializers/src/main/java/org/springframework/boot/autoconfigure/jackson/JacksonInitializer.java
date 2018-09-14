package org.springframework.boot.autoconfigure.jackson;

import static org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.*;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * {@link ApplicationContextInitializer} adapter for {@link JacksonAutoConfiguration}.
 */
public class JacksonInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private JacksonProperties properties;

	public JacksonInitializer(JacksonProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean(Jackson2ObjectMapperBuilderCustomizer.class, () ->
				new Jackson2ObjectMapperBuilderCustomizerConfiguration().standardJacksonObjectMapperBuilderCustomizer(context, properties));
		context.registerBean(Jackson2ObjectMapperBuilder.class, () ->
				new JacksonObjectMapperBuilderConfiguration(context).jacksonObjectMapperBuilder(new ArrayList<>(context.getBeansOfType(Jackson2ObjectMapperBuilderCustomizer.class).values())));
		context.registerBean(ObjectMapper.class, () -> new JacksonObjectMapperConfiguration().jacksonObjectMapper(context.getBean(Jackson2ObjectMapperBuilder.class)));
	}
}
