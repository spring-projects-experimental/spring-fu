package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.net.UnknownHostException;

/**
 * {@link ApplicationContextInitializer} adapter for {@link JedisConnectionConfiguration}
 */
public class JedisRedisInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
	private final RedisProperties redisProperties;

	public JedisRedisInitializer(RedisProperties redisProperties) {
		this.redisProperties = redisProperties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		if (redisProperties.getJedis().getPool() != null) {
			context.registerBean(RedisConnectionFactory.class, () -> getJedisConnectionFactory(context));
		}
	}

	private JedisConnectionFactory getJedisConnectionFactory(GenericApplicationContext context) {
		final JedisConnectionConfiguration configuration = new JedisConnectionConfiguration(redisProperties, context.getBeanProvider(RedisSentinelConfiguration.class), context.getBeanProvider(RedisClusterConfiguration.class));
		return configuration.redisConnectionFactory(context.getBeanProvider(JedisClientConfigurationBuilderCustomizer.class));
	}
}
