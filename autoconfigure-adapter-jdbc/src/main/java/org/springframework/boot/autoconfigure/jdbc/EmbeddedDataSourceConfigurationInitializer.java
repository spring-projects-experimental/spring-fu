package org.springframework.boot.autoconfigure.jdbc;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

public class EmbeddedDataSourceConfigurationInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

  private final DataSourceProperties dataSourceProperties;

  public EmbeddedDataSourceConfigurationInitializer(DataSourceProperties dataSourceProperties) {
    this.dataSourceProperties = dataSourceProperties;
  }

  @Override
  public void initialize(GenericApplicationContext context) {
    if (context.getBeanFactory().getBeanNamesForType(EmbeddedDataSourceConfiguration.class).length == 0) {
      context.registerBean("dataSource", EmbeddedDatabase.class, () -> new EmbeddedDataSourceConfiguration().dataSource(this.dataSourceProperties), def -> def.setDestroyMethodName("shutdown"));
    }
  }
}
