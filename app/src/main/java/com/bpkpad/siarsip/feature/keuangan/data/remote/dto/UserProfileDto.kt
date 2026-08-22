package com.example.arsipbpkpad.data.remote.dto





data class UserProfileDto(
     val id: String,
     val email: String,
     val fullName: String,
     val role: String,
     val isActive: Boolean = true
)
