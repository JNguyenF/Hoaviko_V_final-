package mg.itu.hoaviko.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class WithdrawalRulesTest {

    @Test
    fun `penalty is null from 50 years`() {
        assertEquals(0, WithdrawalRules.penaltyPercent(hasNotice = false, currentAge = 50))
        assertEquals(0, WithdrawalRules.penaltyPercent(hasNotice = true, currentAge = 50))
        assertEquals(0, WithdrawalRules.penaltyPercent(hasNotice = false, currentAge = 60))
        assertEquals(0, WithdrawalRules.penaltyPercent(hasNotice = true, currentAge = 61))
    }

    @Test
    fun `penalty before 50 depends on notice`() {
        assertEquals(10, WithdrawalRules.penaltyPercent(hasNotice = false, currentAge = 49))
        assertEquals(5, WithdrawalRules.penaltyPercent(hasNotice = true, currentAge = 49))
        assertEquals(10, WithdrawalRules.penaltyPercent(hasNotice = false, currentAge = 20))
    }

    @Test
    fun `withdrawal date adds 7 days with notice`() {
        assertEquals(1000, WithdrawalRules.withdrawalDateEpochDay(1000, hasNotice = false))
        assertEquals(1007, WithdrawalRules.withdrawalDateEpochDay(1000, hasNotice = true))
    }

    @Test
    fun `penalty amount and net`() {
        assertEquals(100_000, WithdrawalRules.penaltyAmount(1_000_000, 10))
        assertEquals(900_000, WithdrawalRules.netAmount(1_000_000, 10))
        assertEquals(50_000, WithdrawalRules.penaltyAmount(1_000_000, 5))
        assertEquals(0, WithdrawalRules.penaltyAmount(1_000_000, 0))
        assertEquals(1_000_000, WithdrawalRules.netAmount(1_000_000, 0))
    }
}