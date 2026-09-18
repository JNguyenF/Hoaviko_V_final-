package mg.itu.hoaviko.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Versement lié à un compte utilisateur. */
@Entity(tableName = "contributions")
data class Contribution(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val amountAriary: Long,
    val dateEpochDay: Long,
    val paymentChannelId: Long,
    val accountReference: String = "",
    val note: String = ""
)