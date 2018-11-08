package org.springframework.fu.jafu;

import java.util.function.Consumer;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

public class ServerDsl extends AbstractDsl {

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
