package com.bpkpad.peminjaman.peminjaman.data.repository

import com.bpkpad.peminjaman.core.common.ResultState
import com.bpkpad.peminjaman.core.database.dao.MasterDokumenDao
import com.bpkpad.peminjaman.core.database.entity.MasterDokumenEntity
import com.bpkpad.peminjaman.peminjaman.data.mapper.toPeminjamanDomain
import com.bpkpad.peminjaman.peminjaman.domain.model.MasterDokumen
import com.bpkpad.peminjaman.peminjaman.domain.model.enums.DokumenStatus
import com.bpkpad.peminjaman.peminjaman.domain.repository.MasterDokumenRepository
import com.bpkpad.siarsip.core.database.dao.ArsipDao
import com.bpkpad.siarsip.core.network.SupabaseSyncManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MasterDokumenRepositoryImpl @Inject constructor(
    private val dao: MasterDokumenDao,
    private val arsipDao: ArsipDao,
    private val syncManager: SupabaseSyncManager
) : MasterDokumenRepository {

    override fun getAll(): Flow<List<MasterDokumen>> = flow {
        val items = try {
            val remoteResult = syncManager.fetchArchivesRemotePaginated(
                sumber = null,
                status = null,
                tahun = null,
                query = null,
                limit = 1000,
                offset = 0
            )

            if (remoteResult.items.isNotEmpty()) {
                remoteResult.items.map { it.toPeminjamanDomain() }
            } else {
                val roomItems = arsipDao.getArchivesFiltered(null, null, null, null, 1000, 0).first()
                if (roomItems.isNotEmpty()) {
                    roomItems.map { it.toPeminjamanDomain() }
                } else {
                    dao.getAll().first().map { it.toDomain() }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            try {
                dao.getAll().first().map { it.toDomain() }
            } catch (ce: CancellationException) {
                throw ce
            } catch (ex: Exception) {
                emptyList()
            }
        }
        emit(items)
    }

    override fun getAvailable(): Flow<List<MasterDokumen>> = flow {
        val items = try {
            val remoteResult = syncManager.fetchArchivesRemotePaginated(
                sumber = null,
                status = "AVAILABLE",
                tahun = null,
                query = null,
                limit = 1000,
                offset = 0
            )

            if (remoteResult.items.isNotEmpty()) {
                remoteResult.items.map { it.toPeminjamanDomain() }
            } else {
                val roomItems = arsipDao.getArchivesFiltered(null, "AVAILABLE", null, null, 1000, 0).first()
                if (roomItems.isNotEmpty()) {
                    roomItems.map { it.toPeminjamanDomain() }
                } else {
                    dao.getAvailable().first().map { it.toDomain() }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            try {
                dao.getAvailable().first().map { it.toDomain() }
            } catch (ce: CancellationException) {
                throw ce
            } catch (ex: Exception) {
                emptyList()
            }
        }
        emit(items)
    }

    override suspend fun getById(id: Int): MasterDokumen? {
        return try {
            val roomItems = arsipDao.getArchivesFiltered(null, null, null, null, 2000, 0).first()
            val foundEntity = roomItems.find { kotlin.math.abs(it.id.hashCode()) == id }
            foundEntity?.toPeminjamanDomain() ?: dao.getById(id)?.toDomain()
        } catch (e: Exception) {
            dao.getById(id)?.toDomain()
        }
    }

    override suspend fun create(dokumen: MasterDokumen): ResultState<MasterDokumen> {
        return try {
            val entity = dokumen.toEntity()
            val rowId = dao.insert(entity)
            ResultState.Success(dokumen.copy(id = rowId.toInt()))
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Gagal menyimpan dokumen", e)
        }
    }

    override suspend fun update(dokumen: MasterDokumen): ResultState<MasterDokumen> {
        return try {
            dao.update(dokumen.toEntity())
            ResultState.Success(dokumen)
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Gagal memperbarui dokumen", e)
        }
    }

    override suspend fun updateStatus(id: Int, status: DokumenStatus): ResultState<Unit> {
        return try {
            dao.updateStatus(id, status.name.lowercase())
            ResultState.Success(Unit)
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Gagal mengupdate status", e)
        }
    }

    override fun search(query: String): Flow<List<MasterDokumen>> = flow {
        val items = try {
            val remoteResult = syncManager.fetchArchivesRemotePaginated(
                sumber = null,
                status = null,
                tahun = null,
                query = query,
                limit = 1000,
                offset = 0
            )

            if (remoteResult.items.isNotEmpty()) {
                remoteResult.items.map { it.toPeminjamanDomain() }
            } else {
                dao.search(query).first().map { it.toDomain() }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            try {
                dao.search(query).first().map { it.toDomain() }
            } catch (ce: CancellationException) {
                throw ce
            } catch (ex: Exception) {
                emptyList()
            }
        }
        emit(items)
    }

    private fun MasterDokumenEntity.toDomain() = MasterDokumen(
        id = id,
        nomorDokumen = nomorDokumen,
        perihal = perihal,
        nominal = nominal,
        tahun = tahun,
        jenisDokumen = jenisDokumen,
        lokasiRak = lokasiRak ?: "RAK-01",
        lokasiBox = lokasiBox ?: "BOX-01",
        status = when (status) {
            "tersedia" -> DokumenStatus.TERSEDIA
            "dipinjam" -> DokumenStatus.DIPINJAM
            else -> DokumenStatus.TERSEDIA
        }
    )

    private fun MasterDokumen.toEntity() = MasterDokumenEntity(
        id = id,
        nomorDokumen = nomorDokumen,
        perihal = perihal,
        nominal = nominal,
        tahun = tahun,
        jenisDokumen = jenisDokumen,
        status = status.name.lowercase(),
        lokasiRak = lokasiRak,
        lokasiBox = lokasiBox
    )
}
