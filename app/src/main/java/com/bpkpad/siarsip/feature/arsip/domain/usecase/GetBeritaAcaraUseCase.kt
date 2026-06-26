package com.bpkpad.siarsip.feature.arsip.domain.usecase

import com.bpkpad.siarsip.feature.arsip.domain.model.BeritaAcaraItem
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class GetBeritaAcaraUseCase @Inject constructor(
    private val repository: ArsipRepository
) {
    operator fun invoke(): Flow<List<BeritaAcaraItem>> {
        return combine(
            repository.getAllBeritaAcara(),
            repository.getAllProposals()
        ) { beritaAcaras, proposals ->
            beritaAcaras.map { ba ->
                val proposalId = ba.archives.firstOrNull()?.proposalId
                val proposal = proposals.firstOrNull { it.id == proposalId }

                val instant = Instant.ofEpochMilli(ba.createdAt)
                val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()

                val formatterFull = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
                val tanggal = localDate.format(formatterFull)

                val day = localDate.dayOfMonth.toString().padStart(2, '0')
                val monthShort = localDate.format(DateTimeFormatter.ofPattern("MMM", Locale("id", "ID"))).uppercase()
                val year = localDate.year.toString()
                val tanggalShort = "$day\n$monthShort\n$year"

                BeritaAcaraItem(
                    id = ba.id,
                    nomor = ba.nomorBa,
                    berkasNomor = proposal?.nomorBerkas ?: "-",
                    perihal = proposal?.perihal ?: "Pemusnahan Arsip",
                    tanggal = tanggal,
                    tanggalShort = tanggalShort,
                    lokasi = ba.keterangan?.takeIf { it.isNotBlank() } ?: "Kantor BPKPAD Balangan",
                    metode = "Pencacahan (Shredding)",
                    jumlahArsip = ba.archives.size,
                    sumber = proposal?.sumberModul ?: "-",
                    tahun = year,
                    penandatangan = ba.signatories
                )
            }
        }
    }
}
