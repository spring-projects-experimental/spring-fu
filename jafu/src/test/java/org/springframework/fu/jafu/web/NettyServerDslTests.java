package org.springframework.fu.jafu.web;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;

public class NettyServerDslTests extends AbstractWebServerDslTests {

	@Override
	ConfigurableReactiveWebServerFactory getServerFactory() {
		return new NettyReactiveWebServerFactory();
	}
}
