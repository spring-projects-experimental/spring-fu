package org.springframework.fu.jafu.cassandra;

import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import org.springframework.boot.autoconfigure.cassandra.CassandraInitializer;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties.ThrottlerType;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

/**
 * JaFu DSL for Cassandra configuration.
 *
 * @author Andreas Gebhardt
 */
public class CassandraDsl extends AbstractDsl {

    private final Consumer<CassandraDsl> dsl;

    private final CassandraProperties properties = new CassandraProperties();

    CassandraDsl(Consumer<CassandraDsl> dsl) {
        this.dsl = dsl;
    }

    public static ApplicationContextInitializer<GenericApplicationContext> cassandra() {
        return new CassandraDsl(it -> {
        });
    }

    public static ApplicationContextInitializer<GenericApplicationContext> cassandra(Consumer<CassandraDsl> dsl) {
        return new CassandraDsl(dsl);
    }

    public CassandraDsl keyspaceName(String keyspaceName) {
        this.properties.setKeyspaceName(keyspaceName);
        return this;
    }

    public CassandraDsl sessionName(String sessionName) {
        this.properties.setSessionName(sessionName);
        return this;
    }

    public CassandraDsl contactPoints(List<String> contactPoints) {
        this.properties.getContactPoints().clear();
        this.properties.getContactPoints().addAll(contactPoints);
        return this;
    }

    public CassandraDsl contactPoints(String... contactPoints) {
        this.properties.getContactPoints().clear();
        this.properties.getContactPoints().addAll(asList(contactPoints));
        return this;
    }

    public CassandraDsl port(int port) {
        this.properties.setPort(port);
        return this;
    }

    public CassandraDsl localDatacenter(String localDatacenter) {
        this.properties.setLocalDatacenter(localDatacenter);
        return this;
    }

    public CassandraDsl username(String username) {
        this.properties.setUsername(username);
        return this;
    }

    public CassandraDsl password(String password) {
        this.properties.setPassword(password);
        return this;
    }

    public CassandraDsl compression(CassandraProperties.Compression compression) {
        this.properties.setCompression(compression);
        return this;
    }

    public CassandraDsl schemaAction(String schemaAction) {
        this.properties.setSchemaAction(schemaAction);
        return this;
    }

    public CassandraDsl ssl(boolean ssl) {
        this.properties.setSsl(ssl);
        return this;
    }

    public CassandraDsl enableSsl() {
        this.properties.setSsl(true);
        return this;
    }

    public CassandraDsl connection(Consumer<ConnectionDsl> connection) {
        connection.accept(new ConnectionDsl(this.properties.getConnection()));
        return this;
    }

    public CassandraDsl pool(Consumer<PoolDsl> pool) {
        pool.accept(new PoolDsl(this.properties.getPool()));
        return this;
    }

    public CassandraDsl request(Consumer<RequestDsl> request) {
        request.accept(new RequestDsl(this.properties.getRequest()));
        return this;
    }

    public static class ConnectionDsl {

        private final CassandraProperties.Connection connection;

        ConnectionDsl(CassandraProperties.Connection connection) {
            this.connection = connection;
        }

        public ConnectionDsl connectTimeout(Duration connectTimeout) {
            this.connection.setConnectTimeout(connectTimeout);
            return this;
        }

        public ConnectionDsl initQueryTimeout(Duration initQueryTimeout) {
            this.connection.setInitQueryTimeout(initQueryTimeout);
            return this;
        }

    }

    public static class PoolDsl {

        private final CassandraProperties.Pool pool;

        PoolDsl(CassandraProperties.Pool pool) {
            this.pool = pool;
        }

        public PoolDsl idleTimeout(Duration idleTimeout) {
            this.pool.setIdleTimeout(idleTimeout);
            return this;
        }

        public PoolDsl heartbeatInterval(Duration heartbeatInterval) {
            this.pool.setHeartbeatInterval(heartbeatInterval);
            return this;
        }

    }

    private static class RequestDsl {

        private final CassandraProperties.Request request;

        public RequestDsl(CassandraProperties.Request request) {
            this.request = request;
        }

        public RequestDsl timeout(Duration timeout) {
            this.request.setTimeout(timeout);
            return this;
        }

        public RequestDsl consistency(DefaultConsistencyLevel consistency) {
            this.request.setConsistency(consistency);
            return this;
        }

        public RequestDsl serialConsistency(DefaultConsistencyLevel serialConsistency) {
            this.request.setSerialConsistency(serialConsistency);
            return this;
        }

        public RequestDsl pageSize(int pageSize) {
            this.request.setPageSize(pageSize);
            return this;
        }

        public RequestDsl throttler(Consumer<ThrottlerDsl> throttler) {
            throttler.accept(new ThrottlerDsl(this.request.getThrottler()));
            return this;
        }

    }

    private static class ThrottlerDsl {

        private final CassandraProperties.Throttler throttler;

        public ThrottlerDsl(CassandraProperties.Throttler throttler) {
            this.throttler = throttler;
        }

        public ThrottlerDsl type(ThrottlerType type) {
            this.throttler.setType(type);
            return this;
        }

        public ThrottlerDsl maxQueueSize(int maxQueueSize) {
            this.throttler.setMaxQueueSize(maxQueueSize);
            return this;
        }

        public ThrottlerDsl maxConcurrentRequests(int maxConcurrentRequests) {
            this.throttler.setMaxConcurrentRequests(maxConcurrentRequests);
            return this;
        }

        public ThrottlerDsl maxRequestsPerSecond(int maxRequestsPerSecond) {
            this.throttler.setMaxRequestsPerSecond(maxRequestsPerSecond);
            return this;
        }

        public ThrottlerDsl drainInterval(Duration drainInterval) {
            this.throttler.setDrainInterval(drainInterval);
            return this;
        }

    }

    @Override
    public void initialize(GenericApplicationContext context) {
        super.initialize(context);
        this.dsl.accept(this);
        new CassandraInitializer(this.properties).initialize(context);
        new CassandraDataInitializer(this.properties).initialize(context);
    }

}
