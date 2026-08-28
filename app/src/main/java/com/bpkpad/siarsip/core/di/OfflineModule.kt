package com.bpkpad.siarsip.core.di

import com.bpkpad.siarsip.core.database.dao.ArsipDao
import com.bpkpad.siarsip.core.network.SupabaseSyncManager
import com.example.arsipbpkpad.data.mapper.toDomain
import com.example.arsipbpkpad.data.mapper.toEntity
import com.example.arsipbpkpad.data.mapper.toKeuanganDomain
import com.example.arsipbpkpad.domain.model.*
import com.example.arsipbpkpad.domain.repository.*
import com.example.arsipbpkpad.domain.service.ExcelService
import com.example.arsipbpkpad.utils.ResultState as KeuanganResultState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OfflineModule {

    @Provides
    @Singleton
    fun provideKeuanganAuthRepository(): AuthRepository = object : AuthRepository {
        override val currentUserRole: StateFlow<UserRole> = MutableStateFlow(UserRole.SUPER_ADMIN)
        override val isUserLoggedIn: StateFlow<Boolean> = MutableStateFlow(true)
        override val currentUserProfile: StateFlow<UserProfile?> = MutableStateFlow(
            UserProfile(id = "local-admin", email = "admin@bpkpad.balangankab.go.id", fullName = "Admin BPKPAD", role = UserRole.SUPER_ADMIN, isActive = true)
        )
        override val isSessionChecked: StateFlow<Boolean> = MutableStateFlow(true)

        override suspend fun login(email: String, password: String, rememberMe: Boolean): DomainResult<Unit> = DomainResult.Success(Unit)
        override suspend fun logout(): DomainResult<Unit> = DomainResult.Success(Unit)
        override suspend fun checkSession(): Boolean = true
        override fun getCurrentUserId(): String = "local-admin"
        override fun getCurrentUserEmail(): String = "admin@bpkpad.balangankab.go.id"
        override fun getCurrentUserFullName(): String = "Admin BPKPAD"
        override fun getSavedEmail(): String = "admin@bpkpad.balangankab.go.id"
        override fun getSavedPassword(): String = ""
        override fun isRememberMeEnabled(): Boolean = false
    }

    @Provides
    @Singleton
    fun provideTransactionBundleRepository(): TransactionBundleRepository = object : TransactionBundleRepository {
        override suspend fun createBundle(name: String, description: String?, year: Int): DomainResult<String> =
            DomainResult.Success("bundle-1")

        override suspend fun softDeleteBundle(bundleId: String): DomainResult<Unit> = DomainResult.Success(Unit)
    }

    @Provides
    @Singleton
    fun provideOcrRepository(): OcrRepository = object : OcrRepository {
        override suspend fun extractText(imageIdentifier: String): DomainResult<String> =
            DomainResult.Success("Extracted text from $imageIdentifier")
    }

    @Provides
    @Singleton
    fun provideAiParserRepository(): AiParserRepository = object : AiParserRepository {
        override suspend fun parseMetadata(rawText: String): DomainResult<ParsedMetadata> =
            DomainResult.Success(
                ParsedMetadata(
                    docNumber = "001/SP2D/2026",
                    year = 2026,
                    subject = "Pengelolaan Keuangan Daerah",
                    docType = "SP2D",
                    nominal = 15000000.0,
                    isArchiveDocument = true
                )
            )
    }

    @Provides
    @Singleton
    fun provideStorageLocationRepository(): StorageLocationRepository = object : StorageLocationRepository {
        private val rooms = listOf(
            Room("room-1", "Gudang Kearsipan Utama"),
            Room("room-2", "Gudang Kearsipan Cadangan")
        )
        private val shelves = listOf(
            Shelf("shelf-1", "room-1", "Rak Storage A1"),
            Shelf("shelf-2", "room-1", "Rak Storage A2"),
            Shelf("shelf-3", "room-2", "Rak Storage B1")
        )
        private val boxes = listOf(
            BoxDetails("box-101", "BOX-2026-01", "shelf-1", "Rak Storage A1", "room-1", "Gudang Kearsipan Utama", 45),
            BoxDetails("box-102", "BOX-2026-02", "shelf-1", "Rak Storage A1", "room-1", "Gudang Kearsipan Utama", 32),
            BoxDetails("box-103", "BOX-2026-03", "shelf-3", "Rak Storage B1", "room-2", "Gudang Kearsipan Cadangan", 50)
        )

        override suspend fun getOrCreateLocation(room: String, shelf: String, boxNumber: String, year: String): DomainResult<String> =
            DomainResult.Success("loc-1")

        override fun getRooms(): Flow<KeuanganResultState<List<Room>>> = flowOf(KeuanganResultState.Success(rooms))
        override fun getShelvesByRoom(roomId: String): Flow<KeuanganResultState<List<Shelf>>> =
            flowOf(KeuanganResultState.Success(shelves.filter { it.roomId == roomId }))
        override fun getBoxesByShelf(shelfId: String): Flow<KeuanganResultState<List<Box>>> =
            flowOf(KeuanganResultState.Success(listOf(Box("box-101", shelfId, "BOX-2026-01"))))
        override fun getAllBoxes(): Flow<KeuanganResultState<List<BoxDetails>>> = flowOf(KeuanganResultState.Success(boxes))

        override suspend fun createRoom(name: String): Result<Room> = Result.success(Room("room-${System.currentTimeMillis()}", name))
        override suspend fun updateRoom(id: String, name: String): Result<Unit> = Result.success(Unit)
        override suspend fun deleteRoom(id: String): Result<Unit> = Result.success(Unit)

        override suspend fun createShelf(roomId: String, name: String): Result<Shelf> = Result.success(Shelf("shelf-${System.currentTimeMillis()}", roomId, name))
        override suspend fun updateShelf(id: String, name: String): Result<Unit> = Result.success(Unit)
        override suspend fun deleteShelf(id: String): Result<Unit> = Result.success(Unit)

        override suspend fun createBox(shelfId: String, name: String): Result<Box> = Result.success(Box("box-${System.currentTimeMillis()}", shelfId, name))
        override suspend fun updateBox(id: String, name: String): Result<Unit> = Result.success(Unit)
        override suspend fun deleteBox(id: String): Result<Unit> = Result.success(Unit)

        override suspend fun getRoomByName(name: String): Room? = rooms.find { it.name.equals(name, ignoreCase = true) }
        override suspend fun getShelfByName(roomId: String, name: String): Shelf? = shelves.find { it.roomId == roomId && it.name.equals(name, ignoreCase = true) }
        override suspend fun checkBoxExists(shelfId: String, name: String): Boolean = false
    }

    @Provides
    @Singleton
    fun provideDocumentTypeRepository(): DocumentTypeRepository = object : DocumentTypeRepository {
        private val types = listOf(
            DocumentType("SP2D", "Surat Perintah Pencairan Dana", isSystem = true, isActive = true),
            DocumentType("SPM", "Surat Perintah Membayar", isSystem = true, isActive = true),
            DocumentType("SPJ", "Surat Pertanggungjawaban", isSystem = true, isActive = true),
            DocumentType("SPP", "Surat Permintaan Pembayaran", isSystem = true, isActive = true)
        )

        override suspend fun getActiveDocumentTypes(): DomainResult<List<DocumentType>> =
            DomainResult.Success(types)

        override suspend fun ensureDocumentTypeExists(type: String): DomainResult<Unit> = DomainResult.Success(Unit)

        override fun observeDocumentTypes(): Flow<List<DocumentType>> = flowOf(types)
    }

    @Provides
    @Singleton
    fun provideKeuanganStagingRepository(): StagingRepository = object : StagingRepository {
        private val _stagedBoxes = MutableStateFlow<List<StagedBox>>(
            listOf(
                StagedBox(
                    sessionId = "session-1",
                    warehouse = "Gudang Kearsipan Utama",
                    rack = "Rak Storage A1",
                    box = "BOX-2026-01",
                    year = "2026",
                    itemCount = 12
                )
            )
        )
        private val _stagingArchives = MutableStateFlow<List<ArchiveDocument>>(emptyList())

        override fun getAllStagedBoxes(): Flow<List<StagedBox>> = _stagedBoxes
        override suspend fun saveStagedBox(box: StagedBox) {
            _stagedBoxes.value = _stagedBoxes.value + box
        }
        override suspend fun deleteStagedBox(sessionId: String) {
            _stagedBoxes.value = _stagedBoxes.value.filter { it.sessionId != sessionId }
        }
        override suspend fun getStagedBoxById(sessionId: String): StagedBox? = _stagedBoxes.value.find { it.sessionId == sessionId }
        override suspend fun checkStagedBoxExists(warehouse: String, rack: String, year: String, box: String, excludeSessionId: String?): Boolean = false

        override fun getAllStagingArchives(): Flow<List<ArchiveDocument>> = _stagingArchives
        override fun getStagingArchivesBySession(sessionId: String): Flow<List<ArchiveDocument>> =
            _stagingArchives.map { list -> list.filter { it.boxSessionId == sessionId } }
        override suspend fun insertToStaging(archive: ArchiveDocument) {
            _stagingArchives.value = _stagingArchives.value + archive
        }
        override suspend fun deleteFromStaging(id: String) {
            _stagingArchives.value = _stagingArchives.value.filter { it.id != id }
        }
        override suspend fun clearStagingBySession(sessionId: String) {
            _stagingArchives.value = _stagingArchives.value.filter { it.boxSessionId != sessionId }
        }
        override suspend fun clearAllStaging() {
            _stagingArchives.value = emptyList()
        }
    }

    @Provides
    @Singleton
    fun provideKeuanganArchiveRepository(
        dao: com.example.arsipbpkpad.data.local.dao.ArchiveDao,
        arsipDao: ArsipDao,
        syncManager: SupabaseSyncManager
    ): ArchiveRepository = object : ArchiveRepository {
        private val sampleClassifications = listOf(
            ClassificationCode("900.1.3.1", "Pengelolaan Keuangan Daerah", "900", 3, true),
            ClassificationCode("900.1.3.2", "Belanja Modal & Barang", "900", 3, true),
            ClassificationCode("900.1.3.3", "Pertanggungjawaban SPJ", "900", 3, true)
        )

        override fun getArchivesFlow(query: String?, years: List<Int>): Flow<List<ArchiveDocument>> = flow {
            val items = try {
                val selectedYear = years.firstOrNull()?.toString()
                val result = syncManager.fetchArchivesRemotePaginated(
                    sumber = "Keuangan",
                    status = null,
                    tahun = selectedYear,
                    query = query,
                    limit = 1000,
                    offset = 0
                )
                val mapped = result.items.map { it.toKeuanganDomain() }
                if (mapped.isNotEmpty()) {
                    mapped
                } else {
                    val roomEntities = arsipDao.getArchivesFiltered("Keuangan", null, selectedYear, query, 1000, 0).first()
                    roomEntities.map { it.toKeuanganDomain() }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                emptyList()
            }
            emit(items)
        }

        override fun getArchivesList(query: String?, years: List<Int>): Flow<DomainResult<List<ArchiveDocument>>> = flow {
            val res = try {
                val selectedYear = years.firstOrNull()?.toString()
                val result = syncManager.fetchArchivesRemotePaginated(
                    sumber = "Keuangan",
                    status = null,
                    tahun = selectedYear,
                    query = query,
                    limit = 1000,
                    offset = 0
                )
                val mapped = result.items.map { it.toKeuanganDomain() }
                if (mapped.isNotEmpty()) {
                    DomainResult.Success(mapped)
                } else {
                    val roomEntities = arsipDao.getArchivesFiltered("Keuangan", null, selectedYear, query, 1000, 0).first()
                    DomainResult.Success(roomEntities.map { it.toKeuanganDomain() })
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                DomainResult.Success(emptyList<ArchiveDocument>())
            }
            emit(res)
        }

        override fun getArchiveDetail(id: String): Flow<DomainResult<ArchiveDocument>> = flow {
            val res = try {
                val entity = arsipDao.getArchivesFiltered(null, null, null, null, 2000, 0).first().find { it.id == id }
                if (entity != null) {
                    DomainResult.Success(entity.toKeuanganDomain())
                } else {
                    val localEntity = dao.getArchiveByIdSync(id)
                    if (localEntity != null) {
                        DomainResult.Success(localEntity.toDomain())
                    } else {
                        DomainResult.Error("Document not found")
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                DomainResult.Error("Document not found: ${e.message}")
            }
            emit(res)
        }

        override suspend fun checkDocumentNumberAndTypeExists(docNumber: String, copyType: String): Boolean =
            dao.existsByDocumentNumberAndType(docNumber, copyType)

        override suspend fun checkDocumentNumberExists(docNumber: String): Boolean =
            dao.existsByDocumentNumber(docNumber)

        override suspend fun saveArchive(archive: ArchiveDocument): DomainResult<Boolean> {
            dao.insertArchive(archive.toEntity())
            return DomainResult.Success(true)
        }

        override suspend fun saveArchives(archives: List<ArchiveDocument>): DomainResult<Boolean> {
            dao.insertArchives(archives.map { it.toEntity() })
            return DomainResult.Success(true)
        }

        override suspend fun deleteArchive(id: String): DomainResult<Unit> {
            dao.hardDeleteArchiveById(id)
            return DomainResult.Success(Unit)
        }

        override suspend fun softDeleteArchive(id: String): DomainResult<Unit> {
            dao.softDeleteArchiveById(id, System.currentTimeMillis().toString())
            return DomainResult.Success(Unit)
        }

        override suspend fun deleteArchiveWithBundleCleanup(id: String): DomainResult<Unit> {
            dao.hardDeleteArchiveById(id)
            return DomainResult.Success(Unit)
        }

        override suspend fun deleteEntireBundle(bundleId: String): DomainResult<Unit> {
            dao.softDeleteArchivesByBundleId(bundleId, System.currentTimeMillis().toString())
            return DomainResult.Success(Unit)
        }

        override suspend fun getActiveBundleArchiveCount(bundleId: String): DomainResult<Int> =
            DomainResult.Success(dao.countActiveArchivesByBundleId(bundleId))

        override suspend fun syncArchives(): DomainResult<Unit> = DomainResult.Success(Unit)
        override suspend fun syncPendingArchives(): DomainResult<Unit> = DomainResult.Success(Unit)

        override fun getArchivedYears(): Flow<List<Int>> = flowOf(listOf(2024, 2023, 2022, 2021, 2020, 2019, 2018, 2017, 2016, 2015))

        override fun getYearStats(): Flow<List<YearStats>> = flowOf(
            listOf(2024, 2023, 2022, 2021, 2020, 2019, 2018, 2017, 2016, 2015).map { y ->
                YearStats(y, 1000, null)
            }
        )

        override fun getArchivesByBundleId(bundleId: String): Flow<List<ArchiveDocument>> =
            dao.getArchivesByBundleId(bundleId).map { list -> list.map { it.toDomain() } }

        override fun getAnalyticsData(year: Int): Flow<DomainResult<AnalyticsData>> =
            dao.getTotalBudgetByYear(year).map { total -> DomainResult.Success(AnalyticsData(total ?: 0.0)) }

        override fun getAnalyticsDataForRange(startYear: Int, endYear: Int): Flow<DomainResult<AnalyticsData>> =
            dao.getTotalBudgetForRange(startYear, endYear).map { total -> DomainResult.Success(AnalyticsData(total ?: 0.0)) }

        override suspend fun uploadImage(id: String, imageByteArray: ByteArray): DomainResult<String> =
            DomainResult.Success(id)

        override suspend fun syncClassificationCodes(): DomainResult<Unit> = DomainResult.Success(Unit)

        override fun observeClassificationCodes(): Flow<List<ClassificationCode>> = flowOf(sampleClassifications)
    }

    @Provides
    @Singleton
    fun provideActivityLogRepository(): ActivityLogRepository = object : ActivityLogRepository {
        override suspend fun logActivity(log: ActivityLog): DomainResult<Unit> = DomainResult.Success(Unit)
        override fun getActivityLogsForEntity(entityId: String, entityType: String): Flow<DomainResult<List<ActivityLog>>> = flowOf(DomainResult.Success(emptyList()))
    }

    @Provides
    @Singleton
    fun provideExcelService(): ExcelService = com.example.arsipbpkpad.data.service.ExcelServiceImpl(Dispatchers.IO)
}
