/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.fu.jafu.cassandra;

import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import org.springframework.boot.autoconfigure.cassandra.CassandraInitializer;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

/**
 * Jafu DSL for CassandraDsl configuration.
 *
 * @author Hamid Mortazavi
 * @since 0.4.0
 */

public class CassandraDsl extends AbstractDsl {

    private final Consumer<CassandraDsl> dsl;

    private final CassandraProperties properties = new CassandraProperties();

    public CassandraDsl(Consumer<CassandraDsl> dsl) {
        this.dsl = dsl;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        super.initialize(context);
        this.dsl.accept(this);
        new CassandraInitializer(this.properties).initialize(context);
        new CassandraDataInitializer(this.properties).initialize(context);
    }

    public static ApplicationContextInitializer<GenericApplicationContext> cassandra() {
        return new CassandraDsl(mongoDsl -> {
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

    public CassandraDsl connection(Consumer<ConnectionDsl> connection) {
        connection.accept(new ConnectionDsl(this.properties.getConnection()));
        return this;
    }

    public CassandraDsl pool(Consumer<PoolDsl> connection) {
        connection.accept(new PoolDsl(this.properties.getPool()));
        return this;
    }

    public CassandraDsl request(Consumer<RequestDsl> connection) {
        connection.accept(new RequestDsl(this.properties.getRequest()));
        return this;
    }

    public static class ConnectionDsl {

        private final CassandraProperties.Connection connection;

        public ConnectionDsl(CassandraProperties.Connection connection) {
            this.connection = connection;
        }

        public ConnectionDsl connectTimeout(Duration connectTimeout) {
            connection.setConnectTimeout(connectTimeout);
            return this;
        }

        public ConnectionDsl initQueryTimeout(Duration initQueryTimeout) {
            connection.setInitQueryTimeout(initQueryTimeout);
            return this;
        }
    }

    public static class PoolDsl {

        private final CassandraProperties.Pool pool;

        public PoolDsl(CassandraProperties.Pool pool) {
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

    public static class RequestDsl {

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

    public static class ThrottlerDsl {

        private final CassandraProperties.Throttler throttler;

        public ThrottlerDsl(CassandraProperties.Throttler throttler) {
            this.throttler = throttler;
        }

        public ThrottlerDsl type(CassandraProperties.ThrottlerType type) {
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
}
