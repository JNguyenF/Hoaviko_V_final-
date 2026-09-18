package mg.itu.hoaviko.domain

/**
 * Règles de retrait de l'épargne.
 *
 * - Retrait < 50 ans sans préavis : pénalité HOAVIKO de 10 % du montant retiré.
 * - Retrait < 50 ans avec préavis ≥ 7 jours : pénalité de 5 %.
 * - Retrait à partir de 50 ans : aucune pénalité.
 */
object WithdrawalRules {

    /** Jours calendaires minimum pour qu'un préavis soit reconnu. */
    const val NOTICE_DAYS = 7

    const val PENALTY_IMMEDIATE = 10
    const val PENALTY_WITH_NOTICE = 5
    const val PENALTY_NONE = 0

    /** Pourcentage de pénalité selon l'âge révolu et la présence d'un préavis. */
    fun penaltyPercent(hasNotice: Boolean, currentAge: Int): Int =
        if (currentAge >= WITHDRAWAL_AGE) PENALTY_NONE
        else if (hasNotice) PENALTY_WITH_NOTICE
        else PENALTY_IMMEDIATE

    /** Date effective du retrait : demande + 7 jours avec préavis, sinon le jour même. */
    fun withdrawalDateEpochDay(requestDateEpochDay: Long, hasNotice: Boolean): Long =
        if (hasNotice) requestDateEpochDay + NOTICE_DAYS else requestDateEpochDay

    /** Montant de la pénalité (arrondi à l'unité). */
    fun penaltyAmount(amount: Long, percent: Int): Long = amount * percent / 100

    /** Montant net reçu après pénalité. */
    fun netAmount(amount: Long, percent: Int): Long = amount - penaltyAmount(amount, percent)
}