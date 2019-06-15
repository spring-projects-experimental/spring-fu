package com.sample

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.graphql.graphql
import org.springframework.fu.kofu.graphql.reactive.websocket
import org.springframework.fu.kofu.webflux.cors
import org.springframework.fu.kofu.webflux.webFlux

private val app = application(WebApplicationType.REACTIVE) {
    beans {
        bean { MessageService() }
    }
    webFlux {
        codecs {
            string()
            jackson()
        }
        cors {
            "/graphql" {
                exposedHeaders = "Access-Control-Allow-Origin"
            }
        }
        graphql {
            schema {
                supportPackages = listOf("com.sample")
                query {
                    Query(ref())
                }
                mutation {
                    Mutation(ref())
                }
                subscription {
                    Subscription(ref())
                }
            }
            websocket()
        }
    }
}

fun main() {
    app.run()
}