package org.springframework.fu.jafu.r2dbc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.testcontainers.containers.GenericContainer;
import org.wildfly.common.Assert;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.fu.jafu.Jafu.application;
import static org.springframework.fu.jafu.r2dbc.R2dbcDsl.r2dbc;

class R2dbcDslTest {

    @Test
    public void enableR2dbcWithH2Embedded(@TempDir Path tempDir) throws IOException {
        var dbPath = tempDir.resolve("test.db");
        Files.createFile(dbPath);

        var app = application(a -> {
            a.enable(r2dbc(r2dbcDsl -> r2dbcDsl.url("r2dbc:h2:file:///" + dbPath.toAbsolutePath())));
            a.beans(beans -> beans.bean(R2dbcTestDataRepository.class));
        });

        var context = app.run();
        var repository = context.getBean(R2dbcTestDataRepository.class);

        StepVerifier.create(
                repository.getClient().sql("CREATE TABLE users (id UUID PRIMARY KEY, name VARCHAR(255));").then()
                        .then(repository.save(dataUser))
                        .then(repository.findOne(dataUser.id)))
                .expectNext(dataUser)
                .verifyComplete();
        context.close();
    }

    @Test
    public void enableR2dbcWithH2EmbeddedWithTransaction(@TempDir Path tempDir) throws IOException {
        var dbPath = tempDir.resolve("test.db");
        Files.createFile(dbPath);

        var app = application(a -> {
            a.enable(r2dbc(r2dbcDsl -> {
                r2dbcDsl.url("r2dbc:h2:file:///" + dbPath.toAbsolutePath());
                r2dbcDsl.transactional(true);
            }));
            a.beans(beans -> beans.bean(R2dbcTestDataRepository.class));
        });

        var context = app.run();
        var repository = context.getBean(R2dbcTestDataRepository.class);
        var transactional = context.getBean(TransactionalOperator.class);
        Assertions.assertNotNull(transactional);

        StepVerifier.create(
                transactional.transactional(
                        repository.getClient().sql("CREATE TABLE users (id UUID PRIMARY KEY, name VARCHAR(255));").then()
                                .then(repository.save(dataUser))
                                .then(repository.findOne(dataUser.id))))
                .expectNext(dataUser)
                .verifyComplete();
        context.close();
    }

    @Test
    public void enableR2dbcWithPostgres() throws IOException {
        var pg = new GenericContainer("postgres:13")
                .withExposedPorts(5432)
                .withEnv("POSTGRES_USER", "jo")
                .withEnv("POSTGRES_PASSWORD", "pwd")
                .withEnv("POSTGRES_DB", "db");
        pg.start();

        var app = application(a -> {
            a.enable(r2dbc(r2dbcDsl -> {
                r2dbcDsl.url("r2dbc:postgresql://" + pg.getContainerIpAddress() + ":" + pg.getFirstMappedPort() + "/db");
                r2dbcDsl.username("jo");
                r2dbcDsl.password("pwd");
            }));
            a.beans(beans -> beans.bean(R2dbcTestDataRepository.class));
        });

        var context = app.run();
        var repository = context.getBean(R2dbcTestDataRepository.class);
        Assert.assertNotNull(repository);

        StepVerifier.create(
                repository.getClient().sql("CREATE TABLE users (id UUID PRIMARY KEY, name VARCHAR(255));").then()
                        .then(repository.save(dataUser))
                        .then(repository.findOne(dataUser.id)))
                .expectNext(dataUser)
                .verifyComplete();
        context.close();
        pg.close();
    }

    @Test
    public void enableR2dbcWithPostgresWithTransaction() throws IOException {
        var pg = new GenericContainer("postgres:13")
                .withExposedPorts(5432)
                .withEnv("POSTGRES_USER", "jo")
                .withEnv("POSTGRES_PASSWORD", "pwd")
                .withEnv("POSTGRES_DB", "db");
        pg.start();

        var app = application(a -> {
            a.enable(r2dbc(r2dbcDsl -> {
                r2dbcDsl.url("r2dbc:postgresql://" + pg.getContainerIpAddress() + ":" + pg.getFirstMappedPort() + "/db");
                r2dbcDsl.username("jo");
                r2dbcDsl.password("pwd");
                r2dbcDsl.transactional(true);
            }));
            a.beans(beans -> beans.bean(R2dbcTestDataRepository.class));
        });

        var context = app.run();
        var repository = context.getBean(R2dbcTestDataRepository.class);
        var transactional = context.getBean(TransactionalOperator.class);
        Assert.assertNotNull(transactional);

        StepVerifier.create(
                transactional.transactional(
                        repository.getClient().sql("CREATE TABLE users (id UUID PRIMARY KEY, name VARCHAR(255));").then()
                                .then(repository.save(dataUser))
                                .then(repository.findOne(dataUser.id))))
                .expectNext(dataUser)
                .verifyComplete();
        context.close();
        pg.close();
    }

    private static TestData dataUser = new TestData(UUID.randomUUID(), "foo");

    public static class R2dbcTestDataRepository {
        private final DatabaseClient client;

        public R2dbcTestDataRepository(DatabaseClient client) {
            this.client = client;
        }

        public DatabaseClient getClient() {
            return client;
        }

        public Mono<Integer> save(TestData newUser) {
            return client.sql("INSERT INTO users(id, name) VALUES(:id, :name)")
                    .bind("id", newUser.id)
                    .bind("name", newUser.name)
                    .fetch()
                    .rowsUpdated();
        }

        public Mono<TestData> findOne(UUID id) {
            return client.sql("SELECT * FROM users WHERE id = :id")
                    .bind("id", id)
                    .map(row -> new TestData(row.get("id", UUID.class), row.get("name", String.class)))
                    .one();
        }


    }

    private static class TestData {
        @Id
        private UUID id;
        private String name;

        public TestData() {
        }

        public TestData(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        public UUID getId() {
            return id;
        }

        public TestData setId(UUID id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public TestData setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public String toString() {
            return "TestDataUser{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestData that = (TestData) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

}