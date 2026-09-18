package mg.itu.hoaviko.domain

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Moteur de calcul d'épargne retraite.
 *
 * Le taux annuel est mensualisé (i = taux annuel / 12). Le capital final est la
 * somme de la valeur acquise des versements mensuels (annuité) et de la valeur
 * acquise de l'épargne initiale (capitalisation compoundée) :
 *
 *   FV_annuité(m) = C * ((1 + i)^n - 1) / i
 *   FV_initial(P) = P * (1 + i)^n
 *
 * avec n = nombre de mois jusqu'à la retraite.
 */
object RetirementCalculator {

    /** Taux mensuel à partir du taux annuel (ex. 0.06 -> 0.005). */
    fun monthlyRate(annualRate: Double): Double = annualRate / 12.0

    /** Nombre de mois restants avant la retraite. */
    fun monthsUntilRetirement(currentAge: Int, retirementAge: Int): Int =
        max(0, (retirementAge - currentAge) * 12)

    /** Valeur acquise d'une suite de versements identiques versés en début/fin de période. */
    fun futureValueOfAnnuity(monthlyContribution: Double, annualRate: Double, months: Int): Double {
        if (months <= 0) return 0.0
        val i = monthlyRate(annualRate)
        if (i == 0.0) return monthlyContribution * months
        return monthlyContribution * ((1.0 + i).pow(months) - 1.0) / i
    }

    /** Valeur acquise d'un capital initial placé. */
    fun futureValueOfLump(principal: Double, annualRate: Double, months: Int): Double {
        if (months <= 0 || principal == 0.0) return principal
        return principal * (1.0 + monthlyRate(annualRate)).pow(months)
    }

    /** Calcul complet d'un plan d'épargne retraite. */
    fun compute(
        initialSavings: Double,
        monthlyContribution: Double,
        annualRate: Double,
        months: Int
    ): RetirementSummary {
        val monthsClamped = max(0, months)
        val capital =
            futureValueOfLump(initialSavings, annualRate, monthsClamped) +
                futureValueOfAnnuity(monthlyContribution, annualRate, monthsClamped)
        val contributed = initialSavings + monthlyContribution * monthsClamped
        val interest = (capital - contributed).roundToLong().coerceAtLeast(0)
        return RetirementSummary(
            months = monthsClamped,
            monthlyContribution = monthlyContribution.roundToLong(),
            annualRate = annualRate,
            initialSavings = initialSavings.roundToLong(),
            totalContributed = contributed.roundToLong(),
            projectedCapital = capital.roundToLong(),
            interestEarned = interest,
            yearlyProjection = yearlyProjection(initialSavings, monthlyContribution, annualRate, monthsClamped)
        )
    }

    /** Projection année par année avec un pas de 12 mois. */
    fun yearlyProjection(
        initialSavings: Double,
        monthlyContribution: Double,
        annualRate: Double,
        months: Int,
        step: Int = 12
    ): List<YearProjection> {
        if (months <= 0) return emptyList()
        val fullYears = months / step
        val totalYears = if (months % step == 0) fullYears else fullYears + 1
        return (1..totalYears).map { year ->
            val m = (year * step).coerceAtMost(months)
            val capital =
                futureValueOfLump(initialSavings, annualRate, m) +
                    futureValueOfAnnuity(monthlyContribution, annualRate, m)
            val contributed = initialSavings + monthlyContribution * m
            YearProjection(
                year = year,
                totalContributed = contributed.roundToLong(),
                capital = capital.roundToLong()
            )
        }
    }
}