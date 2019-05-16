package com.sample

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.graphql.graphql
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
        graphql {
            schema {
                supportPackages = listOf("com.sample")
                query {
                    Query(ref())
                }
                mutation {
                    Mutation(ref())
                }
            }
        }
    }
}

fun main() {
    app.run()
}