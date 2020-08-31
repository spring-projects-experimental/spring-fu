package org.springframework.samples.petclinic

import org.springframework.core.io.ClassPathResource
import org.springframework.fu.kofu.jdbc.jdbc
import org.springframework.fu.kofu.messageSource
import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webmvc.thymeleaf
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.samples.petclinic.owner.ownerConfig
import org.springframework.samples.petclinic.pet.petConfig
import org.springframework.samples.petclinic.system.systemConfig
import org.springframework.samples.petclinic.vet.vetConfig
import org.springframework.samples.petclinic.visit.visitConfig

val app = webApplication {
    messageSource {
        basename = "messages/messages"
    }
    webMvc {
        thymeleaf()
        converters {
            string()
            resource()
            jackson {
                indentOutput = true
            }
        }
        router {
            resources("/webjars/**", ClassPathResource("META-INF/resources/webjars/"))
        }
    }
    jdbc {
        schema = listOf("classpath*:db/h2/schema.sql")
        data = listOf("classpath*:db/h2/data.sql")
    }
    enable(systemConfig)
    enable(vetConfig)
    enable(ownerConfig)
    enable(visitConfig)
    enable(petConfig)
}

fun main() {
    app.run()
}