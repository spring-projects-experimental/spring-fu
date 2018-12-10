package org.springframework.data.r2dbc.function

import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import org.reactivestreams.Publisher
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.function.DatabaseClient.BindSpec
import org.springframework.data.r2dbc.support.R2dbcExceptionTranslator
import kotlin.reflect.KClass


interface CoDatabaseClient {

    /**
     * Prepare an SQL call returning a result.
     */
    fun execute(): CoSqlSpec

    /**
     * Prepare an SQL SELECT call.
     */
    fun select(): CoSelectFromSpec

    /**
     * Prepare an SQL INSERT call.
     */
    fun insert(): CoInsertIntoSpec

    /**
     * Return a builder to mutate properties of this database client.
     */
    fun mutate(): CoDatabaseClient.Builder

    companion object {
        /**
         * A variant of [.create] that accepts a [io.r2dbc.spi.ConnectionFactory]
         */
        fun create(factory: ConnectionFactory): CoDatabaseClient {
            return CoDatabaseClientBuilder().connectionFactory(factory).build()
        }

        /**
         * Obtain a `DatabaseClient` builder.
         */
        fun builder(): CoDatabaseClient.Builder {
            return CoDatabaseClientBuilder()
        }
    }

    /**
     * A mutable builder for creating a [CoDatabaseClient].
     */
    interface Builder {

        /**
         * Configures the [R2DBC connector][ConnectionFactory].
         *
         * @param factory must not be null.
         * @return `this` [Builder].
         */
        fun connectionFactory(factory: ConnectionFactory): Builder

        /**
         * Configures a [R2dbcExceptionTranslator].
         *
         * @param exceptionTranslator must not be null.
         * @return `this` [Builder].
         */
        fun exceptionTranslator(exceptionTranslator: R2dbcExceptionTranslator): Builder

        /**
         * Configures a [ReactiveDataAccessStrategy].
         *
         * @param accessStrategy must not be null.
         * @return `this` [Builder].
         */
        fun dataAccessStrategy(accessStrategy: ReactiveDataAccessStrategy): Builder

        /**
         * Configures a consumer to configure this builder.
         *
         * @param builderConsumer must not be null.
         * @return `this` [Builder].
         */
        fun apply(builderConsumer: (Builder) -> Unit): Builder

        /**
         * Builder the [CoDatabaseClient] instance.
         */
        fun build(): CoDatabaseClient
    }


    /**
     * Contract for specifying a SQL call along with options leading to the exchange.
     */
    interface CoSqlSpec {

        /**
         * Specify a static `sql` string to execute.
         *
         * @param sql must not be null or empty.
         * @return a new [CoGenericExecuteSpec].
         */
        fun sql(sql: String): CoGenericExecuteSpec

        /**
         * Specify a static SQL supplier that provides SQL to execute.
         *
         * @param sqlSupplier must not be null.
         * @return a new [CoGenericExecuteSpec].
         */
        fun sql(sqlSupplier: () -> String): CoGenericExecuteSpec
    }

    /**
     * Contract for specifying a SQL call along with options leading to the exchange.
     */
    interface CoGenericExecuteSpec : BindSpec<CoGenericExecuteSpec> {

        /**
         * Define the target type the result should be mapped to. <br></br>
         * Skip this step if you are anyway fine with the default conversion.
         *
         * @param <R> result type.
         */
        fun <R: Any> asType(resultType: KClass<R>): CoTypedExecuteSpec<R>

        /**
         * Perform the SQL call and retrieve the result.
         */
        fun fetch(): CoFetchSpec<Map<String, Any>>

        /**
         * Perform the SQL request.
         */
        suspend fun execute()
    }

    /**
     * Contract for specifying a SQL call along with options leading to the exchange.
     */
    interface CoTypedExecuteSpec<T> : BindSpec<CoTypedExecuteSpec<T>> {

        /**
         * Define the target type the result should be mapped to. <br></br>
         * Skip this step if you are anyway fine with the default conversion.
         *
         * @param <R> result type.
         */
        fun <R: Any> asType(resultType: KClass<R>): CoTypedExecuteSpec<R>

        /**
         * Perform the SQL call and retrieve the result.
         */
        fun fetch(): CoFetchSpec<T>

        /**
         * Configure a result mapping function.
         *
         * @param mappingFunction must not be {@literal null}.
         * @return a {@link RowsFetchSpec} for configuration what to fetch.
         */
        fun <R> map(mappingFunction: (Row, RowMetadata) -> R): CoRowsFetchSpec<R>

        /**
         * Perform the SQL request.
         *
         * @return the result
         */
        suspend fun execute()
    }

    /**
     * Contract for specifying `SELECT` options leading to the exchange.
     */
    interface CoSelectFromSpec {

        /**
         * Specify the source table to select from.
         *
         * @param table must not be null or empty.
         * @return
         */
        fun from(table: String): CoGenericSelectSpec

