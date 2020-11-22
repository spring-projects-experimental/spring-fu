package org.springframework.fu.kofu.elasticsearch

import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.fu.kofu.AbstractDsl

abstract class AbstractElasticSearchDsl: AbstractDsl() {

    var hostAndPort: String? = null
    var usingSsl: Boolean = false

    protected fun createClientConfiguration() =
        ClientConfiguration.builder()
            .let {
                if (hostAndPort != null) it.connectedTo(hostAndPort)
                else it.connectedToLocalhost()
            }
            .let {
                if (usingSsl) it.usingSsl()
                else it
            }.build()
}