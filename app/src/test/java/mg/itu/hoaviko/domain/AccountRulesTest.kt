package mg.itu.hoaviko.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountRulesTest {

    @Test
    fun `usernameFor est en minuscules sans caracteres speciaux`() {
        assertEquals("rakoto.1", AccountRules.usernameFor("RAKOTO", 1))
        assertEquals("jeanpierre.3", AccountRules.usernameFor("Jean-Pierre", 3))
    }

    @Test
    fun `usernameFor fallback quand nom vide`() {
        assertEquals("user.1", AccountRules.usernameFor("", 1))
        assertEquals("user.5", AccountRules.usernameFor("   ", 5))
    }

    @Test
    fun `retirement ages`() {
        assertEquals(60, RETIREMENT_AGE)
        assertEquals(50, WITHDRAWAL_AGE)
    }
}

class HistoryPeriodTest {

    @Test
    fun `ALL include everything`() {
        assertTrue(HistoryPeriod.ALL.isIncluded(1000, 2000))
    }

    @Test
    fun `WEEK excludes dates older than 7 days`() {
        assertTrue(HistoryPeriod.WEEK.isIncluded(995, 1000))
        assertFalse(HistoryPeriod.WEEK.isIncluded(992, 1000))
    }

    @Test
    fun `YEAR excludes dates older than 365 days`() {
        assertTrue(HistoryPeriod.YEAR.isIncluded(640, 1000))
        assertFalse(HistoryPeriod.YEAR.isIncluded(634, 1000))
    }
}