        /**
         * Specify the source table to select from to using the entity class.
         *
         * @param <R> the entity class.
         * @return
         */
        fun <T: Any> from(table: KClass<T>): CoTypedSelectSpec<T>
    }

    /**
     * Contract for specifying `SELECT` options leading to the exchange.
     */
    interface CoInsertIntoSpec {

        /**
         * Specify the target table to insert into.
         *
         * @param table must not be null or empty.
         * @return
         */
        fun into(table: String): CoGenericInsertSpec<Map<String, Any>>

        /**
         * Specify the target table to insert to using the entity class.
         *
         * @param <R> the entity class.
         * @return
         */
        fun <T: Any> into(table: KClass<T>): CoTypedInsertSpec<T>
    }

    /**
     * Contract for specifying `SELECT` options leading to the exchange.
     */
    interface CoGenericSelectSpec : CoSelectSpec<CoGenericSelectSpec> {

        /**
         * Define the target type the result should be mapped to. <br></br>
         * Skip this step if you are anyway fine with the default conversion.
         *
         * @param <R> result type.
        */
        fun <R: Any> asType(resultType: KClass<R>): CoTypedSelectSpec<R>

        /**
         * Perform the SQL call and retrieve the result.
         */
        fun fetch(): CoFetchSpec<Map<String, Any>>

        /**
         * Configure a result mapping function.
         *
         * @param mappingFunction must not be {@literal null}.
         * @return a {@link RowsFetchSpec} for configuration what to fetch.
         */
        fun <R> map(mappingFunction: (Row, RowMetadata) -> R): CoRowsFetchSpec<R>
    }


    /**
     * Contract for specifying `SELECT` options leading to the exchange.
     */
    interface CoTypedSelectSpec<T> : CoSelectSpec<CoTypedSelectSpec<T>> {

        /**
         * Define the target type the result should be mapped to. <br></br>
         * Skip this step if you are anyway fine with the default conversion.
         *
         * @param <R> result type.
        */
        fun <R: Any> asType(resultType: KClass<R>): CoRowsFetchSpec<R>

        /**
         * Configure a result mapping function.
         *
         * @param mappingFunction must not be {@literal null}.
         * @return a {@link RowsFetchSpec} for configuration what to fetch.
         */
        fun <R> map(mappingFunction: (Row, RowMetadata) -> R): CoRowsFetchSpec<R>

        /**
         * Perform the SQL call and retrieve the result.
         */
        fun fetch(): CoFetchSpec<T>

    }

    /**
     * Contract for specifying `SELECT` options leading to the exchange.
     */
    interface CoSelectSpec<S : CoSelectSpec<S>> {

        /**
         * Configure projected fields.
         *
         * @param selectedFields must not be null.
         */
        fun project(vararg selectedFields: String): S

        /**
         * Configure [Sort].
         *
         * @param sort must not be null.
         */
        fun orderBy(sort: Sort): S

        /**
         * Configure pagination. Overrides [Sort] if the [Pageable] contains a [Sort] object.
         *
         * @param page must not be null.
         */
        fun page(page: Pageable): S
    }

    /**
     * Contract for specifying `INSERT` options leading to the exchange.
     */
    interface CoGenericInsertSpec<T> : CoInsertSpec<T> {

        /**
         * Specify a field and non-null value to insert.
         *
         * @param field must not be null or empty.
         * @param value must not be null
         */
        fun value(field: String, value: Any): CoGenericInsertSpec<T>

        /**
         * Specify a null value to insert.
         *
         * @param field must not be null or empty.
         * @param type must not be null.
         */
        fun nullValue(field: String, type: KClass<*>): CoGenericInsertSpec<T>
    }

    /**
     * Contract for specifying `SELECT` options leading the exchange.
     */
    interface CoTypedInsertSpec<T> {

        /**
         * Insert the given `objectToInsert`.
         *
         * @param objectToInsert the object of which the attributes will provide the values for the insert.
         * @return a [CoInsertSpec] for further configuration of the insert.
         */
        fun using(objectToInsert: T): CoInsertSpec<Map<String, Any>>

        /**
         * Use the given `tableName` as insert target.
         *
         * @param tableName must not be null or empty.
         * @return
         */
        fun table(tableName: String): CoTypedInsertSpec<T>
    }

    /**
     * Contract for specifying `INSERT` options leading to the exchange.
     */
    interface CoInsertSpec <T> {

        /**
         * Perform the SQL call.
         */
        suspend fun execute()

        /**
         * Perform the SQL call and retrieve the result.
         */
        suspend fun fetch(): CoFetchSpec<T>

        /**
         * Configure a result mapping function.
         *
         * @param mappingFunction must not be {@literal null}.
         * @return a {@link RowsFetchSpec} for configuration what to fetch.
         */
        fun <R> map(mappingFunction: (Row, RowMetadata) -> R): CoRowsFetchSpec<R>
    }

}