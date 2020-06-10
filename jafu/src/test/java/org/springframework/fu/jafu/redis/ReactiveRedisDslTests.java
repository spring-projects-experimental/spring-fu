package org.springframework.fu.jafu.redis;

import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.fu.jafu.Jafu.application;
import static org.springframework.fu.jafu.redis.ReactiveRedisDsl.reactiveRedis;

/**
 * JaFu DSL Tests for reactive Redis configuration.
 *
 * @author Andreas Gebhardt
 */
@Testcontainers
public class ReactiveRedisDslTests {

    @Container
    public static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:5").withExposedPorts(6379);

    @Test
    public void shouldEnableReactiveRedisWithDefaultLettuceClient() {

        final var application = application(a ->
                a
                        .beans(b -> b.bean(ReactiveTestRepository.class))
                        .enable(reactiveRedis(r -> r
                                        .host(REDIS.getHost())
                                        .port(REDIS.getFirstMappedPort())
                                )
                        )
        );

        try (var context = application.run()) {

            assertThatBeanIsAvailable(context, LettuceConnectionFactory.class);
            assertThatBeanIsNotAvailable(context, JedisConnectionFactory.class);

            var repository = context.getBean(ReactiveTestRepository.class);

            StepVerifier
                    .create(repository.save(new TestUser("1", "foo")))
                    .expectNext(true)
                    .verifyComplete();

            StepVerifier
                    .create(repository.findById("1"))
                    .expectNextMatches(user -> user.getName().equals("foo"))
                    .verifyComplete();

        }

    }

    public static <T> void assertThatBeanIsAvailable(final ConfigurableApplicationContext context, Class<T> requiredType) {
        assertNotNull(context.getBeanProvider(requiredType).getIfAvailable(() -> null), "Bean not available");
    }

    public static <T> void assertThatBeanIsNotAvailable(final ConfigurableApplicationContext context, Class<T> requiredType) {
        assertNull(context.getBeanProvider(requiredType).getIfAvailable(() -> null), "Bean is available");
    }

    public static class ReactiveTestRepository {

        private final ReactiveRedisTemplate<String, TestUser> reactiveRedisTemplate;

        public ReactiveTestRepository(final ReactiveRedisTemplate<String, TestUser> reactiveRedisTemplate) {
            this.reactiveRedisTemplate = reactiveRedisTemplate;
        }

        public Mono<TestUser> findById(String id) {
            return reactiveRedisTemplate.opsForValue().get(id);
        }

        public Mono<Boolean> save(final TestUser user) {
            return reactiveRedisTemplate.opsForValue().set(user.getId(), user);
        }

    }

}
