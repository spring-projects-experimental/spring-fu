package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

/**
 * {@link ApplicationContextInitializer} adapter for {@link RedisReactiveAutoConfiguration}
 */
public class RedisReactiveInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
    @Override
    public void initialize(GenericApplicationContext context) {
        context.registerBeanDefinition("reactiveRedisTemplate", getReactiveRedisTemplateBeanDefinition(context));
        context.registerBean("reactiveStringRedisTemplate", ReactiveStringRedisTemplate.class, () -> getReactiveStringRedisTemplate(context));
    }

    private RootBeanDefinition getReactiveRedisTemplateBeanDefinition(GenericApplicationContext context) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ReactiveRedisTemplate.class, () -> getReactiveRedisTemplate(context));
        beanDefinition.setTargetType(ResolvableType.forClassWithGenerics(ReactiveRedisTemplate.class, Object.class, Object.class));
        return beanDefinition;
    }

    private ReactiveStringRedisTemplate getReactiveStringRedisTemplate(GenericApplicationContext context) {
        RedisReactiveAutoConfiguration configuration = new RedisReactiveAutoConfiguration();
        return configuration.reactiveStringRedisTemplate(context.getBean(ReactiveRedisConnectionFactory.class));
    }

    private ReactiveRedisTemplate<Object, Object> getReactiveRedisTemplate(GenericApplicationContext context) {
        RedisReactiveAutoConfiguration configuration = new RedisReactiveAutoConfiguration();

        return configuration.reactiveRedisTemplate(context.getBean(ReactiveRedisConnectionFactory.class), context.getBean(ResourceLoader.class));
    }
}
