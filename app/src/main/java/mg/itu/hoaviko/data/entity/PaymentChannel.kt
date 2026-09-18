package mg.itu.hoaviko.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Canal de paiement : mobile money (MVola, Orange Money, Airtel Money) ou
 * banque (BRED, BNI, BMOI, SIPEM, BOA) avec taux d'intérêt.
 */
@Entity(tableName = "payment_channels")
data class PaymentChannel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,       // "MobileMoney" | "Banque"
    val annualRate: Double,     // 0.0 pour mobile money
    val minAmount: Long,
    val maxAmount: Long
)