package org.springframework.boot.autoconfigure.web.reactive;

import java.util.Arrays;

import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientCodecCustomizer;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.codec.CodecConfigurer;

public abstract class AbstractCodecInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	protected final boolean isClientCodec;

	public AbstractCodecInitializer(boolean isClientCodec) {
		this.isClientCodec = isClientCodec;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		if (isClientCodec) {
			context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(WebClientCodecCustomizer.class.getName(), context), WebClientCodecCustomizer.class, () -> new WebClientCodecCustomizer(Arrays.asList((CodecCustomizer) configurer -> register(context, configurer))));
		}
		else {
			context.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(CodecCustomizer.class.getName(), context), CodecCustomizer.class, () -> configurer -> register(context, configurer));
		}
	}

	protected abstract void register(GenericApplicationContext context, CodecConfigurer configurer);
}
