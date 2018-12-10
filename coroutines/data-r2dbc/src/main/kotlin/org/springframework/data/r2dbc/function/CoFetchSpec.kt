package org.springframework.data.r2dbc.function

import kotlinx.coroutines.ObsoleteCoroutinesApi

/**
 * Contract for fetching results.
 *
 * @author Sebastien Deleuze
 * @author Mark Paluch
 */
interface CoFetchSpec<T> : CoRowsFetchSpec<T>, CoUpdatedRowsFetchSpec

/**
 * Contract for fetching tabular results.
 *
 * @author Sebastien Deleuze
 * @author Mark Paluch
 */
interface CoRowsFetchSpec<T> {

    /**
     * Get exactly zero or one result.
     *
     * @return value or null if no match found.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if more than one match found.
     */
    suspend fun one(): T?

    /**
     * Get the first or no result.
     *
     * @return value or null if no match found.
     */
    suspend fun first(): T?

    /**
     * Get all matching elements.
     *
     * **Note: This API will become obsolete in future updates with introduction of lazy asynchronous streams.**
     * See [issue #254](https://github.com/Kotlin/kotlinx.coroutines/issues/254).
     */
    @ObsoleteCoroutinesApi
    suspend fun all(): List<T>

}



/**
 * Contract for fetching the number of affected rows.
 *
 * @author Sebastien Deleuze
 * @author Mark Paluch
 */
interface CoUpdatedRowsFetchSpec {

    /**
     * Get the number of updated rows.
     *
     * @return the number of updated rows.
     */
    suspend fun rowsUpdated(): Int

}