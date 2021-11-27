package org.springframework.boot.autoconfigure.data.redis;

import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * {@link ApplicationContextInitializer} adapter for {@link LettuceConnectionConfiguration}
 */
public class LettuceRedisInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
	private final RedisProperties redisProperties;

	public LettuceRedisInitializer(RedisProperties redisProperties) {
		this.redisProperties = redisProperties;
	}

    @Override
    public void initialize(GenericApplicationContext context) {
        context.registerBean(LettuceConnectionFactory.class, () -> getLettuceConnectionFactory(context));
    }

	private LettuceConnectionFactory getLettuceConnectionFactory(GenericApplicationContext context) {
		final LettuceConnectionConfiguration configuration = new LettuceConnectionConfiguration(redisProperties, context.getBeanProvider(RedisStandaloneConfiguration.class), context.getBeanProvider(RedisSentinelConfiguration.class), context.getBeanProvider(RedisClusterConfiguration.class));
		final ClientResources clientResources = DefaultClientResources.create();
		return configuration.redisConnectionFactory(context.getBeanProvider(LettuceClientConfigurationBuilderCustomizer.class), clientResources);
	}
}
