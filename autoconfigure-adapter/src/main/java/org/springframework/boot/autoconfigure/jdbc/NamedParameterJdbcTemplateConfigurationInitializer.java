package org.springframework.boot.autoconfigure.jdbc;

import java.lang.Override;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class NamedParameterJdbcTemplateConfigurationInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

  @Override
  public void initialize(GenericApplicationContext context) {
    if (context.getBeanFactory().getBeanNamesForType(NamedParameterJdbcTemplateConfiguration.class).length==0) {
      context.registerBean(NamedParameterJdbcTemplateConfiguration.class, () -> new NamedParameterJdbcTemplateConfiguration());
      context.registerBean("namedParameterJdbcTemplate", NamedParameterJdbcTemplate.class, () -> context.getBean(NamedParameterJdbcTemplateConfiguration.class).namedParameterJdbcTemplate(context.getBean(JdbcTemplate.class)));
    }
  }
}
