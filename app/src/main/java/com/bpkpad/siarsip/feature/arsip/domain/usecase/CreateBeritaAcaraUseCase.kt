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
        penanggungJawab: String,
        saksi1: String,
        saksi2: String?,
        keterangan: String?,
        proposalId: String,
        actorId: String
    ): Result<Unit> {
        return try {
            // Validation
            if (nomorBa.isBlank()) return Result.failure(Exception("Nomor Berita Acara tidak boleh kosong"))
            if (tanggalEksekusi.isBlank()) return Result.failure(Exception("Tanggal eksekusi tidak boleh kosong"))
            if (penanggungJawab.isBlank()) return Result.failure(Exception("Penanggung jawab tidak boleh kosong"))
            if (saksi1.isBlank()) return Result.failure(Exception("Saksi 1 tidak boleh kosong"))

            val proposal = repository.getProposalById(proposalId).first()
                ?: return Result.failure(Exception("Proposal tidak ditemukan"))

            // Rule 5: Enforce State Machine (only APPROVED proposals can be DISPOSED)
            if (proposal.status != "APPROVED") {
                return Result.failure(Exception("Berkas usul musnah harus berstatus APPROVED sebelum membuat Berita Acara"))
            }

            val beritaAcaraId = UUID.randomUUID().toString()
            
            // Build signatories
            val signatories = mutableListOf<Penandatangan>()
            signatories.add(
                Penandatangan(
                    id = UUID.randomUUID().toString(),
                    beritaAcaraId = beritaAcaraId,
                    nama = penanggungJawab,
                    jabatan = "Penanggung Jawab",
                    role = "PENANGGUNG_JAWAB",
                    urutan = 1
                )
            )
            signatories.add(
                Penandatangan(
                    id = UUID.randomUUID().toString(),
                    beritaAcaraId = beritaAcaraId,
                    nama = saksi1,
                    jabatan = "Saksi 1",
                    role = "SAKSI_1",
                    urutan = 2
                )
            )
            if (!saksi2.isNullOrBlank()) {
                signatories.add(
                    Penandatangan(
                        id = UUID.randomUUID().toString(),
                        beritaAcaraId = beritaAcaraId,
                        nama = saksi2,
                        jabatan = "Saksi 2",
                        role = "SAKSI_2",
                        urutan = 3
                    )
                )
            }

            // Fetch attached archives
            val archives = repository.getArchivesByProposal(proposalId).first()
            val archiveIds = archives.map { it.id }

            val beritaAcara = BeritaAcara(
                id = beritaAcaraId,
                nomorBa = nomorBa,
                tanggalEksekusi = tanggalEksekusi,
                penanggungJawab = penanggungJawab,
                saksi1 = saksi1,
                saksi2 = saksi2,
                keterangan = keterangan,
                createdAt = System.currentTimeMillis()
            )

            // Execute in repository
            repository.insertBeritaAcara(beritaAcara, archiveIds, signatories)
            repository.updateProposalStatus(proposalId, "DISPOSED")

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
