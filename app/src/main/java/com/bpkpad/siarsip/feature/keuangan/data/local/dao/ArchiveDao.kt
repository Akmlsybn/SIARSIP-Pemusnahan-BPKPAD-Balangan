package com.example.arsipbpkpad.data.local.dao

import androidx.room.*
import com.example.arsipbpkpad.data.local.entity.ArchiveEntity
import kotlinx.coroutines.flow.Flow

data class YearStatEntity(
    val year: Int,
    val count: Int,
    val lastUpdated: String?
)

data class ClassificationBudget(
    val classificationCode: String,
    val total: Double
)

@Dao
interface ArchiveDao {

    @Query("""
        SELECT * FROM keuangan_archives 
        WHERE (:isYearEmpty OR year IN (:years))
        AND (:query IS NULL OR :query = '' 
            OR documentNumber LIKE '%' || :query || '%'
            OR description LIKE '%' || :query || '%'
        )
        AND deletedAt IS NULL
        ORDER BY createdAt DESC
    """)
    fun getArchivesList(query: String?, years: List<Int>, isYearEmpty: Boolean): Flow<List<ArchiveEntity>>

    @Query("SELECT SUM(nominal) FROM keuangan_archives WHERE year = :year AND deletedAt IS NULL")
    fun getTotalBudgetByYear(year: Int): Flow<Double?>

    @Query("SELECT SUM(nominal) FROM keuangan_archives WHERE (year BETWEEN :startYear AND :endYear) AND deletedAt IS NULL")
    fun getTotalBudgetForRange(startYear: Int, endYear: Int): Flow<Double?>

    @Query("SELECT classificationCode, SUM(nominal) as total FROM keuangan_archives WHERE year = :year AND deletedAt IS NULL GROUP BY classificationCode")
    fun getBudgetByClassification(year: Int): Flow<List<ClassificationBudget>>

    @Query("SELECT classificationCode, SUM(nominal) as total FROM keuangan_archives WHERE (year BETWEEN :startYear AND :endYear) AND deletedAt IS NULL GROUP BY classificationCode")
    fun getBudgetByClassificationForRange(startYear: Int, endYear: Int): Flow<List<ClassificationBudget>>

    @Query("SELECT * FROM keuangan_archives WHERE id = :id AND deletedAt IS NULL")
    fun getArchiveById(id: String): Flow<ArchiveEntity?>

    @Query("SELECT * FROM keuangan_archives WHERE id = :id AND deletedAt IS NULL")
    suspend fun getArchiveByIdSync(id: String): ArchiveEntity?

    @Query("SELECT * FROM keuangan_archives WHERE bundleId = :bundleId AND deletedAt IS NULL")
    fun getArchivesByBundleId(bundleId: String): Flow<List<ArchiveEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM keuangan_archives WHERE documentNumber = :docNumber AND copyType = :copyType AND deletedAt IS NULL)")
    suspend fun existsByDocumentNumberAndType(docNumber: String, copyType: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM keuangan_archives WHERE documentNumber = :docNumber AND deletedAt IS NULL)")
    suspend fun existsByDocumentNumber(docNumber: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArchive(archive: ArchiveEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArchives(archives: List<ArchiveEntity>)

    @Query("DELETE FROM keuangan_archives WHERE id = :id")
    suspend fun hardDeleteArchiveById(id: String)

    @Query("UPDATE keuangan_archives SET deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDeleteArchiveById(id: String, deletedAt: String)

    @Query("UPDATE keuangan_archives SET deletedAt = :deletedAt WHERE bundleId = :bundleId AND deletedAt IS NULL")
    suspend fun softDeleteArchivesByBundleId(bundleId: String, deletedAt: String)

    @Query("SELECT COUNT(*) FROM keuangan_archives WHERE bundleId = :bundleId AND deletedAt IS NULL")
    suspend fun countActiveArchivesByBundleId(bundleId: String): Int

    @Query("SELECT * FROM keuangan_archives WHERE syncStatus = 'DRAFT' AND deletedAt IS NULL")
    suspend fun getPendingArchives(): List<ArchiveEntity>

    @Query("SELECT DISTINCT year FROM keuangan_archives WHERE deletedAt IS NULL ORDER BY year DESC")
    fun getArchivedYears(): Flow<List<Int>>

    @Query("""
        SELECT year, COUNT(*) as count, MAX(updatedAt) as lastUpdated 
        FROM keuangan_archives 
        WHERE deletedAt IS NULL
        GROUP BY year 
        ORDER BY year DESC
    """)
    fun getYearStats(): Flow<List<YearStatEntity>>

    @Query("DELETE FROM keuangan_archives")
    suspend fun clearArchives()
}
