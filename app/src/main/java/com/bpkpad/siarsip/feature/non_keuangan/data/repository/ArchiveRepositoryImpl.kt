package com.bpkpad.arsip.data.repository

import com.bpkpad.arsip.core.data.local.dao.ArchiveDocumentDao
import com.bpkpad.arsip.core.data.local.entity.ArchiveDocumentEntity
import com.bpkpad.arsip.core.data.local.entity.toDomain
import com.bpkpad.arsip.domain.model.ArchiveDocument
import com.bpkpad.arsip.domain.repository.ArchiveRepository
import com.bpkpad.arsip.utils.ResultState
import com.bpkpad.siarsip.core.database.dao.ArsipDao
import com.bpkpad.siarsip.core.network.SupabaseSyncManager
import com.bpkpad.siarsip.feature.non_keuangan.data.mapper.toNonKeuanganDomain
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveRepositoryImpl @Inject constructor(
    private val archiveDao: ArchiveDocumentDao,
    private val arsipDao: ArsipDao,
    private val syncManager: SupabaseSyncManager
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
        val res = try {
            val remoteResult = syncManager.fetchArchivesRemotePaginated(
                sumber = "Non-Keuangan",
                status = null,
                tahun = null,
                query = null,
                limit = 1000,
                offset = 0
            )

            val mapped = remoteResult.items.map { it.toNonKeuanganDomain() }
            if (mapped.isNotEmpty()) {
                ResultState.Success(mapped)
            } else {
                val roomItems = arsipDao.getArchivesFiltered("Non-Keuangan", null, null, null, 1000, 0).first()
                if (roomItems.isNotEmpty()) {
                    ResultState.Success(roomItems.map { it.toNonKeuanganDomain() })
                } else {
                    val allRoomItems = arsipDao.getArchivesFiltered(null, null, null, null, 1000, 0).first()
                    val nonSp2dItems = allRoomItems.filter { !it.deskripsi.contains("SP2D", ignoreCase = true) }
                    if (nonSp2dItems.isNotEmpty()) {
                        ResultState.Success(nonSp2dItems.map { it.toNonKeuanganDomain() })
                    } else {
                        ResultState.Success(sampleArchives)
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ResultState.Success(sampleArchives)
        }
        emit(res)
    }

    override fun getArchiveById(id: String): Flow<ResultState<ArchiveDocument>> = flow {
        emit(ResultState.Loading)
        val res = try {
            val roomItems = arsipDao.getArchivesFiltered(null, null, null, null, 2000, 0).first()
            val entity = roomItems.find { it.id == id }
            if (entity != null) {
                ResultState.Success(entity.toNonKeuanganDomain())
            } else {
                val localEntities = archiveDao.getAllDocuments().first()
                val docFromDb = localEntities.find { it.id == id }?.toDomain()
                val doc = docFromDb ?: sampleArchives.find { it.id == id } ?: sampleArchives.first()
                ResultState.Success(doc)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val doc = sampleArchives.find { it.id == id } ?: sampleArchives.first()
            ResultState.Success(doc)
        }
        emit(res)
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
