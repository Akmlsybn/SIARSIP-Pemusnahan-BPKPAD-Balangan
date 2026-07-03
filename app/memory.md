# MEMORY.md — SIARSIP Pemusnahan BPKPAD Balangan

> **Dibuat:** 2026-06-26
> **Tujuan:** Konteks codebase ini di-load setiap sesi AI baru agar tidak perlu re-scan dari awal.
> **Update:** Selalu update file ini setelah ada perubahan besar pada struktur/fitur.

---

## 1. Identitas Proyek

| Item | Value |
|------|-------|
| Nama App | SIARSIP — Sistem Informasi Arsip |
| Modul | Pemusnahan Arsip |
| Instansi | BPKPAD Kabupaten Balangan |
| Package | `com.bpkpad.siarsip` |
| Platform | Android (minSdk 26, targetSdk 36, compileSdk 36) |
| Bahasa | Kotlin |
| versi | 1.0 (versionCode 1) |

---

## 2. Stack Teknologi

| Layer | Teknologi |
|-------|-----------|
| UI | Jetpack Compose + Material3 |
| Arsitektur | MVVM + Clean Architecture |
| DI | Hilt (hilt-android + hilt.navigation.compose) |
| Database Lokal | Room (androidx.room) |
| Database Remote | Supabase PostgreSQL *(belum diimplementasikan)* |
| Async | Kotlin Coroutines + Flow |
| Navigation | Navigation Compose (2.8.5) |
| Build System | Gradle (KTS), KSP |
| Java Version | 17 |

---

## 3. Struktur Direktori

```
app/
├── Agent.md              ← Rules wajib baca sebelum coding
├── memory.md             ← File ini
├── build.gradle.kts
└── src/main/java/com/bpkpad/siarsip/
    ├── SiArsipApplication.kt         ← @HiltAndroidApp
    ├── MainActivity.kt               ← @AndroidEntryPoint, root NavGraph
    │
    ├── core/
    │   ├── database/
    │   │   ├── AppDatabase.kt        ← Room DB (v4: User, Arsip, Berkas, BA, Penandatangan, Log)
    │   │   ├── DatabaseCallback.kt   ← Seeder: admin (SHA-256) & 10 initial archives
    │   │   ├── dao/
    │   │   │   ├── UserDao.kt
    │   │   │   ├── ArsipDao.kt
    │   │   │   ├── BerkasUsulMusnahDao.kt
    │   │   │   ├── BeritaAcaraDao.kt
    │   │   │   ├── PenandatanganDao.kt
    │   │   │   └── AuditLogDao.kt
    │   │   └── entity/
    │   │       ├── UserEntity.kt
    │   │       ├── ArsipEntity.kt
    │   │       ├── BerkasUsulMusnahEntity.kt
    │   │       ├── BeritaAcaraEntity.kt
    │   │       ├── PenandatanganEntity.kt
    │   │       └── AuditLogEntity.kt
    │   ├── di/
    │   │   ├── DatabaseModule.kt     ← @Singleton AppDatabase & all DAOs
    │   │   ├── AuthModule.kt         ← @Binds AuthRepository → AuthRepositoryImpl
    │   │   └── ArsipModule.kt        ← @Binds ArsipRepository → ArsipRepositoryImpl
    │   ├── navigation/
    │   │   ├── Screen.kt             ← sealed class semua routes
    │   │   ├── SiArsipNavGraph.kt    ← NavHost + semua composable route
    │   │   └── PlaceholderScreen.kt  ← Sementara: Profil & Pengaturan
    │   └── utils/
    │       └── ResultState.kt        ← Generic UI state wrapper (Loading, Success, Error)
    │
    ├── feature/auth/
    │   ├── data/
    │   │   ├── mapper/UserMapper.kt          ← UserEntity.toDomain()
    │   │   └── repository/AuthRepositoryImpl.kt  ← login via Room (SHA-256 pw hash check)
    │   ├── domain/
    │   │   ├── model/User.kt                 ← data class User(id, username)
    │   │   ├── repository/AuthRepository.kt  ← interface: suspend login()
    │   │   └── usecase/LoginUseCase.kt       ← validasi + call repo.login()
    │   └── presentation/
    │       └── LoginViewModel.kt             ← UI State & invoke LoginUseCase
    │
    ├── feature/arsip/
    │   ├── data/
    │   │   ├── mapper/ArsipMapper.kt         ← Entity ↔ Domain mapper
    │   │   └── repository/ArsipRepositoryImpl.kt  ← implements ArsipRepository
    │   ├── domain/
    │   │   ├── model/                        ← Arsip, BerkasUsulMusnah, BeritaAcara, Penandatangan, AuditLog
    │   │   ├── repository/ArsipRepository.kt ← Domain repository interface
    │   │   └── usecase/                      ← UseCases: GetAvailable, GetProposals, CreateProposal, UpdateStatus, CreateBA, GetTrackingInfo
    │   └── presentation/
    │       ├── DaftarArsipViewModel.kt       ← Exposes uiState: StateFlow<ResultState<List<Arsip>>>
    │       ├── DaftarUsulMusnahViewModel.kt  ← Exposes uiState: StateFlow<ResultState<List<BerkasUsulMusnah>>>
    │       ├── BuatBerkasUsulMusnahViewModel.kt ← Exposes availableArchives, nextProposalNumber, and saveState
    │       └── StatusTrackingViewModel.kt     ← Exposes trackingList: StateFlow<ResultState<List<TrackingBerkas>>>
    │
    └── ui/
        ├── components/
        │   └── SideDrawer.kt    ← PemusnahanDrawerContent + DrawerRoutes
        ├── screens/
        │   ├── auth/LoginScreen.kt
        │   ├── dashboard/DashboardScreen.kt
        │   └── pemusnahan/
        │       ├── DaftararsipScreen.kt           ← 48 KB — tabel Excel, filter, kolom
        │       ├── DaftarUsulMusnahScreen.kt       ← 38 KB
        │       ├── BuatBerkasUsulMusnahScreen.kt   ← 43 KB — form buat berkas
        │       ├── DetailBerkasUsulMusnahScreen.kt ← 34 KB
        │       ├── StatusTrackingScreen.kt          ← 38 KB — timeline status
        │       ├── BeritaAcaraScreen.kt             ← 21 KB
        │       ├── DetailBeritaAcaraScreen.kt       ← 28 KB
        │       └── LogRiwayatScreen.kt              ← 34 KB — audit immutable
        └── theme/
            ├── Color.kt    ← Semua custom colors
            ├── Theme.kt    ← SiARSIPTheme (Material3)
            └── Type.kt
```

