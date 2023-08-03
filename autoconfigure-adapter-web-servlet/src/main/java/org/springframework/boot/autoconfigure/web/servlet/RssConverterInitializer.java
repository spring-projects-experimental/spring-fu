package org.springframework.boot.autoconfigure.web.servlet;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;

public class RssConverterInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean("rssChannelHttpMessageConverter", HttpMessageConverter.class, RssChannelHttpMessageConverter::new);
	}
}
