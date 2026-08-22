package com.example.arsipbpkpad.data.mapper

import com.example.arsipbpkpad.data.remote.dto.DocumentTypeDto
import com.example.arsipbpkpad.domain.model.DocumentType

fun DocumentTypeDto.toDomain(): DocumentType {
    return DocumentType(
        code = code,
        name = name,
        isSystem = isSystem,
        isActive = isActive
    )
}

fun DocumentType.toDto(userId: String? = null): DocumentTypeDto {
    return DocumentTypeDto(
        code = code,
        name = name,
        isSystem = isSystem,
        isActive = isActive,
        createdBy = userId
    )
}
