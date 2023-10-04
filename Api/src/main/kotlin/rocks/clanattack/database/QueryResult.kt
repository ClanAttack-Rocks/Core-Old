package rocks.clanattack.database

/**
 * A [QueryResult] represents the result of a query.
 */
data class QueryResult<T>(val status: String, val time: String, val result: List<T>)
