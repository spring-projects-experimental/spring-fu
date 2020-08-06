package org.springframework.boot.autoconfigure.jdbc;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.ClassUtils;

public class JndiDataSourceAutoConfigurationInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

  private static final boolean enabled;

  private final DataSourceProperties dataSourceProperties;

  static {
    enabled =
    ClassUtils.isPresent("javax.sql.DataSource", null) &&
    ClassUtils.isPresent("org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType", null);
  }

  public JndiDataSourceAutoConfigurationInitializer(DataSourceProperties dataSourceProperties) {
    this.dataSourceProperties = dataSourceProperties;
  }

  @Override
  public void initialize(GenericApplicationContext context) {
    if (JndiDataSourceAutoConfigurationInitializer.enabled) {
      if (context.getBeanFactory().getBeanNamesForType(JndiDataSourceAutoConfiguration.class).length==0) {
        context.registerBean(JndiDataSourceAutoConfiguration.class, () -> new JndiDataSourceAutoConfiguration());
        context.registerBean("dataSource", DataSource.class, () -> context.getBean(JndiDataSourceAutoConfiguration.class).dataSource(this.dataSourceProperties, context));
      }
    }
  }
}
