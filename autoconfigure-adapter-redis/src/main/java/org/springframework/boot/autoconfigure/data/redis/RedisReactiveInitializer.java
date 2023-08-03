package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

/**
 * {@link ApplicationContextInitializer} adapter for {@link RedisReactiveAutoConfiguration}
 */
public class RedisReactiveInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
    @Override
    public void initialize(GenericApplicationContext context) {
        RedisReactiveAutoConfiguration redisAutoConfiguration = new RedisReactiveAutoConfiguration();
        context.registerBean("reactiveRedisTemplate", ReactiveRedisTemplate.class,
            () -> redisAutoConfiguration.reactiveRedisTemplate(context.getBean(ReactiveRedisConnectionFactory.class), context),
            (definition) -> ((RootBeanDefinition) definition).setTargetType(ResolvableType.forClassWithGenerics(ReactiveRedisTemplate.class, Object.class, Object.class)));
        context.registerBean("reactiveStringRedisTemplate", ReactiveStringRedisTemplate.class,
            () -> redisAutoConfiguration.reactiveStringRedisTemplate(context.getBean(ReactiveRedisConnectionFactory.class)));
    }
}
