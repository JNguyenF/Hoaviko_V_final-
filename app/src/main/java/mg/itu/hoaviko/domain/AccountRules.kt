package mg.itu.hoaviko.domain

import java.time.LocalDate
import java.time.Period

/** Âge de retraite fixé par les règles de l'application. */
const val RETIREMENT_AGE = 60

/** Âge à partir duquel l'épargne devient accessible. */
const val WITHDRAWAL_AGE = 50

/**
 * Règles de création des comptes.
 *
 * Le nom d'utilisateur est généré automatiquement au format
 * `nom.N` où N est le numéro de rang d'inscription : ex. RAKOTO ->
 * `rakoto.3`.
 */
object AccountRules {

    /** Normalise un mot en minuscules sans accents ni caractères spéciaux. */
    fun normalizeToken(name: String): String =
        name.lowercase().replace(Regex("[^a-z0-9]"), "")

    /** Construit le nom d'utilisateur automatique à partir du nom et du rang. */
    fun usernameFor(lastName: String, rank: Int): String {
        val base = normalizeToken(lastName)
        return if (base.isEmpty()) "user.$rank" else "$base.$rank"
    }

    /** Âge révolu en années entre la naissance et une date donnée. */
    fun currentAge(birthDateEpochDay: Long, todayEpochDay: Long): Int {
        val birth = LocalDate.ofEpochDay(birthDateEpochDay)
        val today = LocalDate.ofEpochDay(todayEpochDay)
        return Period.between(birth, today).years
    }
}