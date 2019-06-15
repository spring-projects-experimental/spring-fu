package org.springframework.fu.kofu.graphql

import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.toSchema
import graphql.schema.GraphQLSchema
import org.springframework.context.support.BeanDefinitionDsl.BeanSupplierContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.KofuMarker

@KofuMarker
class GqlSchemaDsl(private val context: GenericApplicationContext) {

    private val queries = mutableListOf<TopLevelObject>()

    private val mutations = mutableListOf<TopLevelObject>()

    private val subscriptions = mutableListOf<TopLevelObject>()

    var supportPackages: List<String> = emptyList()

    fun <T : Any> query(init: BeanSupplierContext.() -> T) {
        queries += TopLevelObject(init(BeanSupplierContext(context)))
    }

    fun <T : Any> mutation(init: BeanSupplierContext.() -> T) {
        mutations += TopLevelObject(init(BeanSupplierContext(context)))
    }

    fun <T : Any> subscription(init: BeanSupplierContext.() -> T) {
        subscriptions += TopLevelObject(init(BeanSupplierContext(context)))
    }

    internal fun build(): GraphQLSchema {
        val config = SchemaGeneratorConfig(supportPackages)
        return toSchema(config, queries, mutations, subscriptions)
    }
}