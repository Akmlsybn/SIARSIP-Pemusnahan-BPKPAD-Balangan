package com.bpkpad.siarsip.feature.arsip.domain.repository

import com.bpkpad.siarsip.feature.arsip.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ArsipRepository {
    fun getAllArchives(): Flow<List<Arsip>>
    fun getAvailableArchives(): Flow<List<Arsip>>
    fun getArchivesFiltered(
        sumber: String? = null,
        status: String? = null,
        tahun: String? = null,
        query: String? = null,
        limit: Int = 25,
        offset: Int = 0
    ): Flow<List<Arsip>>
    fun countArchivesFiltered(
        sumber: String? = null,
        status: String? = null,
        tahun: String? = null,
        query: String? = null
    ): Flow<Int>
    fun getArchivesByProposal(proposalId: String): Flow<List<Arsip>>
    fun getAllProposals(): Flow<List<BerkasUsulMusnah>>
    fun getProposalById(id: String): Flow<BerkasUsulMusnah?>
    fun getProposalByNomor(nomor: String): Flow<BerkasUsulMusnah?>
    fun getAllBeritaAcara(): Flow<List<BeritaAcara>>
    fun getBeritaAcaraById(id: String): Flow<BeritaAcara?>
    fun getPenandatanganForBeritaAcara(beritaAcaraId: String): Flow<List<Penandatangan>>
    fun getAllAuditLogs(): Flow<List<AuditLog>>
    
    suspend fun insertProposal(proposal: BerkasUsulMusnah, archiveIds: List<String>, auditLogs: List<AuditLog>)
    suspend fun updateProposalStatus(
        proposalId: String,
        status: String,
        auditLogs: List<AuditLog>,
        suratPertimbanganNomor: String? = null,
        suratPertimbanganPerihal: String? = null,
        jenisPersetujuanAkhir: String? = null,
        nomorPersetujuanAkhir: String? = null,
        perihalPersetujuanAkhir: String? = null
    )
    suspend fun insertBeritaAcara(beritaAcara: BeritaAcara, archiveIds: List<String>, signatories: List<Penandatangan>)
    suspend fun insertAuditLog(log: AuditLog)
    suspend fun deleteProposal(proposalId: String, auditLogs: List<AuditLog>)
    suspend fun removeArchiveFromProposal(archiveId: String, auditLog: AuditLog)
}
