package org.springframework.security.config.annotation.web.configuration;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * {@link ApplicationContextInitializer} adapter for {@link WebSecurityConfiguration}.
 */
public class WebSecurityInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(GenericApplicationContext context) {

		Supplier<WebSecurityConfiguration> configurationSupplier = new Supplier<WebSecurityConfiguration>() {
			private WebSecurityConfiguration configuration;

			@Override
			public WebSecurityConfiguration get() {
				if (configuration == null) {
					configuration = new WebSecurityConfiguration();
					HttpSecurity security = context.getBean(HttpSecurityInitializer.HTTPSECURITY_BEAN_NAME, HttpSecurity.class);
					try {
						configuration.setFilterChains(Collections.singletonList(security.build()));
					} catch (Exception e) {
						throw new IllegalStateException(e);
					}

					List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers = new ArrayList<>();
					String[] webSecurityConfigurerBeanNames = context.getBeanNamesForType(
							ResolvableType.forClassWithGenerics(SecurityConfigurer.class, Filter.class, WebSecurity.class));
					for (String webSecurityConfigurerBeanName : webSecurityConfigurerBeanNames) {
						webSecurityConfigurers.add((SecurityConfigurer<Filter, WebSecurity>) context.getBean(webSecurityConfigurerBeanName));
					}
					try {
						configuration.setFilterChainProxySecurityConfigurer(context.getBean(ObjectPostProcessor.class), webSecurityConfigurers);
					} catch (Exception e) {
						throw new IllegalStateException(e);
					}
				}
				return configuration;
			}
		};

		context.registerBean(
				SecurityExpressionHandler.class,
				() -> configurationSupplier.get().webSecurityExpressionHandler(),
				bd -> bd.setDependsOn(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)
		);
		context.registerBean(
				AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME,
				Filter.class,
				() -> {
					try {
						return configurationSupplier.get().springSecurityFilterChain();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
		);
		context.registerBean(
				WebInvocationPrivilegeEvaluator.class,
				() -> configurationSupplier.get().privilegeEvaluator(),
				bd -> bd.setDependsOn(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)
		);
	}
}
