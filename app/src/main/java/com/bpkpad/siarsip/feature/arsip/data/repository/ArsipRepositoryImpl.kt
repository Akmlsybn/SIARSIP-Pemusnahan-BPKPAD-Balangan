package com.bpkpad.siarsip.feature.arsip.data.repository

import com.bpkpad.siarsip.core.database.AppDatabase
import androidx.room.withTransaction
import com.bpkpad.siarsip.core.database.dao.*
import com.bpkpad.siarsip.core.network.SupabaseSyncManager
import com.bpkpad.siarsip.feature.arsip.data.mapper.*
import com.bpkpad.siarsip.feature.arsip.domain.model.*
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ArsipRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val arsipDao: ArsipDao,
    private val berkasUsulMusnahDao: BerkasUsulMusnahDao,
    private val beritaAcaraDao: BeritaAcaraDao,
    private val penandatanganDao: PenandatanganDao,
    private val auditLogDao: AuditLogDao,
    private val supabaseSyncManager: SupabaseSyncManager
) : ArsipRepository {

    override fun getAllArchives(): Flow<List<Arsip>> {
        return arsipDao.getAllArchives()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getAvailableArchives(): Flow<List<Arsip>> {
        return arsipDao.getAvailableArchives()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getArchivesFiltered(
        sumber: String?,
        status: String?,
        tahun: String?,
        query: String?,
        limit: Int,
        offset: Int
    ): Flow<List<Arsip>> {
        return flow {
            val result = supabaseSyncManager.fetchArchivesRemotePaginated(
                sumber = sumber,
                status = status,
                tahun = tahun,
                query = query,
                limit = limit,
                offset = offset
            )
            emit(result.items.map { it.toDomain() })
        }.flowOn(Dispatchers.IO)
    }

    override fun countArchivesFiltered(
        sumber: String?,
        status: String?,
        tahun: String?,
        query: String?
    ): Flow<Int> {
        return flow {
            val result = supabaseSyncManager.fetchArchivesRemotePaginated(
                sumber = sumber,
                status = status,
                tahun = tahun,
                query = query,
                limit = 1,
                offset = 0
            )
            emit(result.totalCount)
        }.flowOn(Dispatchers.IO)
    }

    override fun getArchivesByProposal(proposalId: String): Flow<List<Arsip>> {
        return arsipDao.getArchivesByProposal(proposalId)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getAllProposals(): Flow<List<BerkasUsulMusnah>> {
        return berkasUsulMusnahDao.getAllProposals()
            .map { proposals ->
                proposals.map { proposal ->
                    val archives = arsipDao.getArchivesByProposal(proposal.id).first()
                    proposal.toDomain(archives.map { it.toDomain() })
                }
            }.flowOn(Dispatchers.IO)
    }

    override fun getProposalById(id: String): Flow<BerkasUsulMusnah?> {
        return berkasUsulMusnahDao.getProposalById(id)
            .flatMapLatest { proposalEntity ->
                if (proposalEntity != null) {
                    arsipDao.getArchivesByProposal(proposalEntity.id).map { archives ->
                        proposalEntity.toDomain(archives.map { it.toDomain() })
                    }
                } else {
                    flowOf(null)
                }
            }.flowOn(Dispatchers.IO)
    }

    override fun getProposalByNomor(nomor: String): Flow<BerkasUsulMusnah?> {
        return berkasUsulMusnahDao.getProposalByNomor(nomor)
            .flatMapLatest { proposalEntity ->
                if (proposalEntity != null) {
                    arsipDao.getArchivesByProposal(proposalEntity.id).map { archives ->
                        proposalEntity.toDomain(archives.map { it.toDomain() })
                    }
                } else {
                    flowOf(null)
                }
            }.flowOn(Dispatchers.IO)
    }

    override fun getAllBeritaAcara(): Flow<List<BeritaAcara>> {
        return beritaAcaraDao.getAllBeritaAcaraWithRelations()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getBeritaAcaraById(id: String): Flow<BeritaAcara?> {
        return beritaAcaraDao.getBeritaAcaraWithRelationsById(id)
            .map { entity -> entity?.toDomain() }
            .flowOn(Dispatchers.IO)
    }

    override fun getPenandatanganForBeritaAcara(beritaAcaraId: String): Flow<List<Penandatangan>> {
        return penandatanganDao.getPenandatanganByBeritaAcara(beritaAcaraId)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getAllAuditLogs(): Flow<List<AuditLog>> {
        return auditLogDao.getAllAuditLogs()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun insertProposal(
        proposal: BerkasUsulMusnah,
        archiveIds: List<String>,
        auditLogs: List<AuditLog>
    ) {
        val entity = proposal.toEntity()
        withContext(Dispatchers.IO) {
            appDatabase.withTransaction {
                berkasUsulMusnahDao.insertProposal(entity)
                arsipDao.updateArchivesProposal(archiveIds, "PROPOSED", proposal.id)
                for (log in auditLogs) {
                    auditLogDao.insertAuditLog(log.toEntity())
                }
            }
            supabaseSyncManager.pushProposalToCloud(entity)
        }
    }

    override suspend fun updateProposalStatus(
        proposalId: String,
        status: String,
        auditLogs: List<AuditLog>,
        suratPertimbanganNomor: String?,
        suratPertimbanganPerihal: String?,
        jenisPersetujuanAkhir: String?,
        nomorPersetujuanAkhir: String?,
        perihalPersetujuanAkhir: String?
    ) {
        withContext(Dispatchers.IO) {
            appDatabase.withTransaction {
                berkasUsulMusnahDao.updateProposalStatus(proposalId, status)
                if (status == "VERIFIED") {
                    berkasUsulMusnahDao.updateVerificationMetadata(
                        proposalId,
                        suratPertimbanganNomor,
                        suratPertimbanganPerihal
                    )
                } else if (status == "APPROVED") {
                    berkasUsulMusnahDao.updateApprovalMetadata(
                        proposalId,
                        jenisPersetujuanAkhir,
                        nomorPersetujuanAkhir,
                        perihalPersetujuanAkhir
                    )
                }
                arsipDao.updateArchivesStatusByProposal(proposalId, status)
                for (log in auditLogs) {
                    auditLogDao.insertAuditLog(log.toEntity())
                }
            }
        }
    }

    override suspend fun insertBeritaAcara(
        beritaAcara: BeritaAcara,
        archiveIds: List<String>,
        signatories: List<Penandatangan>
    ) {
        val baEntity = beritaAcara.toEntity()
        withContext(Dispatchers.IO) {
            beritaAcaraDao.insertBeritaAcara(baEntity)
            penandatanganDao.insertPenandatangan(signatories.map { it.toEntity() })
            val disposedAtStr = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date())
            arsipDao.disposeArchives(archiveIds, "DISPOSED", beritaAcara.id, disposedAtStr)
            supabaseSyncManager.pushBeritaAcaraToCloud(baEntity)
        }
    }

    override suspend fun insertAuditLog(log: AuditLog) {
        withContext(Dispatchers.IO) {
            auditLogDao.insertAuditLog(log.toEntity())
        }
    }

    override suspend fun deleteProposal(proposalId: String, auditLogs: List<AuditLog>) {
        withContext(Dispatchers.IO) {
            appDatabase.withTransaction {
                val archives = arsipDao.getArchivesByProposal(proposalId).first()
                val archiveIds = archives.map { it.id }
                if (archiveIds.isNotEmpty()) {
                    arsipDao.updateArchivesProposal(archiveIds, "AVAILABLE", null)
                }
                berkasUsulMusnahDao.deleteProposal(proposalId)
                for (log in auditLogs) {
                    auditLogDao.insertAuditLog(log.toEntity())
                }
            }
        }
    }

    override suspend fun removeArchiveFromProposal(archiveId: String, auditLog: AuditLog) {
        withContext(Dispatchers.IO) {
            appDatabase.withTransaction {
                arsipDao.updateArchivesProposal(listOf(archiveId), "AVAILABLE", null)
                auditLogDao.insertAuditLog(auditLog.toEntity())
            }
        }
    }
}
