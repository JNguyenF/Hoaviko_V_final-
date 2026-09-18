package mg.itu.hoaviko.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mg.itu.hoaviko.data.entity.Contribution

@Dao
interface ContributionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contribution: Contribution): Long

    @Query("SELECT * FROM contributions WHERE userId = :userId ORDER BY dateEpochDay DESC, id DESC")
    fun observeByUser(userId: Long): Flow<List<Contribution>>

    @Query("SELECT COALESCE(SUM(amountAriary), 0) FROM contributions WHERE userId = :userId")
    fun observeTotal(userId: Long): Flow<Long>

    @Query("SELECT COUNT(*) FROM contributions WHERE userId = :userId")
    fun observeCount(userId: Long): Flow<Int>

    @Query("DELETE FROM contributions WHERE userId = :userId")
    suspend fun deleteByUser(userId: Long)
}