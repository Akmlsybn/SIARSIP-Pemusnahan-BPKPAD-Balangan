package com.bpkpad.arsip.core.data.repository

import com.bpkpad.arsip.core.data.local.dao.ArchiveDocumentDao
import com.bpkpad.arsip.core.data.local.dao.TempDocumentDao
import com.bpkpad.arsip.core.data.local.entity.ArchiveDocumentEntity
import com.bpkpad.arsip.core.data.local.entity.TempDocumentEntity
import com.bpkpad.arsip.core.domain.model.DocumentType
import com.bpkpad.arsip.core.domain.model.StagingDocument
import com.bpkpad.arsip.core.domain.model.StagingStatus
import com.bpkpad.arsip.core.domain.repository.StagingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StagingRepositoryImpl @Inject constructor(
    private val tempDocumentDao: TempDocumentDao,
    private val archiveDao: ArchiveDocumentDao
) : StagingRepository {

    private val sampleStaging = listOf(
        StagingDocument(
            id = "stg-nk-01",
            type = DocumentType.SURAT,
            title = "Draf Surat Undangan Rapat Koordinasi Pengelolaan Aset",
            year = 2024,
            metadata = mapOf("letter_number" to "005/SK/BPKPAD/2024", "sender" to "Sekretariat BPKPAD"),
            coverLocalPath = null,
            status = StagingStatus.LOCAL_ONLY
        ),
        StagingDocument(
            id = "stg-nk-02",
            type = DocumentType.PERBUP,
            title = "Draf PERBUP Pedoman Digitalisasi Tata Naskah Dinas",
            year = 2024,
            metadata = mapOf("regulation_number" to "14/2024", "subject" to "Digitalisasi Arsip Balangan"),
            coverLocalPath = null,
            status = StagingStatus.LOCAL_ONLY
        )
    )

    override fun getAllStaging(): Flow<List<StagingDocument>> {
        return tempDocumentDao.getAllStaging().map { entities ->
            if (entities.isEmpty()) {
                sampleStaging
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    override suspend fun saveToStaging(doc: StagingDocument): Result<Unit> {
        return try {
            tempDocumentDao.insert(doc.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStaging(doc: StagingDocument): Result<Unit> {
        return try {
            tempDocumentDao.update(doc.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFromStaging(id: String) {
        tempDocumentDao.deleteById(id)
    }

    override suspend fun pushAllToCloud(locationId: String, userId: String): Result<Unit> {
        return try {
            val stagingDocs = tempDocumentDao.getAllStaging().first()
            val listToPush = if (stagingDocs.isEmpty()) sampleStaging else stagingDocs.map { it.toDomain() }
            
            listToPush.forEach { doc ->
                val archiveEntity = ArchiveDocumentEntity(
                    id = doc.id,
                    type = doc.type.name,
                    title = doc.title,
                    year = doc.year,
                    condition = "BAIK",
                    instance = "BPKPAD",
                    metadata = JSONObject(doc.metadata as Map<*, *>).toString(),
                    coverUrl = doc.coverLocalPath,
                    timestampUserId = userId
                )
                archiveDao.insert(archiveEntity)
                tempDocumentDao.deleteById(doc.id)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun TempDocumentEntity.toDomain(): StagingDocument {
        val map = mutableMapOf<String, String>()
        try {
            val json = JSONObject(metadata)
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = json.optString(key)
            }
        } catch (_: Exception) {}

        return StagingDocument(
            id = id,
            type = try { DocumentType.valueOf(type) } catch (_: Exception) { DocumentType.SURAT },
            title = title,
            year = year,
            metadata = map,
            coverLocalPath = coverLocalPath,
            status = try { StagingStatus.valueOf(status) } catch (_: Exception) { StagingStatus.LOCAL_ONLY }
        )
    }

    private fun StagingDocument.toEntity(): TempDocumentEntity {
        val json = JSONObject(metadata as Map<*, *>).toString()
        return TempDocumentEntity(
            id = id,
            type = type.name,
            title = title,
            year = year,
            condition = "BAIK",
            instance = "BPKPAD",
            metadata = json,
            coverLocalPath = coverLocalPath,
            status = status.name
        )
    }
}