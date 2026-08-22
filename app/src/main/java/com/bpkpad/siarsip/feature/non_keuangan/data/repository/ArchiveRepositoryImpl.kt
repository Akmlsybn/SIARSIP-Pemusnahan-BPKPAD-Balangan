package com.bpkpad.arsip.data.repository

import com.bpkpad.arsip.core.data.local.dao.ArchiveDocumentDao
import com.bpkpad.arsip.core.data.local.entity.ArchiveDocumentEntity
import com.bpkpad.arsip.core.data.local.entity.toDomain
import com.bpkpad.arsip.domain.model.ArchiveDocument
import com.bpkpad.arsip.domain.repository.ArchiveRepository
import com.bpkpad.arsip.utils.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveRepositoryImpl @Inject constructor(
    private val archiveDao: ArchiveDocumentDao
) : ArchiveRepository {

    private val sampleArchives = listOf(
        ArchiveDocument(
            id = "non-k-001",
            title = "Surat Perintah Tugas Audit Kinerja BPKPAD 2024",
            type = "SURAT",
            date = System.currentTimeMillis() - 86400000L * 10,
            description = "Surat Tugas pengawasan dan evaluasi kinerja pengelolaan barang milik daerah Kabupaten Balangan.",
            boxId = "BOX-NK-01",
            locationId = "RAK-A-01"
        ),
        ArchiveDocument(
            id = "non-k-002",
            title = "PERDA No 5/2022 tentang Pengelolaan Barang Milik Daerah",
            type = "PERDA",
            date = System.currentTimeMillis() - 86400000L * 300,
            description = "Pedoman resmi pelaksanaan tata kelola dan inventarisasi aset daerah Kabupaten Balangan.",
            boxId = "BOX-NK-02",
            locationId = "RAK-A-02"
        ),
        ArchiveDocument(
            id = "non-k-003",
            title = "PERBUP Balangan No 12/2024 tentang Naskah Dinas Elektronik",
            type = "PERBUP",
            date = System.currentTimeMillis() - 86400000L * 45,
            description = "Standardisasi format dan penandatanganan dokumen naskah dinas di lingkungan Pemkab Balangan.",
            boxId = "BOX-NK-03",
            locationId = "RAK-B-01"
        ),
        ArchiveDocument(
            id = "non-k-004",
            title = "SK Bupati Balangan tentang Tim Penilai Retensi Arsip",
            type = "KEPUTUSAN_BUPATI",
            date = System.currentTimeMillis() - 86400000L * 90,
            description = "Penetapan keanggotaan panitia penilai arsip untuk proses jadwal retensi arsip Pemkab Balangan.",
            boxId = "BOX-NK-04",
            locationId = "RAK-B-02"
        )
    )

    override fun getArchives(): Flow<ResultState<List<ArchiveDocument>>> = flow {
        emit(ResultState.Loading)
        archiveDao.getAllDocuments().collect { entities ->
            if (entities.isEmpty()) {
                emit(ResultState.Success(sampleArchives))
            } else {
                emit(ResultState.Success(entities.map { it.toDomain() }))
            }
        }
    }

    override fun getArchiveById(id: String): Flow<ResultState<ArchiveDocument>> = flow {
        emit(ResultState.Loading)
        archiveDao.getAllDocuments().collect { entities ->
            val docFromDb = entities.find { it.id == id }?.toDomain()
            val doc = docFromDb ?: sampleArchives.find { it.id == id } ?: sampleArchives.first()
            emit(ResultState.Success(doc))
        }
    }

    override fun saveArchive(document: ArchiveDocument): Flow<ResultState<Unit>> = flow {
        emit(ResultState.Loading)
        try {
            val entity = ArchiveDocumentEntity(
                id = if (document.id.isEmpty()) UUID.randomUUID().toString() else document.id,
                type = document.type,
                title = document.title,
                year = 2024,
                condition = "BAIK",
                instance = "BPKPAD",
                metadata = "{\"description\":\"${document.description}\",\"box_id\":\"${document.boxId}\",\"location_id\":\"${document.locationId}\"}",
                coverUrl = document.imageUrl,
                timestampUserId = "admin"
            )
            archiveDao.insert(entity)
            emit(ResultState.Success(Unit))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun parseArchiveFromText(rawText: String): Flow<ResultState<ArchiveDocument>> = flow {
        emit(ResultState.Loading)
        try {
            val parsedDoc = ArchiveDocument(
                id = UUID.randomUUID().toString(),
                title = "Surat Hasil OCR",
                description = rawText,
                imageUrl = "",
                type = "SURAT",
                date = System.currentTimeMillis(),
                boxId = "BOX-NK-01",
                locationId = "RAK-A-01"
            )
            emit(ResultState.Success(parsedDoc))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unknown Error", e))
        }
    }
}
