package org.springframework.boot.autoconfigure.web.servlet;

import java.util.function.Supplier;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;

public class ResourceConverterInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean("resourceHttpMessageConverter", HttpMessageConverter.class, (Supplier<HttpMessageConverter>) ResourceHttpMessageConverter::new);
		context.registerBean("resourceRegionHttpMessageConverter", HttpMessageConverter.class, ResourceRegionHttpMessageConverter::new);
	}
}
