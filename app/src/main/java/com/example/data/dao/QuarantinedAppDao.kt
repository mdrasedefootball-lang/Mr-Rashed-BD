package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.QuarantinedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuarantinedAppDao {
    @Query("SELECT * FROM quarantined_apps ORDER BY detectedTime DESC")
    fun getAllQuarantined(): Flow<List<QuarantinedAppEntity>>

    @Query("SELECT * FROM quarantined_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getByPackageName(packageName: String): QuarantinedAppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(app: QuarantinedAppEntity)

    @Update
    suspend fun update(app: QuarantinedAppEntity)

    @Delete
    suspend fun delete(app: QuarantinedAppEntity)

    @Query("DELETE FROM quarantined_apps WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)
}
