package org.springframework.data.r2dbc.function

import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.reactivestreams.Publisher
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.support.R2dbcExceptionTranslator
import kotlin.reflect.KClass

class CoDatabaseClientBuilder: CoDatabaseClient.Builder {

    private val defaultBuilder = DefaultDatabaseClientBuilder()

    override fun connectionFactory(factory: ConnectionFactory): CoDatabaseClient.Builder {
        defaultBuilder.connectionFactory(factory)
        return this
    }

    override fun exceptionTranslator(exceptionTranslator: R2dbcExceptionTranslator): CoDatabaseClient.Builder {
        defaultBuilder.exceptionTranslator(exceptionTranslator)
        return this
    }

    override fun dataAccessStrategy(accessStrategy: ReactiveDataAccessStrategy): CoDatabaseClient.Builder {
        defaultBuilder.dataAccessStrategy(accessStrategy)
        return this
    }

    override fun apply(builderConsumer: (CoDatabaseClient.Builder) -> Unit): CoDatabaseClient.Builder {
        builderConsumer.invoke(this)
        return this
    }

    override fun build(): CoDatabaseClient = CoDatabaseClientAdapter(defaultBuilder.build())

    private class CoDatabaseClientAdapter(private val client: DatabaseClient) : CoDatabaseClient {

        override fun execute(): CoDatabaseClient.CoSqlSpec = SqlSpecAdapter(client.execute())

        override fun select(): CoDatabaseClient.CoSelectFromSpec = SelectFromSpecAdapter(client.select())

        override fun insert(): CoDatabaseClient.CoInsertIntoSpec = InsertIntoSpecAdapter(client.insert())

        override fun mutate(): CoDatabaseClient.Builder = BuilderAdapter(client.mutate())

        private class SqlSpecAdapter(private val spec: DatabaseClient.SqlSpec): CoDatabaseClient.CoSqlSpec {

            override fun sql(sql: String): CoDatabaseClient.CoGenericExecuteSpec = GenericExecuteSpecAdapter(spec.sql(sql))

            override fun sql(sqlSupplier: () -> String): CoDatabaseClient.CoGenericExecuteSpec = GenericExecuteSpecAdapter(spec.sql(sqlSupplier))

            private class GenericExecuteSpecAdapter(private val spec: DatabaseClient.GenericExecuteSpec) : CoDatabaseClient.CoGenericExecuteSpec {

                override fun <R : Any> asType(resultType: KClass<R>): CoDatabaseClient.CoTypedExecuteSpec<R> = TypedExecuteSpecAdapter(spec.`as`(resultType.java))

                override fun fetch(): CoFetchSpec<Map<String, Any>> = FetchSpecAdapter(spec.fetch())

                override suspend fun exchange(): CoSqlResult<Map<String, Any>> = spec.exchange().map { SqlResultAdapter(it) }.awaitFirst()

                override fun bind(index: Int, value: Any?): CoDatabaseClient.CoGenericExecuteSpec = GenericExecuteSpecAdapter(spec.bind(index, value))

                override fun bind(name: String?, value: Any?): CoDatabaseClient.CoGenericExecuteSpec = GenericExecuteSpecAdapter(spec.bind(name, value))

                override fun bind(bean: Any?): CoDatabaseClient.CoGenericExecuteSpec = GenericExecuteSpecAdapter(spec.bind(bean))

                override fun bindNull(index: Int, type: Class<*>?): CoDatabaseClient.CoGenericExecuteSpec = GenericExecuteSpecAdapter(spec.bindNull(index, type))

                override fun bindNull(name: String?, type: Class<*>?): CoDatabaseClient.CoGenericExecuteSpec = GenericExecuteSpecAdapter(spec.bindNull(name, type))
            }
        }

        private open class FetchSpecAdapter<T>(private val spec: FetchSpec<T>): CoFetchSpec<T> {

            override suspend fun one(): T? = spec.one().awaitFirstOrNull()

            override suspend fun first(): T? = spec.first().awaitFirstOrNull()

            @ObsoleteCoroutinesApi
            override suspend fun all(): List<T> = spec.all().collectList().awaitFirst()

