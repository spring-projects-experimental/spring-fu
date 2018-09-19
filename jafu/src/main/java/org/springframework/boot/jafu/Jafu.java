package org.springframework.boot.jafu;

import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

	public static class ApplicationDsl extends AbstractDsl {

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

		public void beans(Consumer<BeanDsl> dsl) {
			this.initializers.add(new BeanDsl(dsl));
		}

		@Override
		public void register(GenericApplicationContext context) {
			this.dsl.accept(this);
		}

		public static class ServerDsl extends AbstractDsl {

			private final Consumer<ServerDsl> dsl;

			public ServerDsl(Consumer<ServerDsl> dsl) {
				super();
				this.dsl = dsl;
			}

			public void router(Consumer<RouterFunctions.Builder> routerDsl) {
				this.initializers.add(context -> {
					RouterFunctions.Builder builder = RouterFunctions.route();
					context.registerBean(RouterFunction.class, () -> {
						routerDsl.accept(builder);
						return builder.build();
					});

				});
			}

			@Override
			public void register(GenericApplicationContext context) {
				this.dsl.accept(this);
			}
		}
	}

	public static class BeanDsl extends AbstractDsl {

		private final Consumer<BeanDsl> dsl;

		public BeanDsl(Consumer<BeanDsl> dsl) {
			this.dsl = dsl;
		}

		public final <T> void registerBean(Class<T> beanClass) {
			this.context.registerBean(beanClass, definition -> ((AbstractBeanDefinition)definition).setAutowireMode(AUTOWIRE_CONSTRUCTOR));
		}

		public final <T> void registerBean(String beanName, Class<T> beanClass) {
			this.context.registerBean(beanName, beanClass, definition -> ((AbstractBeanDefinition)definition).setAutowireMode(AUTOWIRE_CONSTRUCTOR));
		}

		public final <T> void registerBean(Class<T> beanClass, Supplier<T> supplier) {
			this.context.registerBean(beanClass, supplier);
		}

		public final <T> void registerBean(String beanName, Class<T> beanClass, Supplier<T> supplier) {
			this.context.registerBean(beanName, beanClass, supplier);
		}

		@Override
		public void register(GenericApplicationContext context) {
			this.dsl.accept(this);
		}
	}

	public abstract static class AbstractDsl implements ApplicationContextInitializer<GenericApplicationContext> {

		protected final List<ApplicationContextInitializer<GenericApplicationContext>> initializers = new ArrayList<>();

		protected GenericApplicationContext context;

		public <T> T ref(Class<T> beanClass) {
			return this.context.getBean(beanClass);
		}

		@Override
		public void initialize(GenericApplicationContext context) {
			this.context = context;
			register(context);
			for (ApplicationContextInitializer<GenericApplicationContext> initializer : this.initializers) {
				initializer.initialize(context);
			}
		}

		abstract void register(GenericApplicationContext context);
	}
}
