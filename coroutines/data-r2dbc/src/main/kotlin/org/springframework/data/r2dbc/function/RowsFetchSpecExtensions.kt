package org.springframework.data.r2dbc.function

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle

/**
 * Coroutines variant of [RowsFetchSpec.one].
 *
 * @author Sebastien Deleuze
 */
suspend fun <T> RowsFetchSpec<T>.awaitOne() = one().awaitFirstOrNull()

/**
 * Coroutines variant of [RowsFetchSpec.first].
 *
 * @author Sebastien Deleuze
 */
suspend fun <T> RowsFetchSpec<T>.awaitFirst() = first().awaitFirstOrNull()

/**
 * Coroutines variant of [RowsFetchSpec.all].
 *
 * **Note: This API will become obsolete in future updates with introduction of lazy asynchronous streams.**
 * See [issue #254](https://github.com/Kotlin/kotlinx.coroutines/issues/254).
 *
 * @author Sebastien Deleuze
 */
@ObsoleteCoroutinesApi
suspend fun <T> RowsFetchSpec<T>.awaitAll() = all().collectList().awaitSingle()
