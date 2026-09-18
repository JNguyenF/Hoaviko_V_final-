package mg.itu.hoaviko.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    @Test
    fun `hash est deterministe`() {
        val salt = PasswordHasher.newSalt()
        assertEquals(PasswordHasher.hash("test", salt), PasswordHasher.hash("test", salt))
    }

    @Test
    fun `mots de passe differents produisent des hash differents`() {
        val salt = PasswordHasher.newSalt()
        assertNotEquals(PasswordHasher.hash("abc", salt), PasswordHasher.hash("xyz", salt))
    }

    @Test
    fun `sel different donne hash different`() {
        assertNotEquals(
            PasswordHasher.hash("abc", PasswordHasher.newSalt()),
            PasswordHasher.hash("abc", PasswordHasher.newSalt())
        )
    }

    @Test
    fun `verify fonctionne`() {
        val salt = PasswordHasher.newSalt()
        val h = PasswordHasher.hash("secret", salt)
        assertTrue(PasswordHasher.verify("secret", salt, h))
    }

    private fun assertNotEquals(a: String, b: String) =
        org.junit.Assert.assertNotEquals(a, b)
}