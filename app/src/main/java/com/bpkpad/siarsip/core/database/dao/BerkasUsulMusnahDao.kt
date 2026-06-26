package com.bpkpad.siarsip.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bpkpad.siarsip.core.database.entity.BerkasUsulMusnahEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BerkasUsulMusnahDao {

    @Query("SELECT * FROM proposals ORDER BY createdAt DESC")
    fun getAllProposals(): Flow<List<BerkasUsulMusnahEntity>>

    @Query("SELECT * FROM proposals WHERE id = :id")
    fun getProposalById(id: String): Flow<BerkasUsulMusnahEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProposal(proposal: BerkasUsulMusnahEntity)

    @Query("UPDATE proposals SET status = :status WHERE id = :id")
    suspend fun updateProposalStatus(id: String, status: String)
}