---

## 4. Navigation Routes (Screen.kt)

| Object | Route |
|--------|-------|
| `Screen.Login` | `"login"` |
| `Screen.Dashboard` | `"dashboard"` |
| `Screen.DaftarArsip` | `"daftar_arsip"` |
| `Screen.DaftarUsulMusnah` | `"daftar_usul_musnah"` |
| `Screen.BuatBerkasUsulMusnah` | `"buat_berkas_usul_musnah"` |
| `Screen.DetailBerkasUsulMusnah` | `"detail_berkas_usul_musnah/{berkasNomor}"` |
| `Screen.StatusTracking` | `"status_tracking"` |
| `Screen.BeritaAcara` | `"berita_acara"` |
| `Screen.DetailBeritaAcara` | `"detail_berita_acara/{baId}"` |
| `Screen.LogRiwayat` | `"log_riwayat"` |
| `Screen.Profil` | `"profil"` *(placeholder)* |
| `Screen.Pengaturan` | `"pengaturan"` *(placeholder)* |

**Start destination:** `Screen.Login`

**Drawer Routes (`DrawerRoutes` object):**
- `DAFTAR_ARSIP`, `DAFTAR_USUL_MUSNAH`, `TRACKING`, `BERITA_ACARA`, `LOG_RIWAYAT`, `PROFIL`, `PENGATURAN`

---

## 5. Color Palette (Color.kt)

