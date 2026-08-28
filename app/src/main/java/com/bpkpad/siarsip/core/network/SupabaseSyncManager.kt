package com.bpkpad.siarsip.core.network

import android.util.Log
import com.bpkpad.siarsip.core.database.dao.ArsipDao
import com.bpkpad.siarsip.core.database.dao.BeritaAcaraDao
import com.bpkpad.siarsip.core.database.dao.BerkasUsulMusnahDao
import com.bpkpad.siarsip.core.database.entity.ArsipEntity
import com.bpkpad.siarsip.core.database.entity.BeritaAcaraEntity
import com.bpkpad.siarsip.core.database.entity.BerkasUsulMusnahEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class SupabaseArchiveDto(
    val id: String,
    val kode: String,
    @SerialName("full_kode") val fullKode: String,
    val deskripsi: String,
    val tahun: String,
    val tingkat: String,
    val volume: String,
    @SerialName("retensi_aktif") val retensiAktif: String = "2 Tahun",
    @SerialName("retensi_inaktif") val retensiInaktif: String = "5 Tahun",
    val keterangan: String = "Musnah",
    val sumber: String = "Pemusnahan",
    val status: String = "AVAILABLE",
    @SerialName("nasib_akhir") val nasibAkhir: String = "MUSNAH",
    @SerialName("proposal_id") val proposalId: String? = null,
    @SerialName("berita_acara_id") val beritaAcaraId: String? = null,
    @SerialName("disposed_at") val disposedAt: String? = null
)

