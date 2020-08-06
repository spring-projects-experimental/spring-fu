package org.springframework.boot.autoconfigure.jdbc;

import java.lang.Override;
import javax.sql.DataSource;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcTemplateConfigurationInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

  private final JdbcProperties jdbcProperties;

  public JdbcTemplateConfigurationInitializer(JdbcProperties jdbcProperties) {
    this.jdbcProperties = jdbcProperties;
  }

  @Override
  public void initialize(GenericApplicationContext context) {
    if (context.getBeanFactory().getBeanNamesForType(JdbcTemplateConfiguration.class).length==0) {
      context.registerBean(JdbcTemplateConfiguration.class, () -> new JdbcTemplateConfiguration());
      context.registerBean("jdbcTemplate", JdbcTemplate.class, () -> context.getBean(JdbcTemplateConfiguration.class).jdbcTemplate(context.getBean(DataSource.class), this.jdbcProperties));
    }
  }
}
