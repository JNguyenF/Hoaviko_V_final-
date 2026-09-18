package mg.itu.hoaviko.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Compte d'un utilisateur inscrit. */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true), Index(value = ["username"], unique = true)]
)
data class UserAccount(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lastName: String,
    val firstName: String,
    val birthDateEpochDay: Long,
    val cinNumber: String,
    val profession: String,
    val email: String,
    val firebaseUid: String? = null,
    val passwordHash: String,
    val salt: String,
    val username: String,
    val registrationRank: Int,
    val language: String = "fr",
    val paymentChannelId: Long = 0,
    val savingsChannelId: Long = 0,
    val objectiveAmount: Long = 0,
    val monthlyContribution: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val role: String = ROLE_USER
) {
    val fullName: String get() = "$lastName $firstName"

    companion object {
        const val ROLE_USER = "USER"
        const val ROLE_ADMIN = "ADMIN"
    }
}