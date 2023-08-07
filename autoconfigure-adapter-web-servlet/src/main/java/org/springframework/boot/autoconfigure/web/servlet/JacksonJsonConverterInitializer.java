package org.springframework.boot.autoconfigure.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public class JacksonJsonConverterInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    @Override
    public void initialize(GenericApplicationContext context) {
        context.registerBean(
            "mappingJackson2HttpMessageConverter",
            HttpMessageConverter.class,
            () -> new MappingJackson2HttpMessageConverter(context.getBean(ObjectMapper.class))
        );
    }
}
