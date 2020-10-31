package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.net.UnknownHostException;

/**
 * {@link ApplicationContextInitializer} adapter for {@link RedisAutoConfiguration}
 */
public class RedisInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
	@Override
	public void initialize(GenericApplicationContext context) {
		RedisAutoConfiguration redisAutoConfiguration = new RedisAutoConfiguration();
		context.registerBean("redisTemplate", RedisTemplate.class, () -> redisAutoConfiguration.redisTemplate(context.getBean(RedisConnectionFactory.class)), (definition) -> ((RootBeanDefinition) definition).setTargetType(ResolvableType.forClassWithGenerics(RedisTemplate.class, Object.class, Object.class)));
		context.registerBean("stringRedisTemplate", StringRedisTemplate.class, () -> redisAutoConfiguration.stringRedisTemplate(context.getBean(RedisConnectionFactory.class)));
	}
}