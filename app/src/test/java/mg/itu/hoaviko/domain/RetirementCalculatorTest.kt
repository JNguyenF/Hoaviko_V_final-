package mg.itu.hoaviko.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class RetirementCalculatorTest {

    @Test
    fun `monthly rate est le taux annuel divise par 12`() {
        assertEquals(0.005, RetirementCalculator.monthlyRate(0.06), 1e-9)
        assertEquals(0.0, RetirementCalculator.monthlyRate(0.0), 1e-9)
    }

    @Test
    fun `monthsUntilRetirement calcule les mois restants`() {
        assertEquals(360, RetirementCalculator.monthsUntilRetirement(30, 60))
        assertEquals(0, RetirementCalculator.monthsUntilRetirement(60, 60))
        assertEquals(0, RetirementCalculator.monthsUntilRetirement(65, 60))
    }

    @Test
    fun `annuite sans interet est le simple produit mensualites`() {
        assertEquals(1_200_000.0, RetirementCalculator.futureValueOfAnnuity(100_000.0, 0.0, 12), 0.001)
        assertEquals(0.0, RetirementCalculator.futureValueOfAnnuity(100_000.0, 0.06, 0), 0.001)
    }

    @Test
    fun `annuite avec interet suit la formule composee`() {
        // 1000 par mois, 12% annuel (1% mensuel), 12 mois :
        // 1000 * ((1.01^12 - 1) / 0.01)
        val expected = 1000.0 * (Math.pow(1.01, 12.0) - 1.0) / 0.01
        assertEquals(expected, RetirementCalculator.futureValueOfAnnuity(1000.0, 0.12, 12), 0.01)
    }

    @Test
    fun `capital initial sans interet ne croit pas`() {
        assertEquals(500_000.0, RetirementCalculator.futureValueOfLump(500_000.0, 0.0, 24), 0.001)
        assertEquals(500_000.0, RetirementCalculator.futureValueOfLump(500_000.0, 0.12, 0), 0.001)
    }

    @Test
    fun `capital initial avec interet suit la capitalisation`() {
        val expected = 1000.0 * Math.pow(1.01, 12.0)
        assertEquals(expected, RetirementCalculator.futureValueOfLump(1000.0, 0.12, 12), 0.01)
    }

    @Test
    fun `compute produit un capital superieur au total cotise`() {
        val result = RetirementCalculator.compute(
            initialSavings = 0.0,
            monthlyContribution = 100_000.0,
            annualRate = 0.10,
            months = 360
        )
        assertEquals(360, result.months)
        assertEquals(36_000_000L, result.totalContributed)
        assert(result.projectedCapital > result.totalContributed)
        assert(result.interestEarned >= 0)
        assertEquals(30, result.yearlyProjection.size)
    }

    @Test
    fun `compute sans interet egalise le total cote`() {
        val result = RetirementCalculator.compute(
            initialSavings = 0.0,
            monthlyContribution = 50_000.0,
            annualRate = 0.0,
            months = 120
        )
        assertEquals(6_000_000L, result.projectedCapital)
        assertEquals(6_000_000L, result.totalContributed)
        assertEquals(0L, result.interestEarned)
    }

    @Test
    fun `projection annuelle respecte le nombre d annees`() {
        assertEquals(5, RetirementCalculator.yearlyProjection(0.0, 10_000.0, 0.06, 54).size)
        assertEquals(5, RetirementCalculator.yearlyProjection(0.0, 10_000.0, 0.06, 60).size)
        assertEquals(0, RetirementCalculator.yearlyProjection(0.0, 10_000.0, 0.06, 0).size)
    }
}