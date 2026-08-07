package com.bpkpad.siarsip.feature.arsip.domain.usecase

import com.bpkpad.siarsip.feature.arsip.domain.model.*
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class CreateBeritaAcaraUseCase @Inject constructor(
    private val repository: ArsipRepository
) {
    suspend operator fun invoke(
        nomorBa: String,
        tanggalEksekusi: String,
        keterangan: String?,
        metode: String,
        proposalId: String,
        signatoriesInput: List<Penandatangan>,
        actorId: String,
        fotoDokumentasiUri: String? = null
    ): Result<Unit> {
        return try {
            // Validation
            if (nomorBa.isBlank()) return Result.failure(Exception("Nomor Berita Acara tidak boleh kosong"))
            if (tanggalEksekusi.isBlank()) return Result.failure(Exception("Tanggal eksekusi tidak boleh kosong"))
            
            val hasPj = signatoriesInput.any { it.role == "PENANGGUNG_JAWAB" && it.nama.isNotBlank() }
            val hasSaksi = signatoriesInput.any { it.role.startsWith("SAKSI") && it.nama.isNotBlank() }
            
            if (!hasPj) return Result.failure(Exception("Penanggung jawab wajib diisi"))
            if (!hasSaksi) return Result.failure(Exception("Minimal harus ada satu saksi"))
 
            val proposal = repository.getProposalById(proposalId).first()
                ?: return Result.failure(Exception("Proposal tidak ditemukan"))
 
            // Rule 5: Enforce State Machine (only APPROVED proposals can be DISPOSED)
            if (proposal.status != "APPROVED") {
                return Result.failure(Exception("Berkas usul musnah harus berstatus APPROVED sebelum membuat Berita Acara"))
            }
 
            val beritaAcaraId = UUID.randomUUID().toString()
            
            // Map signatories to have correct beritaAcaraId and urutan
            val mappedSignatories = signatoriesInput.filter { it.nama.isNotBlank() }.mapIndexed { index, sig ->
                sig.copy(
                    id = sig.id.ifBlank { UUID.randomUUID().toString() },
                    beritaAcaraId = beritaAcaraId,
                    urutan = index + 1
                )
            }
            
            // Extract top compatibility fields
            val pjNama = mappedSignatories.find { it.role == "PENANGGUNG_JAWAB" }?.nama ?: ""
            val saksi1Nama = mappedSignatories.filter { it.role.startsWith("SAKSI") }.getOrNull(0)?.nama ?: ""
            val saksi2Nama = mappedSignatories.filter { it.role.startsWith("SAKSI") }.getOrNull(1)?.nama
 
            // Fetch attached archives
            val archives = repository.getArchivesByProposal(proposalId).first()
            val archiveIds = archives.map { it.id }
 
            val beritaAcara = BeritaAcara(
                id = beritaAcaraId,
                nomorBa = nomorBa,
                tanggalEksekusi = tanggalEksekusi,
                penanggungJawab = pjNama,
                saksi1 = saksi1Nama,
                saksi2 = saksi2Nama,
                keterangan = keterangan,
                metode = metode,
                createdAt = System.currentTimeMillis(),
                fotoDokumentasiUri = fotoDokumentasiUri
            )
 
            // Execute in repository
            repository.insertBeritaAcara(beritaAcara, archiveIds, mappedSignatories)
            repository.updateProposalStatus(
                proposalId = proposalId,
                status = "DISPOSED",
                auditLogs = emptyList()
            )

            // Log Berita Acara creation
            repository.insertAuditLog(
                AuditLog(
                    id = UUID.randomUUID().toString(),
                    action = "CREATE_BERITA_ACARA",
                    actorId = actorId,
                    archiveId = null,
                    proposalId = proposalId,
                    beritaAcaraId = beritaAcaraId,
                    previousStatus = null,
                    newStatus = "DISPOSED",
                    notes = "Membuat Berita Acara Pemusnahan $nomorBa",
                    timestamp = System.currentTimeMillis()
                )
            )

            // Log proposal transition to DISPOSED
            repository.insertAuditLog(
                AuditLog(
                    id = UUID.randomUUID().toString(),
                    action = "DISPOSE_PROPOSAL",
                    actorId = actorId,
                    archiveId = null,
                    proposalId = proposalId,
                    beritaAcaraId = beritaAcaraId,
                    previousStatus = "APPROVED",
                    newStatus = "DISPOSED",
                    notes = "Proposal dimusnahkan secara fisik melalui Berita Acara $nomorBa",
                    timestamp = System.currentTimeMillis()
                )
            )

            // Log status transitions for each archive
            val timestampStr = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(Date())
            for (archive in archives) {
                repository.insertAuditLog(
                    AuditLog(
                        id = UUID.randomUUID().toString(),
                        action = "ARCHIVE_DISPOSED",
                        actorId = actorId,
                        archiveId = archive.id,
                        proposalId = proposalId,
                        beritaAcaraId = beritaAcaraId,
                        previousStatus = "APPROVED",
                        newStatus = "DISPOSED",
                        notes = "Arsip dimusnahkan secara fisik pada $timestampStr",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