| Token | Hex | Penggunaan |
|-------|-----|------------|
| `GreenPrimary` | `#1B5E20` | Warna utama, AppBar, tombol |
| `GreenDark` | `#155017` | Variant gelap |
| `GreenLight` | `#E8F5E9` | Background badge/chip hijau |
| `GreenMid` | `#C8E6C9` | Aksen mid |
| `BgPage` | `#EBF2F8` | Background halaman Login |
| `BgDashboard` | `#F8FAF8` | Background halaman lain |
| `CardWhite` | `#FFFFFF` | Card, sheet |
| `BorderGray` | `#E0E4E0` | Border divider |
| `TextHead` | `#1A231E` | Judul/heading |
| `TextBody` | `#454D47` | Body text |
| `TextHint` | `#717A6D` | Placeholder / hint |
| `DangerBg/Text` | `#FFDAD6 / #9B1919` | Error, musnah, terlambat |
| `AmberBg/Text` | `#FFFBEB / #B45309` | Warning, menunggu |
| `BlueBg/Text` | `#EFF6FF / #1D4ED8` | Info, peminjaman |
| `PurpleBg/Text` | `#F3E8FF / #6B21A8` | Non-keuangan tag |

---

## 6. Layer Arsitektur

```
UI (Compose Screen)
    ↓ event / uiState
ViewModel
    ↓ invoke
UseCase  ← Business Logic
    ↓
Repository (interface)
    ↓
RepositoryImpl
    ├── Room DAO (local)
    └── Supabase API (remote — belum ada)
```

**Aturan:**
- Compose hanya render UI, tidak boleh ada business logic / SQL langsung
- ViewModel: handle UIState + UserEvent + panggil UseCase
- UseCase: validasi + business rule
- Repository: return `Flow<ResultState<T>>` atau `suspend fun` → `Result<T>`
- Dispatchers.IO untuk semua DB work

---

## 7. Database Schema (Room — saat ini)

### `users` table (UserEntity)
| Kolom | Tipe | Keterangan |
|-------|------|------------|
| `id` | Long (autoGenerate) | PK |
| `username` | String | Login username |
| `passwordHash` | String | SHA-256 dari password |

**Default user (seeder):** `admin` / `admin123` (hash SHA-256)

> ✅ Catatan: `AuthRepositoryImpl` memverifikasi input password menggunakan SHA-256 hash.

### `archives` table (ArsipEntity)
| Kolom | Tipe | Keterangan |
|-------|------|------------|
| `id` | String | PK (UUID v4) |
| `kode` | String | Kode klasifikasi (JRA) |
| `fullKode` | String | Kode/nomor arsip lengkap |
| `deskripsi` | String | Isi ringkas informasi |
| `tahun` | String | Kurun waktu pembuatan |
| `tingkat` | String | Tingkat perkembangan (Asli/Copy) |
| `volume` | String | Volume fisik |
| `retensiAktif` | String | Jangka waktu retensi aktif |
| `retensiInaktif` | String | Jangka waktu retensi inaktif |
| `keterangan` | String | Status akhir (Musnah/Permanen) |
| `sumber` | String | Sumber modul asal |
| `status` | String | State (AVAILABLE, PROPOSED, VERIFIED, APPROVED, DISPOSED) |
| `proposalId` | String? | FK ke proposals (nullable) |
| `beritaAcaraId` | String? | FK ke berita_acara (nullable) |
| `disposedAt` | String? | Timestamp pemusnahan (nullable) |

### `proposals` table (BerkasUsulMusnahEntity)
| Kolom | Tipe | Keterangan |
|-------|------|------------|
| `id` | String | PK (UUID v4) |
| `nomorBerkas` | String | Nomor usulan berkas |
| `tanggal` | String | Tanggal usulan |
| `unitPengolah` | String | Instansi unit pengolah |
| `sumberModul` | String | Sumber modul JRA |
| `perihal` | String | Uraian perihal berkas |
| `status` | String | State (PROPOSED, VERIFIED, APPROVED, DISPOSED) |
| `createdAt` | Long | Unix timestamp pembuatan |
| `suratPertimbanganNomor` | String? | Nomor surat pertimbangan Tim Penilai |
| `suratPertimbanganPerihal` | String? | Perihal surat pertimbangan Tim Penilai |
| `jenisPersetujuanAkhir` | String? | Otoritas persetujuan (BUPATI / ANRI) |
| `nomorPersetujuanAkhir` | String? | Nomor surat persetujuan akhir |
| `perihalPersetujuanAkhir` | String? | Perihal surat persetujuan akhir |

