package com.bpkpad.siarsip.feature.arsip.data.repository

import com.bpkpad.siarsip.core.database.dao.*
import com.bpkpad.siarsip.feature.arsip.data.mapper.*
import com.bpkpad.siarsip.feature.arsip.domain.model.*
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArsipRepositoryImpl @Inject constructor(
    private val arsipDao: ArsipDao,
    private val berkasUsulMusnahDao: BerkasUsulMusnahDao,
    private val beritaAcaraDao: BeritaAcaraDao,
    private val penandatanganDao: PenandatanganDao,
    private val auditLogDao: AuditLogDao
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

    override fun getArchivesByProposal(proposalId: String): Flow<List<Arsip>> {
        return arsipDao.getArchivesByProposal(proposalId)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getAllProposals(): Flow<List<BerkasUsulMusnah>> {
        return berkasUsulMusnahDao.getAllProposals()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getProposalById(id: String): Flow<BerkasUsulMusnah?> {
        return berkasUsulMusnahDao.getProposalById(id)
            .map { entity -> entity?.toDomain() }
            .flowOn(Dispatchers.IO)
    }

    override fun getAllBeritaAcara(): Flow<List<BeritaAcara>> {
        return beritaAcaraDao.getAllBeritaAcara()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getBeritaAcaraById(id: String): Flow<BeritaAcara?> {
        return beritaAcaraDao.getBeritaAcaraById(id)
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

    override suspend fun insertProposal(proposal: BerkasUsulMusnah, archiveIds: List<String>) = withContext(Dispatchers.IO) {
        berkasUsulMusnahDao.insertProposal(proposal.toEntity())
        arsipDao.updateArchivesProposal(archiveIds, "PROPOSED", proposal.id)
    }

    override suspend fun updateProposalStatus(proposalId: String, status: String) = withContext(Dispatchers.IO) {
        berkasUsulMusnahDao.updateProposalStatus(proposalId, status)
        arsipDao.updateArchivesStatusByProposal(proposalId, status)
    }

    override suspend fun insertBeritaAcara(
        beritaAcara: BeritaAcara,
        archiveIds: List<String>,
        signatories: List<Penandatangan>
    ) = withContext(Dispatchers.IO) {
        beritaAcaraDao.insertBeritaAcara(beritaAcara.toEntity())
        penandatanganDao.insertPenandatangan(signatories.map { it.toEntity() })
        val disposedAtStr = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date())
        arsipDao.disposeArchives(archiveIds, "DISPOSED", beritaAcara.id, disposedAtStr)
    }

    override suspend fun insertAuditLog(log: AuditLog) = withContext(Dispatchers.IO) {
        auditLogDao.insertAuditLog(log.toEntity())
    }
}
