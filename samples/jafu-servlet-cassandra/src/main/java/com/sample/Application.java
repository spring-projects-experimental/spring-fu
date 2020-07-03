package com.sample;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.fu.jafu.JafuApplication;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import static org.springframework.fu.jafu.Jafu.webApplication;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static final JafuApplication cassandra = webApplication(app -> app
            .enable(Configurations.cassandraConfig)
            .enable(Configurations.webConfig));

    public static void main (String[] args) {
        bootstrapCassandra();
        cassandra.run(args);
    }

    private static void bootstrapCassandra() {
        CassandraContainer container = new CassandraContainer();
        container.waitingFor(Wait.forListeningPort())
                .withLogConsumer(new Slf4jLogConsumer(log))
                .start();
        Configurations.port = container.getFirstMappedPort();
        final Cluster cluster = container.getCluster();
        try (final Session session = cluster.connect()) {
            session.execute("CREATE KEYSPACE test WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};");
            session.execute("CREATE TABLE test.users(id uuid PRIMARY KEY,firstname text,lastname text);");
        }
    }
}
