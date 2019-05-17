package org.springframework.fu.kofu.cassandra

import org.springframework.boot.autoconfigure.cassandra.CassandraInitializer
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataInitializer
import org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.ConfigurationDsl

/**
 * Kofu DSL for Reactive Cassandra configuration.
 *
 * Enable and configure Reactive Cassandra support by registering [org.springframework.data.cassandra.core.ReactiveCassandraTemplate]
 *
 * Required dependencies can be retrieved using `org.springframework.boot:spring-boot-starter-data-cassandra-reactive`.
 * @sample org.springframework.fu.kofu.samples.reactiveCassandra
 */
open class ReactiveCassandraDsl(private val initBlock: ReactiveCassandraDsl.() -> Unit) : CassandraDsl({}) {
	override fun initialize(context: GenericApplicationContext){
		super.initialize(context)
		initBlock()
		CassandraInitializer(properties).initialize(context)
		CassandraDataInitializer(properties).initialize(context)
		CassandraReactiveDataInitializer().initialize(context)
	}
}

/**
 * Configure Reactive Cassandra support.
 * @see ReactiveCassandraDsl
 */
fun ConfigurationDsl.reactiveCassandra(dsl: ReactiveCassandraDsl.() -> Unit = {}) {
	ReactiveCassandraDsl(dsl).initialize(context)
}
