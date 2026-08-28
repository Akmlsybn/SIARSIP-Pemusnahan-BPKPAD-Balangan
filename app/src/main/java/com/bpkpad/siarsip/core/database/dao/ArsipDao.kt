package com.bpkpad.siarsip.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bpkpad.siarsip.core.database.entity.ArsipEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArsipDao {

    @Query("SELECT * FROM archives ORDER BY id ASC LIMIT 200")
    fun getAllArchives(): Flow<List<ArsipEntity>>

    @Query("SELECT * FROM archives WHERE status = 'AVAILABLE' AND nasibAkhir = 'MUSNAH' ORDER BY id ASC LIMIT 200")
    fun getAvailableArchives(): Flow<List<ArsipEntity>>

    @Query("SELECT * FROM archives WHERE (kode LIKE '%' || :query || '%' OR deskripsi LIKE '%' || :query || '%') ORDER BY id ASC LIMIT 200")
    fun searchArchives(query: String): Flow<List<ArsipEntity>>

    @Query("""
        SELECT * FROM archives 
        WHERE (:sumber IS NULL OR sumber = :sumber)
          AND (:status IS NULL OR status = :status)
          AND (:tahun IS NULL OR tahun = :tahun)
          AND (:query IS NULL OR kode LIKE '%' || :query || '%' OR deskripsi LIKE '%' || :query || '%')
        ORDER BY id ASC
        LIMIT :limit OFFSET :offset
    """)
    fun getArchivesFiltered(
        sumber: String?,
        status: String?,
        tahun: String?,
        query: String?,
        limit: Int,
        offset: Int
    ): Flow<List<ArsipEntity>>

    @Query("""
        SELECT COUNT(*) FROM archives 
        WHERE (:sumber IS NULL OR sumber = :sumber)
          AND (:status IS NULL OR status = :status)
          AND (:tahun IS NULL OR tahun = :tahun)
          AND (:query IS NULL OR kode LIKE '%' || :query || '%' OR deskripsi LIKE '%' || :query || '%')
    """)
    fun countArchivesFiltered(
        sumber: String?,
        status: String?,
        tahun: String?,
        query: String?
    ): Flow<Int>

    @Query("SELECT * FROM archives WHERE proposalId = :proposalId ORDER BY id ASC")
    fun getArchivesByProposal(proposalId: String): Flow<List<ArsipEntity>>

    @Query("SELECT * FROM archives WHERE beritaAcaraId = :beritaAcaraId ORDER BY id ASC")
    fun getArchivesByBeritaAcara(beritaAcaraId: String): Flow<List<ArsipEntity>>

    @Query("UPDATE archives SET status = :status, proposalId = :proposalId WHERE id IN (:ids)")
    suspend fun updateArchivesProposal(ids: List<String>, status: String, proposalId: String?)

    @Query("UPDATE archives SET status = :status WHERE proposalId = :proposalId")
    suspend fun updateArchivesStatusByProposal(proposalId: String, status: String)

    @Query("UPDATE archives SET status = :status, beritaAcaraId = :beritaAcaraId, disposedAt = :disposedAt WHERE id IN (:ids)")
    suspend fun disposeArchives(ids: List<String>, status: String, beritaAcaraId: String, disposedAt: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArchives(archives: List<ArsipEntity>)

    @Query("SELECT COUNT(*) FROM archives")
    suspend fun countArchives(): Int
}
