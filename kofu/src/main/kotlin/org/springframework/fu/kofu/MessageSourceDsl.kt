package org.springframework.fu.kofu

import org.springframework.boot.autoconfigure.context.MessageSourceInitializer
import org.springframework.boot.autoconfigure.context.MessageSourceProperties
import org.springframework.context.support.GenericApplicationContext

class MessageSourceDsl(private val init: MessageSourceDsl.() -> Unit) : AbstractDsl() {

    var basename = "messages"

    override fun initialize(context: GenericApplicationContext) {
        super.initialize(context)
        init()

        val messageSourceProperties = MessageSourceProperties().apply {
            basename = this@MessageSourceDsl.basename
        }
        MessageSourceInitializer(messageSourceProperties).initialize(context)
    }
}

/**
 * Configure MessageSource support.
 * @see MessageSourceDsl
 */
fun ConfigurationDsl.messageSource(dsl: MessageSourceDsl.() -> Unit = {}) {
    MessageSourceDsl(dsl).initialize(context)
}