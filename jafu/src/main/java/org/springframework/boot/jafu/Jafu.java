package org.springframework.boot.jafu;

import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ResourceCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.StringCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.MessageSource;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

abstract public class Jafu {

	private static class Application {}

	public static SpringApplication application(Consumer<ApplicationDsl> dsl) {
		SpringApplication app = new SpringApplication(Application.class) {
			@Override
			protected void load(ApplicationContext context, Object[] sources) {
				// We don't want the annotation bean definition reader
			}
		};
		app.setWebApplicationType(WebApplicationType.REACTIVE);
		app.setApplicationContextClass(ReactiveWebServerApplicationContext.class);
		app.addInitializers((GenericApplicationContext context) -> {
			context.registerBean(AutowiredConstructorBeanPostProcessor.class);
			context.registerBean("messageSource", MessageSource.class, () -> {
				ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
				messageSource.setBasename("messages");
				messageSource.setDefaultEncoding("UTF-8");
				return messageSource;
			});
		});
		app.addInitializers(new ApplicationDsl(dsl));
		return app;
	}

	public static class AutowiredConstructorBeanPostProcessor extends AutowiredAnnotationBeanPostProcessor {
		@Override
		public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeanCreationException {
			Constructor<?> primaryConstructor = BeanUtils.findPrimaryConstructor(beanClass);
			if (primaryConstructor != null) {
				return new Constructor<?>[] { primaryConstructor };
			}
			else {
				return null;
			}
		}
	}

	public static class ApplicationDsl extends AbstractDsl implements ApplicationContextInitializer<GenericApplicationContext> {

		private final List<ApplicationContextInitializer<GenericApplicationContext>> initializers = new ArrayList<>();

		private final Consumer<ApplicationDsl> dsl;

		public ApplicationDsl(Consumer<ApplicationDsl> dsl) {
			super();
			this.dsl = dsl;
		}

		public void server(Consumer<ServerDsl> dsl) {
			ServerProperties serverProperties = new ServerProperties();
			ResourceProperties resourceProperties = new ResourceProperties();
			WebFluxProperties webFluxProperties = new WebFluxProperties();
			ConfigurableReactiveWebServerFactory serverFactory = new NettyReactiveWebServerFactory();
			this.initializers.add(new StringCodecInitializer(false));
			this.initializers.add(new ResourceCodecInitializer(false));
			this.initializers.add(new ReactiveWebServerInitializer(serverProperties, resourceProperties, webFluxProperties, serverFactory));
			this.initializers.add(new ServerDsl(dsl));
		}

		public void beans(Consumer<GenericApplicationContext> contextConsumer) {
			this.initializers.add(context -> contextConsumer.accept(context));
		}

		@Override
		public void initialize(GenericApplicationContext context) {
			this.context = context;
			this.dsl.accept(this);
			for (ApplicationContextInitializer<GenericApplicationContext> initializer : this.initializers) {
				initializer.initialize(context);
			}
		}

		public static class ServerDsl extends AbstractDsl implements ApplicationContextInitializer<GenericApplicationContext> {

			private final Consumer<ServerDsl> dsl;

			private Consumer<RouterFunctions.Builder> routerDsl;

			public ServerDsl(Consumer<ServerDsl> dsl) {
				super();
				this.dsl = dsl;
			}

			public void router(Consumer<RouterFunctions.Builder> routerDsl) {
				this.routerDsl = routerDsl;
			}

			@Override
			public void initialize(GenericApplicationContext context) {
				this.context = context;
				this.dsl.accept(this);
				RouterFunctions.Builder builder = RouterFunctions.route();
				context.registerBean(RouterFunction.class, () -> {
					this.routerDsl.accept(builder);
					return builder.build();
				});
			}
		}
	}

	public abstract static class AbstractDsl {

		protected GenericApplicationContext context;

		public <T> T ref(Class<T> beanClass) {
			return this.context.getBean(beanClass);
		}

		public final <T> void registerBean(Class<T> beanClass) {
			this.context.registerBean(beanClass, definition -> ((AbstractBeanDefinition)definition).setAutowireMode(AUTOWIRE_CONSTRUCTOR));
		}
	}
}
