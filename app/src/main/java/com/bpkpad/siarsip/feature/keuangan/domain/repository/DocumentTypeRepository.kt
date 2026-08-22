package com.example.arsipbpkpad.domain.repository

import com.example.arsipbpkpad.domain.model.DocumentType
import com.example.arsipbpkpad.domain.model.DomainResult
import kotlinx.coroutines.flow.Flow

interface DocumentTypeRepository {
    suspend fun getActiveDocumentTypes(): DomainResult<List<DocumentType>>
    suspend fun ensureDocumentTypeExists(type: String): DomainResult<Unit>
    fun observeDocumentTypes(): Flow<List<DocumentType>>
}
