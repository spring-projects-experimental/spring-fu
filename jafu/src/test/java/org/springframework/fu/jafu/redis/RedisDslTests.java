package org.springframework.fu.jafu.redis;

import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.fu.jafu.Jafu.application;
import static org.springframework.fu.jafu.redis.RedisDsl.redis;

/**
 * JaFu DSL Tests for Redis configuration.
 *
 * @author Andreas Gebhardt
 */
@Testcontainers
public class RedisDslTests {

    @Container
    public static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:5").withExposedPorts(6379);

    @Test
    public void shouldEnableRedisWithDefaultLettuceClient() {

        final var application = application(a ->
                a
                        .beans(b -> b.bean(TestRepository.class))
                        .enable(redis(r -> r
                                        .host(REDIS.getHost())
                                        .port(REDIS.getFirstMappedPort())
                                )
                        )
        );

        try (var context = application.run()) {

            assertThatBeanIsAvailable(context, LettuceConnectionFactory.class);
            assertThatBeanIsNotAvailable(context, JedisConnectionFactory.class);

            var repository = context.getBean(TestRepository.class);

            repository.save(new TestUser("1", "foo"));
            assertEquals("foo", repository.findById("1").getName());

        }

    }

    @Test
    public void shouldEnableRedisWithLettuceClient() {

        final var application = application(a ->
                a
                        .beans(b -> b.bean(TestRepository.class))
                        .enable(
                                redis(r -> r
                                        .host(REDIS.getHost())
                                        .port(REDIS.getFirstMappedPort())
                                        .lettuce(l -> l.shutdownTimeout(Duration.ofMillis(100)))
                                )
                        )
        );

        try (var context = application.run()) {

            assertThatBeanIsAvailable(context, LettuceConnectionFactory.class);
            assertThatBeanIsNotAvailable(context, JedisConnectionFactory.class);

            var repository = context.getBean(TestRepository.class);

            repository.save(new TestUser("1", "foo"));
            assertEquals("foo", repository.findById("1").getName());

        }

    }

    @Test
    public void shouldEnableRedisWithJedisClient() {

        final var application = application(a ->
                a
                        .beans(b -> b.bean(TestRepository.class))
                        .enable(redis(r -> r
                                        .host(REDIS.getHost())
                                        .port(REDIS.getFirstMappedPort())
                                        .jedis()
                                )
                        )
        );

        try (var context = application.run()) {

            assertThatBeanIsAvailable(context, JedisConnectionFactory.class);
            assertThatBeanIsNotAvailable(context, LettuceConnectionFactory.class);

            var repository = context.getBean(TestRepository.class);

            repository.save(new TestUser("1", "foo"));
            assertEquals("foo", repository.findById("1").getName());

        }

    }

    public static <T> void assertThatBeanIsAvailable(final ConfigurableApplicationContext context, Class<T> requiredType) {
        assertNotNull(context.getBeanProvider(requiredType).getIfAvailable(() -> null), "Bean not available");
    }

    public static <T> void assertThatBeanIsNotAvailable(final ConfigurableApplicationContext context, Class<T> requiredType) {
        assertNull(context.getBeanProvider(requiredType).getIfAvailable(() -> null), "Bean is available");
    }

    public static class TestRepository {


        private final RedisTemplate<String, TestUser> redisTemplate;

        public TestRepository(final RedisTemplate<String, TestUser> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        public TestUser findById(String id) {
            return redisTemplate.opsForValue().get(id);
        }

        public void save(final TestUser user) {
            redisTemplate.opsForValue().set(user.getId(), user);
        }

    }

}
