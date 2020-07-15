package org.springframework.boot.autoconfigure.data.r2dbc;

import com.github.jasync.r2dbc.mysql.JasyncConnectionFactory;
import com.github.jasync.sql.db.mysql.pool.MySQLConnectionFactory;
import com.github.jasync.sql.db.mysql.util.URLParser;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.r2dbc.core.DatabaseClient;

import java.nio.charset.StandardCharsets;

public class MysqlDatabaseClientInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private final MysqlR2dbcProperties properties;

	public MysqlDatabaseClientInitializer(MysqlR2dbcProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {

		JasyncConnectionFactory jasyncConnectionFactory = new JasyncConnectionFactory(new MySQLConnectionFactory(
				URLParser.INSTANCE.parseOrDie(properties.getUrl(), StandardCharsets.UTF_8)));
		context.registerBean(DatabaseClient.class, () -> DatabaseClient.builder().connectionFactory(jasyncConnectionFactory).build());
	}
}
