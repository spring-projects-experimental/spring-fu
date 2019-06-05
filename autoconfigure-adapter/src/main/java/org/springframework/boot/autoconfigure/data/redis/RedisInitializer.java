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
        context.registerBeanDefinition("redisTemplate", getRedisTemplateBeanDefinition(context));
        context.registerBean("stringRedisTemplate", StringRedisTemplate.class, () -> getStringRedisTemplate(context));
    }

    private RootBeanDefinition getRedisTemplateBeanDefinition(GenericApplicationContext context) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(RedisTemplate.class, () -> getRedisTemplate(context));
        beanDefinition.setTargetType(ResolvableType.forClassWithGenerics(RedisTemplate.class, Object.class, Object.class));
        return beanDefinition;
    }

    private RedisTemplate<?, ?> getRedisTemplate(GenericApplicationContext context) {
        RedisAutoConfiguration redisAutoConfiguration = new RedisAutoConfiguration();
        try {
            return redisAutoConfiguration.redisTemplate(context.getBean(RedisConnectionFactory.class));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    private StringRedisTemplate getStringRedisTemplate(GenericApplicationContext context) {
        RedisAutoConfiguration redisAutoConfiguration = new RedisAutoConfiguration();
        try {
            return redisAutoConfiguration.stringRedisTemplate(context.getBean(RedisConnectionFactory.class));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}