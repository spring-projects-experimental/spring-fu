package org.springframework.fu.kofu.samples

fun gqlFindAll() = """
    query findAll {
        messages: findAll {
            title
        }
    }
"""

fun gqlAddMessage() = """
    mutation addMessage(${"\$message"}: MessageInput!) {
        message: save(message: ${"\$message"}) {
            id
        }
    }
"""

fun graphqlQuery() = """
    ${gqlFindAll()}
    ${gqlAddMessage()}
    subscription newMessage {
        message: observeNewMessage {
            title
            content
        }
    }
"""