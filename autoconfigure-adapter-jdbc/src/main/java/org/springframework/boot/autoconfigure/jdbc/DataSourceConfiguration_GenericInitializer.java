package org.springframework.boot.autoconfigure.jdbc;

import java.lang.Override;
import javax.sql.DataSource;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

public class DataSourceConfiguration_GenericInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

  private final DataSourceProperties dataSourceProperties;

  public DataSourceConfiguration_GenericInitializer(DataSourceProperties dataSourceProperties) {
    this.dataSourceProperties = dataSourceProperties;
  }

  @Override
  public void initialize(GenericApplicationContext context) {
    if (context.getBeanFactory().getBeanNamesForType(DataSourceConfiguration.Generic.class).length==0) {
      context.registerBean(DataSourceConfiguration.Generic.class, () -> new DataSourceConfiguration.Generic());
      context.registerBean("dataSource", DataSource.class, () -> context.getBean(DataSourceConfiguration.Generic.class).dataSource(this.dataSourceProperties));
    }
  }
}
