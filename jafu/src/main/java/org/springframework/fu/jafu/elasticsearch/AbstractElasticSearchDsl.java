package org.springframework.fu.jafu.elasticsearch;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.fu.jafu.AbstractDsl;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractElasticSearchDsl<SELF extends AbstractElasticSearchDsl<SELF>> extends AbstractDsl {

    private final SELF self;
    private final Consumer<SELF> dsl;
    private Optional<String> hostAndPort = Optional.empty();
    private Boolean useSsl = false;

    protected abstract SELF getSelf();

    protected AbstractElasticSearchDsl(Consumer<SELF> dsl) {
        this.dsl = dsl;
        this.self = getSelf();
    }

    public SELF hostAndPort(String hostAndPort) {
        this.hostAndPort = Optional.ofNullable(hostAndPort);
        return self;
    }

    public SELF useSsl(Boolean useSsl) {
        this.useSsl = useSsl;
        return self;
    }

    protected ClientConfiguration createClientConfiguration() {
        ClientConfiguration.ClientConfigurationBuilderWithRequiredEndpoint baseBuilder = ClientConfiguration.builder();
        ClientConfiguration.MaybeSecureClientConfigurationBuilder maybeSecureBuilder =
                hostAndPort.map(baseBuilder::connectedTo).orElseGet(baseBuilder::connectedToLocalhost);
        ClientConfiguration.TerminalClientConfigurationBuilder terminalBuilder = maybeSecureBuilder;
        if (useSsl) {
            terminalBuilder = maybeSecureBuilder.usingSsl();
        }
        return terminalBuilder.build();
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        super.initialize(context);
        dsl.accept(self);
    }
}
