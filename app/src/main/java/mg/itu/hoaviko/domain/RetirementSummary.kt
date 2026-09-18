package mg.itu.hoaviko.domain

/** Résumé chiffré d'un plan d'épargne retraite. */
data class RetirementSummary(
    val months: Int,
    val monthlyContribution: Long,
    val annualRate: Double,
    val initialSavings: Long,
    val totalContributed: Long,
    val projectedCapital: Long,
    val interestEarned: Long,
    val yearlyProjection: List<YearProjection>
)