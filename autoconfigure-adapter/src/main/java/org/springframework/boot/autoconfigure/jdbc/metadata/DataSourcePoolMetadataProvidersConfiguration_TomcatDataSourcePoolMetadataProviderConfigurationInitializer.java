package org.springframework.boot.autoconfigure.jdbc.metadata;

import java.lang.Override;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.ClassUtils;

public class DataSourcePoolMetadataProvidersConfiguration_TomcatDataSourcePoolMetadataProviderConfigurationInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
  private static final boolean enabled;

  static {
    enabled =
    ClassUtils.isPresent("org.apache.tomcat.jdbc.pool.DataSource", null);
  }

  @Override
  public void initialize(GenericApplicationContext context) {
    if (DataSourcePoolMetadataProvidersConfiguration_TomcatDataSourcePoolMetadataProviderConfigurationInitializer.enabled) {
      if (context.getBeanFactory().getBeanNamesForType(DataSourcePoolMetadataProvidersConfiguration.TomcatDataSourcePoolMetadataProviderConfiguration.class).length==0) {
        context.registerBean(DataSourcePoolMetadataProvidersConfiguration.TomcatDataSourcePoolMetadataProviderConfiguration.class, () -> new DataSourcePoolMetadataProvidersConfiguration.TomcatDataSourcePoolMetadataProviderConfiguration());
        context.registerBean("tomcatPoolDataSourceMetadataProvider", DataSourcePoolMetadataProvider.class, () -> context.getBean(DataSourcePoolMetadataProvidersConfiguration.TomcatDataSourcePoolMetadataProviderConfiguration.class).tomcatPoolDataSourceMetadataProvider());
      }
    }
  }
}
