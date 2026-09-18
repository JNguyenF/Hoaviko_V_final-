package mg.itu.hoaviko.domain

/** Périodes de consultation de l'historique des versements. */
enum class HistoryPeriod {
    WEEK, MONTH, YEAR, ALL;

    /** EpochDay du début de la période (null = pas de filtre). */
    fun startEpochDay(nowEpochDay: Long): Long? = when (this) {
        WEEK -> nowEpochDay - 7
        MONTH -> nowEpochDay - 30
        YEAR -> nowEpochDay - 365
        ALL -> null
    }

    fun isIncluded(dateEpochDay: Long, nowEpochDay: Long): Boolean {
        val start = startEpochDay(nowEpochDay) ?: return true
        return dateEpochDay >= start
    }
}