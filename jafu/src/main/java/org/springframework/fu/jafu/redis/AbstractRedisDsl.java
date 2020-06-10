package org.springframework.fu.jafu.redis;

import org.springframework.boot.autoconfigure.data.redis.ClusterInitializer;
import org.springframework.boot.autoconfigure.data.redis.LettuceRedisInitializer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.SentinelInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

/**
 * base JaFu DSL for Redis configuration.
 *
 * @author Andreas Gebhardt
 */
public abstract class AbstractRedisDsl<SELF extends AbstractRedisDsl<SELF>> extends AbstractDsl {

    private final SELF self;

    private ApplicationContextInitializer<GenericApplicationContext> redisClientInitializer;

    private final Consumer<SELF> dsl;

    protected RedisProperties properties = new RedisProperties();

    protected abstract SELF getSelf();

    public void setRedisClientInitializer(ApplicationContextInitializer<GenericApplicationContext> redisClientInitializer) {
        this.redisClientInitializer = redisClientInitializer;
    }

    protected ApplicationContextInitializer<GenericApplicationContext> getRedisClientInitializier() {
        return redisClientInitializer;
    }

    public AbstractRedisDsl(final Consumer<SELF> dsl) {
        this.self = getSelf();
        this.redisClientInitializer = new LettuceRedisInitializer(properties);
        this.dsl = dsl;
    }

    public SELF database(final int database) {
        this.properties.setDatabase(database);
        return self;
    }

    public SELF url(final String url) {
        this.properties.setUrl(url);
        return self;
    }

    public SELF host(final String host) {
        this.properties.setHost(host);
        return self;
    }

    public SELF password(final String password) {
        this.properties.setPassword(password);
        return self;
    }

    public SELF port(final int port) {
        this.properties.setPort(port);
        return self;
    }

    public SELF ssl(final boolean ssl) {
        this.properties.setSsl(ssl);
        return self;
    }

    public SELF enableSsl() {
        this.properties.setSsl(true);
        return self;
    }

    public SELF timeout(final Duration timeout) {
        this.properties.setTimeout(timeout);
        return self;
    }

    public SELF clientName(final String clientName) {
        this.properties.setClientName(clientName);
        return self;
    }

    public SELF sentinel(final Consumer<SentinelDsl> sentinel) {
        this.properties.setSentinel(new RedisProperties.Sentinel());
        sentinel.accept(new SentinelDsl(this.properties.getSentinel()));
        return self;
    }

    public SELF cluster(final Consumer<ClusterDsl> cluster) {
        this.properties.setCluster(new RedisProperties.Cluster());
        cluster.accept(new ClusterDsl(this.properties.getCluster()));
        return self;
    }

    public SELF lettuce(final Consumer<LettuceDsl> lettuce) {
        setRedisClientInitializer(new LettuceRedisInitializer(properties));
        lettuce.accept(new LettuceDsl(this.properties.getLettuce()));
        return self;
    }

    public static class SentinelDsl {

        private final RedisProperties.Sentinel sentinel;

        SentinelDsl(final RedisProperties.Sentinel sentinel) {
            this.sentinel = sentinel;
        }

        public SentinelDsl master(final String master) {
            this.sentinel.setMaster(master);
            return this;
        }

        public SentinelDsl nodes(final List<String> nodes) {
            this.sentinel.setNodes(nodes);
            return this;
        }

        public SentinelDsl nodes(final String... nodes) {
            this.sentinel.setNodes(asList(nodes));
            return this;
        }

        public SentinelDsl password(final String password) {
            this.sentinel.setPassword(password);
            return this;
        }

    }

    public static class ClusterDsl {

        private final RedisProperties.Cluster cluster;

        ClusterDsl(final RedisProperties.Cluster cluster) {
            this.cluster = cluster;
        }

        public ClusterDsl nodes(final List<String> nodes) {
            this.cluster.setNodes(nodes);
            return this;
        }

        public ClusterDsl nodes(final String... nodes) {
            this.cluster.setNodes(asList(nodes));
            return this;
        }

        public ClusterDsl maxRedirects(final int maxRedirects) {
            this.cluster.setMaxRedirects(maxRedirects);
            return this;
        }

    }

    public static class PoolDsl {

        private final RedisProperties.Pool pool;

        PoolDsl(final RedisProperties.Pool pool) {
            this.pool = pool;
        }

        public PoolDsl maxIdle(final int maxIdle) {
            this.pool.setMaxIdle(maxIdle);
            return this;
        }

        public PoolDsl minIdle(final int minIdle) {
            this.pool.setMinIdle(minIdle);
            return this;
        }

        public PoolDsl maxActive(final int maxActive) {
            this.pool.setMaxActive(maxActive);
            return this;
        }

        public PoolDsl maxWait(final Duration maxWait) {
            this.pool.setMaxWait(maxWait);
            return this;
        }

        public PoolDsl timeBetweenEvictionRuns(final Duration timeBetweenEvictionRuns) {
            this.pool.setTimeBetweenEvictionRuns(timeBetweenEvictionRuns);
            return this;
        }

    }

    public static class LettuceDsl {

        private final RedisProperties.Lettuce lettuce;

        LettuceDsl(final RedisProperties.Lettuce lettuce) {
            this.lettuce = lettuce;
        }

        public LettuceDsl shutdownTimeout(final Duration shutdownTimeout) {
            this.lettuce.setShutdownTimeout(shutdownTimeout);
            return this;
        }

        public LettuceDsl pool(final Consumer<PoolDsl> pool) {
            this.lettuce.setPool(new RedisProperties.Pool());
            pool.accept(new PoolDsl(this.lettuce.getPool()));
            return this;
        }

        public LettuceDsl cluster(final Consumer<ClusterDsl> cluster) {
            cluster.accept(new ClusterDsl(this.lettuce.getCluster()));
            return this;
        }

        public static class ClusterDsl {

            private final RedisProperties.Lettuce.Cluster cluster;

            public ClusterDsl(final RedisProperties.Lettuce.Cluster cluster) {
                this.cluster = cluster;
            }

            public ClusterDsl refresh(final Consumer<RefreshDsl> refresh) {
                refresh.accept(new RefreshDsl(this.cluster.getRefresh()));
                return this;
            }

            public static class RefreshDsl {

                private final RedisProperties.Lettuce.Cluster.Refresh refresh;

                public RefreshDsl(final RedisProperties.Lettuce.Cluster.Refresh refresh) {
                    this.refresh = refresh;
                }

                public RefreshDsl period(final Duration period) {
                    this.refresh.setPeriod(period);
                    return this;
                }

                public RefreshDsl adaptive(final boolean adaptive) {
                    this.refresh.setAdaptive(adaptive);
                    return this;
                }

            }

        }
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        super.initialize(context);

        dsl.accept(self);

        getRedisClientInitializier().initialize(context);
        new ClusterInitializer(properties.getCluster()).initialize(context);
        new SentinelInitializer(properties.getSentinel()).initialize(context);
    }

}
