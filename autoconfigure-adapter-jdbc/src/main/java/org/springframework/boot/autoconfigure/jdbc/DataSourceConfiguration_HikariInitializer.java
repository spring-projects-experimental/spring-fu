package org.springframework.boot.autoconfigure.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import java.lang.Override;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

public class DataSourceConfiguration_HikariInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

  private final DataSourceProperties dataSourceProperties;

  public DataSourceConfiguration_HikariInitializer(DataSourceProperties dataSourceProperties) {
    this.dataSourceProperties = dataSourceProperties;
  }

  @Override
  public void initialize(GenericApplicationContext context) {
    if (context.getBeanFactory().getBeanNamesForType(DataSourceConfiguration.Hikari.class).length==0) {
      context.registerBean(DataSourceConfiguration.Hikari.class, () -> new DataSourceConfiguration.Hikari());
      context.registerBean("dataSource", HikariDataSource.class, () -> context.getBean(DataSourceConfiguration.Hikari.class).dataSource(this.dataSourceProperties), def -> { def.setFactoryMethodName("dataSource"); def.setFactoryBeanName(DataSourceConfiguration.Hikari.class.getName());});
    }
  }
}
