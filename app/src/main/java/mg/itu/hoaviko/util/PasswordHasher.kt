package mg.itu.hoaviko.util

import java.security.MessageDigest
import kotlin.random.Random

/** Hachage des mots de passe (SHA-256 + sel par utilisateur). */
object PasswordHasher {

    /** Génère un sel aléatoire hexadécimal (16 octets). */
    fun newSalt(): String {
        val bytes = ByteArray(16)
        Random.nextBytes(bytes)
        return bytes.toHex()
    }

    /** Hachage déterministe = SHA-256(sel + mot de passe). */
    fun hash(password: String, salt: String): String {
        val input = (salt + password).toByteArray(Charsets.UTF_8)
        return MessageDigest.getInstance("SHA-256").digest(input).toHex()
    }

    fun verify(password: String, salt: String, expectedHash: String): Boolean =
        hash(password, salt) == expectedHash

    private fun ByteArray.toHex(): String =
        joinToString(separator = "") { byte -> "%02x".format(byte) }
}