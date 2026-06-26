package com.bpkpad.siarsip.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bpkpad.siarsip.core.database.entity.PenandatanganEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PenandatanganDao {

    @Query("SELECT * FROM penandatangan WHERE beritaAcaraId = :beritaAcaraId ORDER BY urutan ASC")
    fun getPenandatanganByBeritaAcara(beritaAcaraId: String): Flow<List<PenandatanganEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPenandatangan(penandatangan: List<PenandatanganEntity>)
}