### `berita_acara` table (BeritaAcaraEntity)
| Kolom | Tipe | Keterangan |
|-------|------|------------|
| `id` | String | PK (UUID v4) |
| `nomorBa` | String | Nomor Berita Acara (BA) |
| `tanggalEksekusi` | String | Tanggal pemusnahan fisik |
| `penanggungJawab` | String | Petugas penanggung jawab |
| `saksi1` | String | Nama saksi pertama |
| `saksi2` | String? | Nama saksi kedua (optional) |
| `keterangan` | String? | Catatan tambahan |
| `createdAt` | Long | Unix timestamp pembuatan |

### `penandatangan` table (PenandatanganEntity)
| Kolom | Tipe | Keterangan |
|-------|------|------------|
| `id` | String | PK (UUID v4) |
| `beritaAcaraId` | String | FK ke berita_acara (Cascade) |
| `nama` | String | Nama penandatangan |
| `jabatan` | String | Jabatan dinas |
| `role` | String | Role (PENANGGUNG_JAWAB, SAKSI_1, SAKSI_2) |
| `urutan` | Int | Urutan tanda tangan |

### `audit_logs` table (AuditLogEntity)
| Kolom | Tipe | Keterangan |
|-------|------|------------|
| `id` | String | PK (UUID v4) |
| `action` | String | Jenis aksi (CREATE_PROPOSAL, UPDATE_STATUS, etc) |
| `actorId` | String | Username pelaku aksi |
| `archiveId` | String? | Referensi arsip (optional) |
| `proposalId` | String? | Referensi berkas usul (optional) |
| `beritaAcaraId` | String? | Referensi berita acara (optional) |
| `previousStatus` | String? | Status sebelum aksi (optional) |
| `newStatus` | String? | Status sesudah aksi (optional) |
| `notes` | String? | Keterangan detail log |
| `timestamp` | Long | Unix timestamp kejadian |

---

## 8. Database Schema Target (Supabase / PostgreSQL)

### Status Arsip (state machine wajib):
```
AVAILABLE → PROPOSED → VERIFIED → APPROVED → DISPOSED
```
Tidak boleh skip state. Tidak boleh HARD DELETE.

### Tabel utama (dari Agent.md):
```sql
CREATE TABLE berita_acara_pemusnahan (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nomor_ba VARCHAR(100) UNIQUE NOT NULL,
    tanggal_eksekusi DATE NOT NULL,
    penanggung_jawab VARCHAR(150) NOT NULL,
    saksi_1 VARCHAR(150) NOT NULL,
    saksi_2 VARCHAR(150),
    keterangan TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
```

### Payload Dispose Archive:
```kotlin
@Serializable
data class DisposeArchivePayload(
    val status: String = "DISPOSED",
    @SerialName("berita_acara_id") val beritaAcaraId: String,
    @SerialName("disposed_at") val disposedAt: String
)
```

---

## 9. Status Fitur Saat Ini

| Fitur | Status | Catatan |
|-------|--------|---------|
| Login Screen | ✅ Selesai | Terhubung ke LoginViewModel & StateFlow (ditambahkan fungsionalitas 'Remember Me' auto-login dengan persistensi SharedPreferences) |
| Dashboard | ✅ UI selesai | Data dummy |
| DaftarArsip Screen | ✅ Selesai | Terhubung ke DaftarArsipViewModel & Room DB (ditambahkan filter otomatis berdasarkan beritaAcaraId di level ViewModel dengan UI banner filter interaktif) |
| DaftarUsulMusnah | ✅ Selesai | Terhubung ke DaftarUsulMusnahViewModel & Room DB (ditambahkan fitur Export Excel .xlsx via SAF untuk berkas APPROVED & DISPOSED) |
| BuatBerkasUsulMusnah | ✅ Selesai | Terhubung ke BuatBerkasUsulMusnahViewModel, Room DB & transaksi creation proposal |
| DetailBerkasUsulMusnah | ✅ UI selesai | Menggunakan domain model Arsip (siap dihubungkan) |
| StatusTracking | ✅ Selesai | Terhubung ke StatusTrackingViewModel & Room DB (dialog diperbarui dengan warna latar belakang putih bersih & teks kontras tinggi) |
| BeritaAcara | ✅ Selesai | Terhubung ke BeritaAcaraViewModel & Room DB (dialog pembuatan BA baru diperbarui dengan warna latar belakang putih & teks kontras tinggi) |
| DetailBeritaAcara | ✅ Selesai | Terhubung ke DetailBeritaAcaraViewModel & Room DB (ditambahkan Kartu Dasar Hukum Pemusnahan & fitur Ekspor PDF resmi 2 halaman via SAF) |
| LogRiwayat | ✅ Selesai | Terhubung ke LogRiwayatViewModel & Room DB |
| Profil | ✅ Selesai | Menggunakan UI asli dari teman & terintegrasi penuh ke NavGraph (ditambahkan konfirmasi keluar) |
| Auth ViewModel | ✅ Selesai | Mengelola State & terhubung ke UseCase |
| Supabase Integration | 🔲 Belum ada | Dependency belum ditambahkan |
| State Machine Logic | ✅ Selesai | Validasi state di UseCase (UpdateStatus & CreateBA) |
| Audit Log | ✅ Selesai | Tabel Room audit_logs + pencatatan log fleksibel |

