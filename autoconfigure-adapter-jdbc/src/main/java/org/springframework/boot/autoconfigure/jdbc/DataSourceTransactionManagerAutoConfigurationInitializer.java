package org.springframework.boot.autoconfigure.jdbc;

import java.lang.Override;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public class DataSourceTransactionManagerAutoConfigurationInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

  @Override
  public void initialize(GenericApplicationContext context) {
    if (context.getBeanFactory().getBeanNamesForType(DataSourceTransactionManagerAutoConfiguration.class).length==0) {
      context.registerBean(DataSourceTransactionManagerAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration::new);
      context.registerBean(DataSourceTransactionManagerAutoConfiguration.JdbcTransactionManagerConfiguration.class, DataSourceTransactionManagerAutoConfiguration.JdbcTransactionManagerConfiguration::new);
      context.registerBean("transactionManager", DataSourceTransactionManager.class, () -> context.getBean(DataSourceTransactionManagerAutoConfiguration.JdbcTransactionManagerConfiguration.class).transactionManager(context.getBean(Environment.class), context.getBean(DataSource.class),context.getBeanProvider(TransactionManagerCustomizers.class)));
    }
  }
}
