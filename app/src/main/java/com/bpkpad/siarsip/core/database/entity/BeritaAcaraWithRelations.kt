package com.bpkpad.siarsip.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class BeritaAcaraWithRelations(
    @Embedded
    val beritaAcara: BeritaAcaraEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "beritaAcaraId"
    )
    val signatories: List<PenandatanganEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "beritaAcaraId"
    )
    val archives: List<ArsipEntity>
)
