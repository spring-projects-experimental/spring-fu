package org.springframework.boot.autoconfigure.jackson;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.ServerSentEventHttpMessageWriter;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

/**
 * {@link ApplicationContextInitializer} adapter for registering Jackson JSON codecs.
 */
public class JacksonJsonReactiveWebInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean(BeanPostProcessor.class, () -> new BeanPostProcessor() {

			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				if (bean instanceof CodecConfigurer) {
					CodecConfigurer.CustomCodecs codecs = ((CodecConfigurer)bean).customCodecs();
					ObjectMapper mapper = context.getBean(ObjectMapper.class);
					Jackson2JsonEncoder encoder = new Jackson2JsonEncoder(mapper);
					codecs.decoder(new Jackson2JsonDecoder(mapper));
					codecs.encoder(encoder);
					codecs.writer(new ServerSentEventHttpMessageWriter(encoder));
				}
				return bean;
			}
		});
	}
}
