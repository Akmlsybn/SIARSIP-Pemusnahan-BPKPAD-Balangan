package com.bpkpad.siarsip.feature.arsip.domain.usecase

import com.bpkpad.siarsip.feature.arsip.domain.model.BerkasUsulMusnah
import com.bpkpad.siarsip.feature.arsip.domain.repository.ArsipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProposalByNomorUseCase @Inject constructor(
    private val repository: ArsipRepository
) {
    operator fun invoke(nomor: String): Flow<BerkasUsulMusnah?> {
        return repository.getAllProposals().map { list ->
            list.firstOrNull { it.nomorBerkas == nomor }
        }
    }
}
