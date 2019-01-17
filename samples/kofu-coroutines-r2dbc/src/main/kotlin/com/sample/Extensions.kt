package com.sample

import io.r2dbc.spi.Row
import kotlin.reflect.KClass

// TODO Contribute to https://github.com/r2dbc/r2dbc-spi
fun <T : Any> Row.get(identifier: Any, type: KClass<T>) = get(identifier, type.java)