---

## 10. Data Dummy / Placeholder

Semua screen pemusnahan menggunakan data dummy (hardcoded lists):
- `dummyArsipList` → DaftarArsipScreen
- `dummyBerkasDetail` → DetailBerkasUsulMusnahScreen (referenced di NavGraph)

Data dummy di-declare di dalam file screen masing-masing.

---

## 11. Komponen Reusable

| Komponen | File | Keterangan |
|----------|------|------------|
| `PemusnahanDrawerContent` | SideDrawer.kt | Side drawer navigasi semua screen pemusnahan |
| `DrawerRoutes` | SideDrawer.kt | Konstanta string route untuk drawer |
| `QuickFilterPill` | DaftararsipScreen.kt | Chip filter (modul/tahun) |
| `ExcelTable` | DaftararsipScreen.kt | Tabel horizontal scroll dengan sticky header |
| `ExcelCell` | DaftararsipScreen.kt | Cell renderer per kolom |
| `ColumnSelectorSheet` | DaftararsipScreen.kt | Modal sheet toggle kolom tabel |
| `FilterAdvancedSheet` | DaftararsipScreen.kt | Modal sheet filter lanjutan |
| `LoginField` | LoginScreen.kt | TextField reusable untuk form login |
| `LoginTopBar` | LoginScreen.kt | AppBar login |

---

## 12. Golden Rules (dari Agent.md — WAJIB DITERAPKAN)

1. **NEVER hard-delete** arsip dari database → gunakan soft delete (`status = DISPOSED`)
2. **10-tahun retention**: hanya arsip dengan `document_year + 10 <= current_year` yang boleh diusulkan musnah
3. **Berita Acara wajib ada** sebelum status DISPOSED bisa dibuat
4. **PROPOSED = frozen**: tidak bisa edit, pinjam, pindah box, atau diusulkan lagi
5. **State Machine ketat**: AVAILABLE → PROPOSED → VERIFIED → APPROVED → DISPOSED (tidak boleh skip)
6. **Setiap transisi status** harus menghasilkan audit log (actor_id, archive_id, previous_status, new_status, timestamp)
7. **UUID Safety**: Room ID ≠ Supabase UUID; selalu pakai UUID v4 untuk FK ke remote
8. **No hardcoded string** pemerintahan → masuk `res/values/strings.xml`
9. **State hoisting**: screen terima `uiState` + emit `onEvent()`
10. **Dispatchers.IO** untuk semua operasi DB

---

## 13. To-Do Teknis Berikutnya (Prioritas)

1. **Tambah dependency Supabase** ke `build.gradle.kts`
2. **Replace dummy data** dengan Room Flow di setiap screen
3. **Tambah strings.xml** untuk terminologi pemerintahan

---

## 14. Cara Jalankan / Build

```bash
# Run di Android device/emulator
./gradlew assembleDebug

# Atau via Android Studio: Run > Run 'app'
```

Tidak ada konfigurasi environment khusus yang diperlukan saat ini.

---

*Terakhir diperbarui: 2026-06-26 oleh AI scan otomatis*
