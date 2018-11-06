package org.springframework.data.r2dbc.function

import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata


/**
 * Mappable [CoFetchSpec] that accepts a mapping function to map SQL [Row]s.
 *
 * @author Sebastien Deleuze
 * @author Mark Paluch
 */
interface CoSqlResult<T> : CoFetchSpec<T> {

    /**
     * Apply a mapping function to the result that emits [Row]s.
     *
     * @param mappingFunction must not be null.
     * @param <R>
     * @return a new [CoSqlResult] with mapping function applied.
    </R> */
    fun <R> extract(mappingFunction: (Row, RowMetadata) -> R): CoSqlResult<R>
}