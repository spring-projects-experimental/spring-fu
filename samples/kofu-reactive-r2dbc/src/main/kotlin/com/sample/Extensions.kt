package com.sample

import io.r2dbc.spi.Row
import org.springframework.data.r2dbc.function.DatabaseClient
import kotlin.reflect.KClass

// TODO Contribute to in https://github.com/spring-projects/spring-data-r2dbc/
fun <R : Any> DatabaseClient.GenericSelectSpec.asType(resultType: KClass<R>): DatabaseClient.TypedSelectSpec<R> = `as`(resultType.java)

// TODO Contribute to in https://github.com/spring-projects/spring-data-r2dbc/
fun <R : Any> DatabaseClient.GenericExecuteSpec.asType(resultType: KClass<R>): DatabaseClient.TypedExecuteSpec<R> = `as`(resultType.java)

// TODO Contribute to https://github.com/spring-projects/spring-data-r2dbc/
fun <T : Any> DatabaseClient.InsertIntoSpec.into(table: KClass<T>) : DatabaseClient.TypedInsertSpec<T> = into(table.java)

// TODO Contribute to https://github.com/r2dbc/r2dbc-spi
fun <T : Any> Row.get(identifier: Any, type: KClass<T>) = get(identifier, type.java)