package org.springframework.fu.kofu.web

import org.springframework.fu.kofu.AbstractDsl

/**
 * Kofu DSL for WebFlux codecs configuration.
 */
abstract class WebFluxCodecDsl : AbstractDsl() {

    /**
     * Enable [org.springframework.core.codec.CharSequenceEncoder] and [org.springframework.core.codec.StringDecoder]
     */
    abstract fun string()

    /**
     * Enable [org.springframework.http.codec.ResourceHttpMessageWriter] and [org.springframework.core.codec.ResourceDecoder]
     */
    abstract fun resource()

    /**
     * Enable [org.springframework.http.codec.protobuf.ProtobufEncoder] and [org.springframework.http.codec.protobuf.ProtobufDecoder]
     *
     * This codec requires Protobuf 3 or higher with the official `com.google.protobuf:protobuf-java` dependency, and
     * supports `application/x-protobuf` and `application/octet-stream`.
     */
    abstract fun protobuf()

    /**
     * Enable [org.springframework.http.codec.FormHttpMessageWriter] and [org.springframework.http.codec.FormHttpMessageReader]
     */
    abstract fun form()

    /**
     * Enable [org.springframework.http.codec.multipart.MultipartHttpMessageWriter] and
     * [org.springframework.http.codec.multipart.MultipartHttpMessageReader]
     *
     * This codec requires Synchronoss NIO Multipart library via  the `org.synchronoss.cloud:nio-multipart-parser`
     * dependency.
     */
    abstract fun multipart()
}
