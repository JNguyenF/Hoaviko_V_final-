package mg.itu.hoaviko.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Demande de retrait d'épargne envoyée à l'administrateur. */
@Entity(tableName = "withdrawals")
data class Withdrawal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val amountAriary: Long,
    val requestDateEpochDay: Long,
    val withdrawalDateEpochDay: Long,
    val hasNotice: Boolean,
    val penaltyPercent: Int,
    val status: String = STATUS_PENDING,
    val note: String = ""
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_APPROVED = "APPROVED"
        const val STATUS_REJECTED = "REJECTED"
    }
}