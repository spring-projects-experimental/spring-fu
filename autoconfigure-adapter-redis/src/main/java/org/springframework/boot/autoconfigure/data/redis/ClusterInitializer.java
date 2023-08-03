package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Cluster;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.redis.connection.RedisClusterConfiguration;

/**
 * {@link ApplicationContextInitializer} adapter for {@link RedisClusterConfiguration}
 */
public class ClusterInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
    private final Cluster cluster;

    public ClusterInitializer(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        if (cluster != null) {
            context.registerBean(RedisClusterConfiguration.class, this::getRedisClusterConfiguration);
        }
    }

    private RedisClusterConfiguration getRedisClusterConfiguration() {
        final RedisClusterConfiguration configuration = new RedisClusterConfiguration(cluster.getNodes());
        configuration.setMaxRedirects(cluster.getMaxRedirects());
        return configuration;
    }
}
