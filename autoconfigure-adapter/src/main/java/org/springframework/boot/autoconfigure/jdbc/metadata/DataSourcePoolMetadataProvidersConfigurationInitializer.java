package org.springframework.boot.autoconfigure.jdbc.metadata;

import java.lang.Override;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

public class DataSourcePoolMetadataProvidersConfigurationInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
  @Override
  public void initialize(GenericApplicationContext context) {
    if (context.getBeanFactory().getBeanNamesForType(DataSourcePoolMetadataProvidersConfiguration.class).length==0) {
      context.registerBean(DataSourcePoolMetadataProvidersConfiguration.class, () -> new DataSourcePoolMetadataProvidersConfiguration());
      new DataSourcePoolMetadataProvidersConfiguration_TomcatDataSourcePoolMetadataProviderConfigurationInitializer().initialize(context);
      new DataSourcePoolMetadataProvidersConfiguration_HikariPoolDataSourceMetadataProviderConfigurationInitializer().initialize(context);
      new DataSourcePoolMetadataProvidersConfiguration_CommonsDbcp2PoolDataSourceMetadataProviderConfigurationInitializer().initialize(context);
    }
  }
}
