package mg.itu.hoaviko.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import mg.itu.hoaviko.data.entity.UserAccount

class FirebaseProfileService(
    private val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()
) {

    suspend fun saveProfile(user: UserAccount): Boolean {
        val uid = user.firebaseUid ?: return false

        val profile = mapOf(
            "firebaseUid" to uid,
            "lastName" to user.lastName,
            "firstName" to user.firstName,
            "birthDateEpochDay" to user.birthDateEpochDay,
            "cinNumber" to user.cinNumber,
            "profession" to user.profession,
            "email" to user.email,
            "username" to user.username,
            "registrationRank" to user.registrationRank,
            "language" to user.language,
            "paymentChannelId" to user.paymentChannelId,
            "savingsChannelId" to user.savingsChannelId,
            "objectiveAmount" to user.objectiveAmount,
            "monthlyContribution" to user.monthlyContribution,
            "createdAt" to user.createdAt,
            "updatedAt" to System.currentTimeMillis(),
            "role" to user.role
        )

        return try {
            firestore
                .collection("users")
                .document(uid)
                .set(profile, SetOptions.merge())
                .await()

            true
        } catch (_: Exception) {
            false
        }
    }
}

