package mg.itu.hoaviko.data

import mg.itu.hoaviko.data.entity.PaymentChannel

private const val MOBILE_MONEY = "MobileMoney"
private const val BANQUE = "Banque"

/** Canaux de paiement pré-remplis (MVola, Orange, Airtel + banques listées). */
object SeedData {
    val channels: List<PaymentChannel> = listOf(
        PaymentChannel(name = "MVola", category = MOBILE_MONEY, annualRate = 0.0, minAmount = 500, maxAmount = 5_000_000),
        PaymentChannel(name = "Orange Money", category = MOBILE_MONEY, annualRate = 0.0, minAmount = 500, maxAmount = 5_000_000),
        PaymentChannel(name = "Airtel Money", category = MOBILE_MONEY, annualRate = 0.0, minAmount = 500, maxAmount = 5_000_000),
        PaymentChannel(name = "BRED Madagascar", category = BANQUE, annualRate = 0.050, minAmount = 5_000, maxAmount = 5_000_000),
        PaymentChannel(name = "BNI Madagascar", category = BANQUE, annualRate = 0.060, minAmount = 10_000, maxAmount = 5_000_000),
        PaymentChannel(name = "BMOI", category = BANQUE, annualRate = 0.055, minAmount = 10_000, maxAmount = 5_000_000),
        PaymentChannel(name = "SIPEM", category = BANQUE, annualRate = 0.090, minAmount = 10_000, maxAmount = 5_000_000),
        PaymentChannel(name = "BOA Madagascar", category = BANQUE, annualRate = 0.055, minAmount = 10_000, maxAmount = 5_000_000)
    )
}