package org.springframework.fu.kofu.graphql

import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Flux
import java.util.*
import kotlin.collections.HashMap

class MessageInput(val title: String, val content: String)

class Message(val id: Long, val title: String, val content: String)

class MessageService {

    private val map = HashMap<Long, Message>()

    private val processor = EmitterProcessor.create<Message>()

    fun observeNewMessage(): Flux<Message> {
        return processor
    }

    fun findAll(): List<Message> {
        return map.values.toList()
    }

    fun save(message: MessageInput): Message {
        val id = UUID.randomUUID().mostSignificantBits
        return Message(id, message.title, message.content).also {
            map[id] = it
            processor.onNext(it)
        }
    }
}

class Query(private val messageService: MessageService) {

    fun findAll() = messageService.findAll()
}

class Mutation(private val messageService: MessageService) {

    fun save(message: MessageInput) = messageService.save(message)
}

class Subscription(private val messageService: MessageService) {

    fun observeNewMessage() = messageService.observeNewMessage()
}