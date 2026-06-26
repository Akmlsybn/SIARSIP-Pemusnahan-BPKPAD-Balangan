package com.bpkpad.siarsip.feature.auth.data.mapper

import com.bpkpad.siarsip.core.database.entity.UserEntity
import com.bpkpad.siarsip.feature.auth.domain.model.User

fun UserEntity.toDomain(): User = User(
    id = id,
    username = username
)