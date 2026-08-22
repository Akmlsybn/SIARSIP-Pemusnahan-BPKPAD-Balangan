package com.bpkpad.arsip.domain.usecase

import com.bpkpad.arsip.core.domain.model.DocumentType
import com.bpkpad.arsip.domain.model.ArchiveDocument
import com.bpkpad.arsip.utils.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import javax.inject.Inject

class ParseMetadataWithAiUseCase @Inject constructor() {
    operator fun invoke(rawText: String, type: DocumentType = DocumentType.SURAT): Flow<ResultState<ArchiveDocument>> = flow {
        emit(ResultState.Loading)
        
        try {
            val title = if (rawText.isNotBlank()) {
                rawText.lines().firstOrNull { it.isNotBlank() } ?: "Dokumen ${type.name}"
            } else {
                "Dokumen ${type.name}"
            }
            val description = if (rawText.length > 50) rawText.take(150) + "..." else rawText
            
            emit(ResultState.Success(
                ArchiveDocument(
                    title = title,
                    type = type.name,
                    description = description,
                    date = System.currentTimeMillis()
                )
            ))
        } catch (e: Exception) {
            emit(ResultState.Error("Gagal mengurai respon: ${e.message}"))
        }
    }
}
