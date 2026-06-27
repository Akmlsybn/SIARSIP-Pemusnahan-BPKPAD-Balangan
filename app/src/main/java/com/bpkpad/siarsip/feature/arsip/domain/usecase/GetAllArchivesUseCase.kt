package com.bpkpad.siarsip.feature.arsip.domain.usecase

import com.bpkpad.siarsip.feature.arsip.domain.model.Arsip
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllArchivesUseCase @Inject constructor(
    private val repository: ArsipRepository
) {
    operator fun invoke(): Flow<List<Arsip>> {
        return repository.getAllArchives().map { archives ->
            archives.filter { it.status != "DISPOSED" }
        }
    }
}
