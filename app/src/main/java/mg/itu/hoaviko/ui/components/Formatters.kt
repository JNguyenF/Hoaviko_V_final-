package mg.itu.hoaviko.ui.components

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToLong

private val integerFormat = NumberFormat.getIntegerInstance(Locale.FRANCE)
private val decimalFormat = NumberFormat.getNumberInstance(Locale.FRANCE)

/** Formate un montant en ariary, ex. "1 234 567 Ar". */
fun formatAriary(amount: Long): String = "${integerFormat.format(amount)} Ar"

/** Formate un montant double en ariary. */
fun formatAriary(amount: Double): String = formatAriary(amount.roundToLong())

/** Formate un taux annuel, ex. 0.06 -> "6 %". */
fun formatPercent(annualRate: Double): String {
    val percent = annualRate * 100.0
    return if (percent % 1.0 == 0.0) {
        "${percent.toInt()} %"
    } else {
        "${decimalFormat.format(percent)} %"
    }
}

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

/** Formate une date stockée en epochDay, ex. "14/09/2026". */
fun formatDate(epochDay: Long): String = LocalDate.ofEpochDay(epochDay).format(dateFormatter)