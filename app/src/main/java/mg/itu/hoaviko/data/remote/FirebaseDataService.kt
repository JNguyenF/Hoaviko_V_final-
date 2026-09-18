package mg.itu.hoaviko.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import mg.itu.hoaviko.data.entity.Contribution
import mg.itu.hoaviko.data.entity.Withdrawal

class FirebaseDataService(
    private val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()
) {

    /**
     * Ajoute ou met à jour une cotisation dans Firestore.
     *
     * L'identifiant local est combiné avec l'UID Firebase pour éviter
     * de créer deux fois la même cotisation.
     */
    fun queueContribution(
        firebaseUid: String?,
        contribution: Contribution
    ) {
        if (firebaseUid.isNullOrBlank() || contribution.id <= 0L) {
            return
        }

        val data = mapOf(
            "localId" to contribution.id,
            "userFirebaseUid" to firebaseUid,
            "amountAriary" to contribution.amountAriary,
            "dateEpochDay" to contribution.dateEpochDay,
            "paymentChannelId" to contribution.paymentChannelId,
            "accountReference" to contribution.accountReference,
            "note" to contribution.note,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore
            .collection("users")
            .document(firebaseUid)
            .collection("contributions")
            .document(contributionDocumentId(firebaseUid, contribution.id))
            .set(data, SetOptions.merge())
    }

    /**
     * Ajoute ou met à jour une demande de retrait dans Firestore.
     */
    fun queueWithdrawal(
        firebaseUid: String?,
        withdrawal: Withdrawal
    ) {
        if (firebaseUid.isNullOrBlank() || withdrawal.id <= 0L) {
            return
        }

        val data = mapOf(
            "localId" to withdrawal.id,
            "userFirebaseUid" to firebaseUid,
            "amountAriary" to withdrawal.amountAriary,
            "requestDateEpochDay" to withdrawal.requestDateEpochDay,
            "withdrawalDateEpochDay" to withdrawal.withdrawalDateEpochDay,
            "hasNotice" to withdrawal.hasNotice,
            "penaltyPercent" to withdrawal.penaltyPercent,
            "status" to withdrawal.status,
            "note" to withdrawal.note,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore
            .collection("users")
            .document(firebaseUid)
            .collection("withdrawals")
            .document(withdrawalDocumentId(firebaseUid, withdrawal.id))
            .set(data, SetOptions.merge())
    }

    /**
     * Met à jour le statut d'une demande après validation ou refus.
     */
    fun queueWithdrawalStatus(
        firebaseUid: String?,
        localWithdrawalId: Long,
        status: String
    ) {
        if (firebaseUid.isNullOrBlank() || localWithdrawalId <= 0L) {
            return
        }

        val update = mapOf(
            "status" to status,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore
            .collection("users")
            .document(firebaseUid)
            .collection("withdrawals")
            .document(
                withdrawalDocumentId(
                    firebaseUid,
                    localWithdrawalId
                )
            )
            .set(update, SetOptions.merge())
    }

    private fun contributionDocumentId(
        firebaseUid: String,
        localId: Long
    ): String {
        return "${firebaseUid}_contribution_$localId"
    }

    private fun withdrawalDocumentId(
        firebaseUid: String,
        localId: Long
    ): String {
        return "${firebaseUid}_withdrawal_$localId"
    }
}