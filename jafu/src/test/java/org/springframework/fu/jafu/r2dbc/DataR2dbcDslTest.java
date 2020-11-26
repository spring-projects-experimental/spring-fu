package org.springframework.fu.jafu.r2dbc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
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
import static org.springframework.fu.jafu.r2dbc.DataR2dbcDsl.dataR2dbc;

class DataR2dbcDslTest {

    @Test
    public void enableDataWithH2EmbeddedR2dbc(@TempDir Path tempDir) throws IOException {
        var dbPath = tempDir.resolve("test.db");
        Files.createFile(dbPath);
        var app = application(a -> {
            a.enable(dataR2dbc(dataR2dbcDsl ->
                    dataR2dbcDsl.r2dbc(r2dbcDsl ->
                            r2dbcDsl.url("r2dbc:h2:file:///" + dbPath.toAbsolutePath()))));
            a.beans(beans -> beans.bean(DataR2dbcTestDataRepository.class));
        });

        var context = app.run();
        var entityTemplate = context.getBean(R2dbcEntityTemplate.class);
        Assert.assertNotNull(entityTemplate);
        var repository = context.getBean(DataR2dbcTestDataRepository.class);
        Assert.assertNotNull(repository);

        StepVerifier.create(
                entityTemplate.getDatabaseClient().sql("CREATE TABLE test_data (id UUID PRIMARY KEY, name VARCHAR(255));").then()
                        .then(repository.save(dataUser))
                        .then(repository.findOne(dataUser.id)))
                .expectNext(dataUser)
                .verifyComplete();
        context.close();
    }

    @Test
    public void enableDataWithPostgresR2dbc(@TempDir Path tempDir) throws IOException {
        var pg = new GenericContainer("postgres:13")
                .withExposedPorts(5432)
                .withEnv("POSTGRES_USER", "jo")
                .withEnv("POSTGRES_PASSWORD", "pwd")
                .withEnv("POSTGRES_DB", "db");
        pg.start();

        var app = application(a -> {
            a.enable(dataR2dbc(dataR2dbcDsl ->
                    dataR2dbcDsl.r2dbc(r2dbcDsl -> {
                        r2dbcDsl.url("r2dbc:postgresql://" + pg.getContainerIpAddress() + ":" + pg.getFirstMappedPort() + "/db");
                        r2dbcDsl.username("jo");
                        r2dbcDsl.password("pwd");
                    })));
            a.beans(beans -> beans.bean(DataR2dbcTestDataRepository.class));
        });

        var context = app.run();
        var entityTemplate = context.getBean(R2dbcEntityTemplate.class);
        Assert.assertNotNull(entityTemplate);
        var repository = context.getBean(DataR2dbcTestDataRepository.class);
        Assert.assertNotNull(repository);

        StepVerifier.create(
                entityTemplate.getDatabaseClient().sql("CREATE TABLE test_data (id UUID PRIMARY KEY, name VARCHAR(255));").then()
                        .then(repository.save(dataUser))
                        .then(repository.findOne(dataUser.id)))
                .expectNext(dataUser)
                .verifyComplete();
        context.close();
        pg.close();
    }

    private static TestData dataUser = new TestData(UUID.randomUUID(), "foo");

    public static class DataR2dbcTestDataRepository {
        private final R2dbcEntityTemplate entityTemplate;

        public DataR2dbcTestDataRepository(R2dbcEntityTemplate entityTemplate) {
            this.entityTemplate = entityTemplate;
        }

        public Mono<TestData> save(TestData newUser) {
            return entityTemplate.insert(dataUser);
        }

        public Mono<TestData> findOne(UUID id) {
            return entityTemplate.selectOne(Query.query(Criteria.where("id").is(id)), TestData.class);
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