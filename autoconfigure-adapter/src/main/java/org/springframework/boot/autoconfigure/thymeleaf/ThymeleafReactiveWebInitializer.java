package org.springframework.boot.autoconfigure.thymeleaf;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * {@link ApplicationContextInitializer} adapter for {@link ThymeleafAutoConfiguration.ThymeleafWebFluxConfiguration}.
 */
public class ThymeleafReactiveWebInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    private ThymeleafProperties properties;

    public ThymeleafReactiveWebInitializer(ThymeleafProperties properties) {
        this.properties = properties;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        ThymeleafAutoConfiguration.ThymeleafReactiveConfiguration reactiveConfiguration = new ThymeleafAutoConfiguration.ThymeleafReactiveConfiguration();
        ThymeleafAutoConfiguration.ThymeleafWebFluxConfiguration webFluxConfiguration = new ThymeleafAutoConfiguration.ThymeleafWebFluxConfiguration();
        context.registerBean("thymeleafTemplateEngine", SpringWebFluxTemplateEngine.class, () -> reactiveConfiguration.templateEngine(this.properties, context.getBeanProvider(ITemplateResolver.class), context.getBeanProvider(IDialect.class)));
        context.registerBean(ViewResolver.class, () -> webFluxConfiguration.thymeleafViewResolver(context.getBean("thymeleafTemplateEngine", SpringWebFluxTemplateEngine.class), this.properties));
    }
}
