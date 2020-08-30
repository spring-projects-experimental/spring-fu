package org.springframework.fu.jafu.jdbc;

import org.junit.jupiter.api.Test;
import org.wildfly.common.Assert;

import org.springframework.jdbc.core.JdbcTemplate;

import static org.springframework.fu.jafu.Jafu.application;
import static org.springframework.fu.jafu.jdbc.JdbcDsl.*;

public class JdbcDslTests {

	@Test
	public void enableJdbc() {
		var app = application(a -> a.enable(jdbc()));
		var context = app.run();
		Assert.assertNotNull(context.getBean(JdbcTemplate.class));
		context.close();
	}
}
