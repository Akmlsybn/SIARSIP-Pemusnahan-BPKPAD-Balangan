package com.bpkpad.siarsip.feature.arsip.domain.usecase

import com.bpkpad.siarsip.core.utils.JraRules
import com.bpkpad.siarsip.feature.arsip.domain.model.Arsip
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * UseCase untuk mengambil daftar arsip berstatus AVAILABLE yang telah memenuhi
 * seluruh kriteria kelayakan pemusnahan berdasarkan Jadwal Retensi Arsip (JRA).
 *
 * Menerapkan Hybrid JRA System:
 * - Arsip Keuangan (Prefix "900"): Retensi 10 tahun (2 aktif + 8 inaktif).
 * - Arsip Non-Keuangan: Retensi 5 tahun (default 2 aktif + 3 inaktif).
 * - Mengunci arsip berstatus atau berketerangan PERMANEN.
 */
class GetAvailableArchivesUseCase @Inject constructor(
    private val repository: ArsipRepository
) {
    /**
     * Mengambil arsip yang layak musnah secara terpaginasi untuk Form BUM.
     */
    operator fun invoke(
        sumber: String? = null,
        tahun: String? = null,
        query: String? = null,
        limit: Int = 25,
        offset: Int = 0
    ): Flow<List<Arsip>> {
        return repository.getArchivesFiltered(
            sumber = sumber,
            status = "AVAILABLE",
            tahun = tahun,
            query = query,
            limit = limit,
            offset = offset,
            onlyEligibleForDisposal = true
        )
    }

    /**
     * Menghitung total arsip yang layak musnah untuk paginasi Form BUM.
     */
    fun count(
        sumber: String? = null,
        tahun: String? = null,
        query: String? = null
    ): Flow<Int> {
        return repository.countArchivesFiltered(
            sumber = sumber,
            status = "AVAILABLE",
            tahun = tahun,
            query = query,
            onlyEligibleForDisposal = true
        )
    }

    /**
     * Mengambil arsip yang layak musnah dari cache lokal / unpaginated.
     */
    operator fun invoke(): Flow<List<Arsip>> {
        val currentYear = JraRules.getCurrentYear()
        return repository.getAvailableArchives().map { archives ->
            archives.filter { archive ->
                JraRules.isEligibleForDisposal(archive, currentYear)
            }
        }
    }
}