            override suspend fun rowsUpdated(): Int = spec.rowsUpdated().awaitFirst()
        }

        private class SqlResultAdapter<T>(private val spec: SqlResult<T>): CoSqlResult<T>, FetchSpecAdapter<T>(spec) {

            override fun <R> extract(mappingFunction: (Row, RowMetadata) -> R): CoSqlResult<R> =
                    SqlResultAdapter(spec.extract { t, u -> mappingFunction.invoke(t, u) })

        }

        private class TypedExecuteSpecAdapter<T>(private val spec: DatabaseClient.TypedExecuteSpec<T>): CoDatabaseClient.CoTypedExecuteSpec<T> {

            override fun <R : Any> asType(resultType: KClass<R>): CoDatabaseClient.CoTypedExecuteSpec<R>
                    = TypedExecuteSpecAdapter(spec.`as`(resultType.java))

            override fun fetch(): CoFetchSpec<T> = FetchSpecAdapter(spec.fetch())

            override suspend fun exchange(): CoSqlResult<T> =
                    spec.exchange().map { SqlResultAdapter(it) }.awaitFirst()

            override fun bind(index: Int, value: Any?): CoDatabaseClient.CoTypedExecuteSpec<T>
                    = TypedExecuteSpecAdapter(spec.bind(index, value))

            override fun bind(name: String?, value: Any?): CoDatabaseClient.CoTypedExecuteSpec<T>
                    = TypedExecuteSpecAdapter(spec.bind(name, value))

            override fun bind(bean: Any?): CoDatabaseClient.CoTypedExecuteSpec<T>
                    = TypedExecuteSpecAdapter(spec.bind(bean))

            override fun bindNull(index: Int, type: Class<*>?): CoDatabaseClient.CoTypedExecuteSpec<T>
                    = TypedExecuteSpecAdapter(spec.bindNull(index, type))

            override fun bindNull(name: String?, type: Class<*>?): CoDatabaseClient.CoTypedExecuteSpec<T>
                    = TypedExecuteSpecAdapter(spec.bindNull(name, type))
        }

        private class SelectFromSpecAdapter(private val spec: DatabaseClient.SelectFromSpec): CoDatabaseClient.CoSelectFromSpec {

            override fun from(table: String): CoDatabaseClient.CoGenericSelectSpec
                    = GenericSelectSpecAdapter(spec.from(table))

            override fun <T : Any> from(table: KClass<T>): CoDatabaseClient.CoTypedSelectSpec<T>
                    = TypedSelectSpecAdapter(spec.from(table.java))
        }

        private class GenericSelectSpecAdapter(private val spec: DatabaseClient.GenericSelectSpec): CoDatabaseClient.CoGenericSelectSpec {

            override fun <R : Any> asType(resultType: KClass<R>): CoDatabaseClient.CoTypedSelectSpec<R>
                    = TypedSelectSpecAdapter(spec.`as`(resultType.java))

            override fun fetch(): CoFetchSpec<Map<String, Any>>
                    = FetchSpecAdapter(spec.fetch())

            override suspend fun exchange(): CoSqlResult<Map<String, Any>>
                    = spec.exchange().map { SqlResultAdapter(it) }.awaitFirst()

            override fun project(vararg selectedFields: String): CoDatabaseClient.CoGenericSelectSpec
                    = GenericSelectSpecAdapter(spec.project(*selectedFields))

            override fun orderBy(sort: Sort): CoDatabaseClient.CoGenericSelectSpec
                    = GenericSelectSpecAdapter(spec.orderBy(sort))

            override fun page(page: Pageable): CoDatabaseClient.CoGenericSelectSpec
                    = GenericSelectSpecAdapter(spec.page(page))
        }

        private class TypedSelectSpecAdapter<R>(val spec: DatabaseClient.TypedSelectSpec<R>) : CoDatabaseClient.CoTypedSelectSpec<R> {

            override fun <R : Any> asType(resultType: KClass<R>): CoDatabaseClient.CoTypedSelectSpec<R>
                    = TypedSelectSpecAdapter(spec.`as`(resultType.java))

