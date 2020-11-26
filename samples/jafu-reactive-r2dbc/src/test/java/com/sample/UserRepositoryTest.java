package com.sample;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRepositoryTest {

    private ConfigurableApplicationContext context;

    @BeforeAll
    public void beforeAll() {
        context = Application.app.run("tests");
    }

    @Test
    public void count() {
        var repository = context.getBean(UserRepository.class);
        assertEquals(3, repository.count().block());
    }

    @Test
    public void testFindOne() {
        var repository = context.getBean(UserRepository.class);
        var expected = new User();
        expected.setLogin("sdeleuze");
        expected.setFirstname("Sébastien");
        expected.setLastname("Deleuze");
        assertEquals(expected, repository.findOne("sdeleuze").block());
    }

    @AfterAll
    public void afterAll() {
        context.close();
    }

}