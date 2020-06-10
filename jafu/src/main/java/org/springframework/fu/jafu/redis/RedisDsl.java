package org.springframework.fu.jafu.redis;

import org.springframework.boot.autoconfigure.data.redis.JedisRedisInitializer;
import org.springframework.boot.autoconfigure.data.redis.RedisInitializer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

import java.util.function.Consumer;

/**
 * JaFu DSL for Redis configuration.
 *
 * @author Andreas Gebhardt
 */
public class RedisDsl extends AbstractRedisDsl<RedisDsl> {

    public RedisDsl(final Consumer<RedisDsl> dsl) {
        super(dsl);
    }

    public RedisDsl jedis() {
        return jedis(dsl -> {
        });
    }

    @Override
    protected RedisDsl getSelf() {
        return this;
    }

    public RedisDsl jedis(final Consumer<JedisDsl> jedis) {
        setRedisClientInitializer(new JedisRedisInitializer(properties));
        jedis.accept(new JedisDsl(properties.getJedis()));
        return this;
    }

    public static ApplicationContextInitializer<GenericApplicationContext> redis() {
        return new RedisDsl(dsl -> {
        });
    }

    public static ApplicationContextInitializer<GenericApplicationContext> redis(final Consumer<RedisDsl> dsl) {
        return new RedisDsl(dsl);
    }

    public static class JedisDsl {

        private final RedisProperties.Jedis jedis;

        JedisDsl(final RedisProperties.Jedis jedis) {
            this.jedis = jedis;
            this.jedis.setPool(new RedisProperties.Pool());
        }

        public JedisDsl pool(final Consumer<PoolDsl> pool) {
            pool.accept(new PoolDsl(this.jedis.getPool()));
            return this;
        }

    }

    @Override
    public void initialize(GenericApplicationContext context) {
        super.initialize(context);
        new RedisInitializer().initialize(context);
    }

}
