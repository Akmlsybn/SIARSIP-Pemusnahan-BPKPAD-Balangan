package com.bpkpad.siarsip.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.bpkpad.siarsip.core.database.entity.BeritaAcaraEntity
import com.bpkpad.siarsip.core.database.entity.BeritaAcaraWithRelations
import kotlinx.coroutines.flow.Flow

@Dao
interface BeritaAcaraDao {

    @Query("SELECT * FROM berita_acara ORDER BY createdAt DESC")
    fun getAllBeritaAcara(): Flow<List<BeritaAcaraEntity>>

    @Query("SELECT * FROM berita_acara WHERE id = :id")
    fun getBeritaAcaraById(id: String): Flow<BeritaAcaraEntity?>

    @Transaction
    @Query("SELECT * FROM berita_acara ORDER BY createdAt DESC")
    fun getAllBeritaAcaraWithRelations(): Flow<List<BeritaAcaraWithRelations>>

    @Transaction
    @Query("SELECT * FROM berita_acara WHERE id = :id")
    fun getBeritaAcaraWithRelationsById(id: String): Flow<BeritaAcaraWithRelations?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeritaAcara(beritaAcara: BeritaAcaraEntity)
}

