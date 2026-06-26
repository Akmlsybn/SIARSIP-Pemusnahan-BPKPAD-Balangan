package com.bpkpad.siarsip.feature.arsip.domain.usecase

import com.bpkpad.siarsip.feature.arsip.domain.model.Arsip
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class GetAvailableArchivesUseCase @Inject constructor(
    private val repository: ArsipRepository
) {
    operator fun invoke(): Flow<List<Arsip>> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return repository.getAvailableArchives().map { archives ->
            archives.filter { archive ->
                val docYear = archive.tahun.toIntOrNull() ?: 0
                (docYear + 10) <= currentYear
            }
        }
    }
}
