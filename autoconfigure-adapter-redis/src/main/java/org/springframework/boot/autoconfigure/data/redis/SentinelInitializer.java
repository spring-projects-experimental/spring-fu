package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;

import java.util.HashSet;

/**
 * {@link ApplicationContextInitializer} adapter for {@link RedisSentinelConfiguration}
 */
public class SentinelInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
    private final RedisProperties.Sentinel sentinel;

    public SentinelInitializer(RedisProperties.Sentinel sentinel) {
        this.sentinel = sentinel;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        if (sentinel != null) {
            context.registerBean(RedisSentinelConfiguration.class, this::getRedisSentinelConfiguration);
        }
    }

    private RedisSentinelConfiguration getRedisSentinelConfiguration() {
        return new RedisSentinelConfiguration(sentinel.getMaster(), new HashSet<>(sentinel.getNodes()));
    }
}