@Serializable
data class SupabaseProposalDto(
    val id: String,
    @SerialName("nomor_berkas") val nomorBerkas: String,
    val tanggal: String,
    @SerialName("unit_pengolah") val unitPengolah: String,
    @SerialName("sumber_modul") val sumberModul: String,
    val perihal: String,
    val status: String = "PROPOSED",
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class SupabaseBeritaAcaraDto(
    val id: String,
    @SerialName("nomor_ba") val nomorBa: String,
    @SerialName("tanggal_eksekusi") val tanggalEksekusi: String,
    @SerialName("penanggung_jawab") val penanggungJawab: String,
    val saksi1: String,
    val saksi2: String? = null,
    val keterangan: String? = null,
    val metode: String = "Pencacahan",
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis(),
    @SerialName("foto_dokumentasi_uri") val fotoDokumentasiUri: String? = null
)

data class PaginatedArchivesResult(
    val items: List<ArsipEntity>,
    val totalCount: Int
)

@Singleton
class SupabaseSyncManager @Inject constructor(
    private val supabase: SupabaseClient,
    private val arsipDao: ArsipDao,
    private val proposalDao: BerkasUsulMusnahDao,
    private val beritaAcaraDao: BeritaAcaraDao
) {
    companion object {
        private const val TAG = "SupabaseSync"
    }

    suspend fun syncAllData() = withContext(Dispatchers.IO) {
        try {
            syncProposals()
            syncBeritaAcara()
            syncTop100PerYearForOffline()
        } catch (e: Exception) {
            Log.w(TAG, "Sync proposals/BA/offline top 100 failed: ${e.message}")
        }
    }

    private suspend fun syncTop100PerYearForOffline() {
        val years = listOf("2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015")
        for (year in years) {
            try {
                val remoteDocs = supabase.from("archive_documents").select {
                    filter {
                        eq("tahun", year)
                        eq("status", "AVAILABLE")
                    }
                    order("id", order = Order.ASCENDING)
                    range(0, 99) // Top 100 per year
                }.decodeList<SupabaseArchiveDto>()

                if (remoteDocs.isNotEmpty()) {
                    val entities = remoteDocs.map { dto ->
                        ArsipEntity(
                            id = dto.id,
                            kode = dto.kode,
                            fullKode = dto.fullKode,
                            deskripsi = dto.deskripsi,
                            tahun = dto.tahun,
                            tingkat = dto.tingkat,
                            volume = dto.volume,
                            retensiAktif = dto.retensiAktif,
                            retensiInaktif = dto.retensiInaktif,
                            keterangan = dto.keterangan,
                            sumber = dto.sumber,
                            status = dto.status,
                            proposalId = dto.proposalId,
                            beritaAcaraId = dto.beritaAcaraId,
                            disposedAt = dto.disposedAt,
                            nasibAkhir = dto.nasibAkhir
                        )
                    }
                    arsipDao.insertArchives(entities)
                    Log.d(TAG, "Pre-cached top ${entities.size} items for year $year in Room DB for offline mode.")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed pre-caching top 100 for year $year: ${e.message}")
            }
        }
    }

    suspend fun fetchArchivesRemotePaginated(
        sumber: String?,
        status: String?,
        tahun: String?,
        query: String?,
        limit: Int,
        offset: Int
    ): PaginatedArchivesResult = withContext(Dispatchers.IO) {
        try {
            val result = supabase.from("archive_documents").select {
                count(Count.EXACT)
                filter {
                    if (!sumber.isNullOrBlank() && sumber != "Semua") {
                        eq("sumber", sumber)
                    }
                    if (!status.isNullOrBlank() && status != "Semua") {
                        eq("status", status)
                    }
                    if (!tahun.isNullOrBlank() && tahun != "Semua") {
                        eq("tahun", tahun)
                    }
                    if (!query.isNullOrBlank()) {
                        or {
                            ilike("kode", "%$query%")
                            ilike("deskripsi", "%$query%")
                        }
                    }
                }
                order("id", order = Order.ASCENDING)
                range(offset.toLong(), (offset + limit - 1).toLong())
            }

            val totalCount = result.countOrNull()?.toInt() ?: 0
            val remoteDocs = result.decodeList<SupabaseArchiveDto>()

            val entities = remoteDocs.map { dto ->
                ArsipEntity(
                    id = dto.id,
                    kode = dto.kode,
                    fullKode = dto.fullKode,
                    deskripsi = dto.deskripsi,
                    tahun = dto.tahun,
                    tingkat = dto.tingkat,
                    volume = dto.volume,
                    retensiAktif = dto.retensiAktif,
                    retensiInaktif = dto.retensiInaktif,
                    keterangan = dto.keterangan,
                    sumber = dto.sumber,
                    status = dto.status,
                    proposalId = dto.proposalId,
                    beritaAcaraId = dto.beritaAcaraId,
                    disposedAt = dto.disposedAt,
                    nasibAkhir = dto.nasibAkhir
                )
            }

            if (entities.isNotEmpty()) {
                arsipDao.insertArchives(entities)
            }

            Log.d(TAG, "Fetched ${entities.size} items from Supabase for range ($offset..${offset + limit}). Total matching in Cloud: $totalCount")
            PaginatedArchivesResult(entities, totalCount)
        } catch (e: Exception) {
            Log.w(TAG, "Failed fetching remote paginated archives, falling back to local Room DB: ${e.message}")
            val roomEntities = arsipDao.getArchivesFiltered(
                sumber = if (sumber == "Semua") null else sumber,
                status = if (status == "Semua") null else status,
                tahun = if (tahun == "Semua") null else tahun,
                query = query?.trim()?.takeIf { it.isNotBlank() },
                limit = limit,
                offset = offset
            ).first()

            val roomTotalCount = arsipDao.countArchivesFiltered(
                sumber = if (sumber == "Semua") null else sumber,
                status = if (status == "Semua") null else status,
                tahun = if (tahun == "Semua") null else tahun,
                query = query?.trim()?.takeIf { it.isNotBlank() }
            ).first()

            PaginatedArchivesResult(roomEntities, roomTotalCount)
        }
    }

    private suspend fun syncProposals() {
        try {
            val remoteProposals = supabase.from("proposals")
                .select()
                .decodeList<SupabaseProposalDto>()

            if (remoteProposals.isNotEmpty()) {
                val entities = remoteProposals.map { dto ->
                    BerkasUsulMusnahEntity(
                        id = dto.id,
                        nomorBerkas = dto.nomorBerkas,
                        tanggal = dto.tanggal,
                        unitPengolah = dto.unitPengolah,
                        sumberModul = dto.sumberModul,
                        perihal = dto.perihal,
                        status = dto.status,
                        createdAt = dto.createdAt
                    )
                }
                proposalDao.insertProposals(entities)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed syncing proposals: ${e.message}")
        }
    }

    private suspend fun syncBeritaAcara() {
        try {
            val remoteBA = supabase.from("berita_acara")
                .select()
                .decodeList<SupabaseBeritaAcaraDto>()

            if (remoteBA.isNotEmpty()) {
                val entities = remoteBA.map { dto ->
                    BeritaAcaraEntity(
                        id = dto.id,
                        nomorBa = dto.nomorBa,
                        tanggalEksekusi = dto.tanggalEksekusi,
                        penanggungJawab = dto.penanggungJawab,
                        saksi1 = dto.saksi1,
                        saksi2 = dto.saksi2,
                        keterangan = dto.keterangan,
                        metode = dto.metode,
                        createdAt = dto.createdAt,
                        fotoDokumentasiUri = dto.fotoDokumentasiUri
                    )
                }
                beritaAcaraDao.insertBeritaAcaraList(entities)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed syncing berita acara: ${e.message}")
        }
    }

    suspend fun pushProposalToCloud(proposal: BerkasUsulMusnahEntity) = withContext(Dispatchers.IO) {
        try {
            val dto = SupabaseProposalDto(
                id = proposal.id,
                nomorBerkas = proposal.nomorBerkas,
                tanggal = proposal.tanggal,
                unitPengolah = proposal.unitPengolah,
                sumberModul = proposal.sumberModul,
                perihal = proposal.perihal,
                status = proposal.status,
                createdAt = proposal.createdAt
            )
            supabase.from("proposals").insert(dto)
        } catch (e: Exception) {
            Log.e(TAG, "Failed pushing proposal to cloud: ${e.message}")
        }
    }

    suspend fun pushBeritaAcaraToCloud(ba: BeritaAcaraEntity) = withContext(Dispatchers.IO) {
        try {
            val dto = SupabaseBeritaAcaraDto(
                id = ba.id,
                nomorBa = ba.nomorBa,
                tanggalEksekusi = ba.tanggalEksekusi,
                penanggungJawab = ba.penanggungJawab,
                saksi1 = ba.saksi1,
                saksi2 = ba.saksi2,
                keterangan = ba.keterangan,
                metode = ba.metode,
                createdAt = ba.createdAt,
                fotoDokumentasiUri = ba.fotoDokumentasiUri
            )
            supabase.from("berita_acara").insert(dto)
        } catch (e: Exception) {
            Log.e(TAG, "Failed pushing berita acara to cloud: ${e.message}")
        }
    }
}