            override fun <R> extract(mappingFunction: (Row, RowMetadata) -> R): CoDatabaseClient.CoTypedSelectSpec<R>
                    = TypedSelectSpecAdapter(spec.extract(mappingFunction))

            override fun fetch(): CoFetchSpec<R>
                    = FetchSpecAdapter(spec.fetch())

            override suspend fun exchange(): CoSqlResult<R>
                    = spec.exchange().map { SqlResultAdapter(it) }.awaitFirst()

            override fun project(vararg selectedFields: String): CoDatabaseClient.CoTypedSelectSpec<R>
                    = TypedSelectSpecAdapter(spec.project(*selectedFields))

            override fun orderBy(sort: Sort): CoDatabaseClient.CoTypedSelectSpec<R>
                    = TypedSelectSpecAdapter(spec.orderBy(sort))

            override fun page(page: Pageable): CoDatabaseClient.CoTypedSelectSpec<R>
                    = TypedSelectSpecAdapter(spec.page(page))

        }

        private class InsertIntoSpecAdapter(private val spec: DatabaseClient.InsertIntoSpec): CoDatabaseClient.CoInsertIntoSpec {

            override fun into(table: String): CoDatabaseClient.CoGenericInsertSpec
                    = GenericInsertSpecAdapter(spec.into(table))

            override fun <T : Any> into(table: KClass<T>): CoDatabaseClient.CoTypedInsertSpec<T>
                    = TypedInsertSpecAdapter(spec.into(table.java))
        }

        private class GenericInsertSpecAdapter(private val spec: DatabaseClient.GenericInsertSpec): CoDatabaseClient.CoGenericInsertSpec {

            override fun value(field: String, value: Any): CoDatabaseClient.CoGenericInsertSpec
                    = GenericInsertSpecAdapter(spec.value(field, value))

            override fun nullValue(field: String, type: KClass<*>): CoDatabaseClient.CoGenericInsertSpec
                    = GenericInsertSpecAdapter(spec.nullValue(field, type.java))

            override suspend fun then() {
                spec.then().awaitFirstOrNull()
            }

            override suspend fun exchange(): CoSqlResult<Map<String, Any>>
                    = spec.exchange().map { SqlResultAdapter(it) }.awaitFirst()
        }

        private class TypedInsertSpecAdapter<T>(val spec: DatabaseClient.TypedInsertSpec<T>): CoDatabaseClient.CoTypedInsertSpec<T> {

            override fun using(objectToInsert: T): CoDatabaseClient.CoInsertSpec
                    = InsertSpecAdapter(spec.using(objectToInsert))

            override fun table(tableName: String): CoDatabaseClient.CoTypedInsertSpec<T>
                    = TypedInsertSpecAdapter(spec.table(tableName))

            override fun using(objectToInsert: Publisher<T>): CoDatabaseClient.CoInsertSpec
                    = InsertSpecAdapter(spec.using(objectToInsert))
        }

        private class InsertSpecAdapter(val spec: DatabaseClient.InsertSpec): CoDatabaseClient.CoInsertSpec {

            override suspend fun then() {
                spec.then().awaitFirstOrNull()
            }

            override suspend fun exchange(): CoSqlResult<Map<String, Any>>
                    = spec.exchange().map { SqlResultAdapter(it) }.awaitFirst()
        }

        private class BuilderAdapter(val builder: DatabaseClient.Builder): CoDatabaseClient.Builder {

            override fun connectionFactory(factory: ConnectionFactory): CoDatabaseClient.Builder {
                builder.connectionFactory(factory)
                return this
            }

            override fun exceptionTranslator(exceptionTranslator: R2dbcExceptionTranslator): CoDatabaseClient.Builder {
                builder.exceptionTranslator(exceptionTranslator)
                return this
            }

            override fun dataAccessStrategy(accessStrategy: ReactiveDataAccessStrategy): CoDatabaseClient.Builder {
                builder.dataAccessStrategy(accessStrategy)
                return this
            }

            override fun apply(builderConsumer: (CoDatabaseClient.Builder) -> Unit): CoDatabaseClient.Builder {
                builderConsumer.invoke(this)
                return this
            }

            override fun build(): CoDatabaseClient = CoDatabaseClientAdapter(builder.build())
        }



    }
}