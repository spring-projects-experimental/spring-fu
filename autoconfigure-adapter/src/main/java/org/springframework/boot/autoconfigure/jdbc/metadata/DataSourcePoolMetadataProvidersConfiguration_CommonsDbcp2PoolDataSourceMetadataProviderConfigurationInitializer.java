package org.springframework.boot.autoconfigure.jdbc.metadata;

import java.lang.Override;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.ClassUtils;

public class DataSourcePoolMetadataProvidersConfiguration_CommonsDbcp2PoolDataSourceMetadataProviderConfigurationInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
  private static final boolean enabled;

  static {
    enabled =
    ClassUtils.isPresent("org.apache.commons.dbcp2.BasicDataSource", null);
  }

  @Override
  public void initialize(GenericApplicationContext context) {
    if (DataSourcePoolMetadataProvidersConfiguration_CommonsDbcp2PoolDataSourceMetadataProviderConfigurationInitializer.enabled) {
      if (context.getBeanFactory().getBeanNamesForType(DataSourcePoolMetadataProvidersConfiguration.CommonsDbcp2PoolDataSourceMetadataProviderConfiguration.class).length==0) {
        context.registerBean(DataSourcePoolMetadataProvidersConfiguration.CommonsDbcp2PoolDataSourceMetadataProviderConfiguration.class, () -> new DataSourcePoolMetadataProvidersConfiguration.CommonsDbcp2PoolDataSourceMetadataProviderConfiguration());
        context.registerBean("commonsDbcp2PoolDataSourceMetadataProvider", DataSourcePoolMetadataProvider.class, () -> context.getBean(DataSourcePoolMetadataProvidersConfiguration.CommonsDbcp2PoolDataSourceMetadataProviderConfiguration.class).commonsDbcp2PoolDataSourceMetadataProvider());
      }
    }
  }
}
