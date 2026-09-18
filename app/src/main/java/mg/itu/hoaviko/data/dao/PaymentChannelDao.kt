package mg.itu.hoaviko.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mg.itu.hoaviko.data.entity.PaymentChannel

@Dao
interface PaymentChannelDao {

    @Query("SELECT * FROM payment_channels ORDER BY category, name")
    fun getAll(): Flow<List<PaymentChannel>>

    @Query("SELECT * FROM payment_channels ORDER BY category, name")
    suspend fun getAllSuspend(): List<PaymentChannel>

    @Query("SELECT * FROM payment_channels WHERE id = :id")
    suspend fun getById(id: Long): PaymentChannel?

    @Query("SELECT * FROM payment_channels WHERE category = 'Banque' ORDER BY name")
    fun getBanks(): Flow<List<PaymentChannel>>

    @Query("SELECT COUNT(*) FROM payment_channels")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(channels: List<PaymentChannel>)
}