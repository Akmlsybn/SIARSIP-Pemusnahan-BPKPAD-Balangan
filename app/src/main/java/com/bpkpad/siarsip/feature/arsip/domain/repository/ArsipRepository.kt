package com.bpkpad.siarsip.feature.arsip.domain.repository

import com.bpkpad.siarsip.feature.arsip.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ArsipRepository {
    fun getAllArchives(): Flow<List<Arsip>>
    fun getAvailableArchives(): Flow<List<Arsip>>
    fun getArchivesByProposal(proposalId: String): Flow<List<Arsip>>
    fun getAllProposals(): Flow<List<BerkasUsulMusnah>>
    fun getProposalById(id: String): Flow<BerkasUsulMusnah?>
    fun getAllBeritaAcara(): Flow<List<BeritaAcara>>
    fun getBeritaAcaraById(id: String): Flow<BeritaAcara?>
    fun getPenandatanganForBeritaAcara(beritaAcaraId: String): Flow<List<Penandatangan>>
    fun getAllAuditLogs(): Flow<List<AuditLog>>
    
    suspend fun insertProposal(proposal: BerkasUsulMusnah, archiveIds: List<String>, auditLogs: List<AuditLog>)
    suspend fun updateProposalStatus(proposalId: String, status: String)
    suspend fun insertBeritaAcara(beritaAcara: BeritaAcara, archiveIds: List<String>, signatories: List<Penandatangan>)
    suspend fun insertAuditLog(log: AuditLog)
}
