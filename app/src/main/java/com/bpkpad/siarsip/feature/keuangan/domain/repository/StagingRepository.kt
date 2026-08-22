package com.example.arsipbpkpad.domain.repository

import com.example.arsipbpkpad.domain.model.ArchiveDocument
import com.example.arsipbpkpad.domain.model.StagedBox
import kotlinx.coroutines.flow.Flow

/**
 * Interface for staging operations.
 * Manages temporary storage of boxes and documents before they are finalized.
 */
interface StagingRepository {
    // Box Management
    fun getAllStagedBoxes(): Flow<List<StagedBox>>
    suspend fun saveStagedBox(box: StagedBox)
    suspend fun deleteStagedBox(sessionId: String)
    suspend fun getStagedBoxById(sessionId: String): StagedBox?
    suspend fun checkStagedBoxExists(
        warehouse: String,
        rack: String,
        year: String,
        box: String,
        excludeSessionId: String? = null
    ): Boolean

    // Archive Management
    fun getAllStagingArchives(): Flow<List<ArchiveDocument>>
    fun getStagingArchivesBySession(sessionId: String): Flow<List<ArchiveDocument>>
    suspend fun insertToStaging(archive: ArchiveDocument)
    suspend fun deleteFromStaging(id: String)
    suspend fun clearStagingBySession(sessionId: String)
    suspend fun clearAllStaging()
}
