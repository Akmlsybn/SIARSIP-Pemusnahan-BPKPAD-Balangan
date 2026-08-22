package com.example.arsipbpkpad.data.remote.dto





data class ClassificationCodeDto(
     val code: String,
     val name: String,
     val parentCode: String? = null,
     val level: Int = 1,
     val isActive: Boolean = true
)
