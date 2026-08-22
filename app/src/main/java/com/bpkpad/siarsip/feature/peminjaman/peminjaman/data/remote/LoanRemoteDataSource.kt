package com.bpkpad.peminjaman.peminjaman.data.remote

import com.bpkpad.peminjaman.core.database.entity.DetailPeminjamanEntity
import com.bpkpad.peminjaman.core.database.entity.MasterDokumenEntity
import com.bpkpad.peminjaman.core.database.entity.TransaksiEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoanRemoteDataSource @Inject constructor() {
    suspend fun findArchiveDocumentId(documentNumber: String): String? = null

    suspend fun ensureTransaction(
        transaction: TransaksiEntity,
        details: List<DetailPeminjamanEntity>,
        documents: Map<Int, MasterDokumenEntity>
    ): String {
        return transaction.syncKey
    }

    suspend fun syncState(
        remoteId: String,
        transaction: TransaksiEntity,
        details: List<DetailPeminjamanEntity>,
        documents: Map<Int, MasterDokumenEntity>
    ) {
    }

    suspend fun upsertAgency(name: String, address: String?, code: String?): String {
        return "agency_1"
    }

    data class RemoteProfileDto(
        val id: String,
        val legacyId: Long,
        val username: String,
        val namaLengkap: String,
        val nip: String? = null,
        val role: String,
        val noHp: String? = null,
        val isActive: Boolean
    )

    data class RemoteLoanItem(
        val loanTransactionId: String,
        val archiveDocumentId: String,
        val documentNumberSnapshot: String,
        val titleSnapshot: String,
        val yearSnapshot: Int?,
        val locationSnapshot: String?,
        val returnCondition: String? = null,
        val conditionNote: String? = null
    )

    data class RemoteAgencyDto(
        val namaInstansi: String
    )

    data class RemoteTransactionResponse(
        val id: String,
        val clientReference: String?,
        val borrowerAgencyId: String,
        val picNama: String,
        val picNoHp: String,
        val nomorSuratPengantar: String,
        val fotoSuratPengantarPath: String,
        val qrCodeToken: String?,
        val tanggalPinjam: String,
        val tanggalKembaliRencana: String,
        val tanggalKembaliAktual: String?,
        val status: String,
        val metodePersetujuan: String?,
        val buktiBypassPath: String?,
        val catatanBypass: String?,
        val isBypassAcknowledged: Boolean,
        val alasanPenolakan: String?,
        val createdBy: String,
        val approvedBy: String?,
        val agency: RemoteAgencyDto? = null,
        val items: List<RemoteLoanItem> = emptyList()
    )

    suspend fun getAllProfiles(): List<RemoteProfileDto> = emptyList()

    suspend fun getAllTransactions(): List<RemoteTransactionResponse> = emptyList()
}
