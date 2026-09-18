package mg.itu.hoaviko.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mg.itu.hoaviko.data.entity.Withdrawal

/** Demande de retrait avec le nom de l'utilisateur demandeur (vue administrateur). */
data class WithdrawalWithUser(
    val id: Long,
    val userId: Long,
    val userName: String,
    val amountAriary: Long,
    val requestDateEpochDay: Long,
    val withdrawalDateEpochDay: Long,
    val hasNotice: Boolean,
    val penaltyPercent: Int,
    val status: String,
    val note: String
)

@Dao
interface WithdrawalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(withdrawal: Withdrawal): Long

    @Query("SELECT * FROM withdrawals WHERE userId = :userId ORDER BY requestDateEpochDay DESC, id DESC")
    fun observeByUser(userId: Long): Flow<List<Withdrawal>>

    @Query("SELECT COUNT(*) FROM withdrawals")
    fun observeCount(): Flow<Int>

    @Query(
        "SELECT w.id, w.userId, (u.lastName || ' ' || u.firstName) AS userName, w.amountAriary, " +
            "w.requestDateEpochDay, w.withdrawalDateEpochDay, w.hasNotice, w.penaltyPercent, w.status, w.note " +
            "FROM withdrawals w INNER JOIN users u ON u.id = w.userId " +
            "ORDER BY w.requestDateEpochDay DESC, w.id DESC"
    )
    fun observeAllWithUser(): Flow<List<WithdrawalWithUser>>

    @Query("UPDATE withdrawals SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("DELETE FROM withdrawals WHERE userId = :userId")
    suspend fun deleteByUser(userId: Long)
}