package mg.itu.hoaviko.domain

/** Une ligne de la projection annuelle du plan d'épargne. */
data class YearProjection(
    val year: Int,
    val totalContributed: Long,
    val capital: Long
)