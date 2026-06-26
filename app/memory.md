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
    │   │   ├── AppDatabase.kt        ← Room DB (v1, entity: UserEntity)
    │   │   ├── DatabaseCallback.kt   ← Seeder: insert user "admin" (SHA-256)
    │   │   └── dao/UserDao.kt        ← insertUser, findByUsername, countUsers
    │   │   └── entity/UserEntity.kt  ← @Entity("users"): id, username, passwordHash
    │   ├── di/
    │   │   ├── DatabaseModule.kt     ← @Singleton AppDatabase + UserDao
    │   │   └── AuthModule.kt         ← @Binds AuthRepository → AuthRepositoryImpl
    │   └── navigation/
    │       ├── Screen.kt             ← sealed class semua routes
    │       ├── SiArsipNavGraph.kt    ← NavHost + semua composable route
    │       └── PlaceholderScreen.kt  ← Sementara: Profil & Pengaturan
    │
    ├── feature/auth/
    │   ├── data/
    │   │   ├── mapper/UserMapper.kt          ← UserEntity.toDomain()
    │   │   └── repository/AuthRepositoryImpl.kt  ← login via Room (plaintext pw check)
    │   └── domain/
    │       ├── model/User.kt                 ← data class User(id, username)
    │       ├── repository/AuthRepository.kt  ← interface: suspend login()
    │       └── usecase/LoginUseCase.kt       ← validasi + call repo.login()
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

> ⚠️ Catatan: `AuthRepositoryImpl` saat ini membandingkan `passwordHash` langsung (bukan hash),  
> perlu fix: hash input password sebelum dibandingkan.

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
| Login Screen | ✅ UI selesai | Belum ada ViewModel/state hoisting |
| Dashboard | ✅ UI selesai | Data dummy |
| DaftarArsip Screen | ✅ UI selesai | Data dummy, tabel Excel + filter + kolom toggle |
| DaftarUsulMusnah | ✅ UI selesai | Data dummy |
| BuatBerkasUsulMusnah | ✅ UI selesai | Form lengkap, belum terhubung DB |
| DetailBerkasUsulMusnah | ✅ UI selesai | Data dummy |
| StatusTracking | ✅ UI selesai | Timeline dummy |
| BeritaAcara | ✅ UI selesai | Data dummy |
| DetailBeritaAcara | ✅ UI selesai | Data dummy |
| LogRiwayat | ✅ UI selesai | Data dummy, immutable |
| Profil | 🔲 Placeholder | Belum ada screen asli |
| Pengaturan | 🔲 Placeholder | Belum ada screen asli |
| Auth ViewModel | 🔲 Belum ada | LoginScreen belum pakai ViewModel |
| Supabase Integration | 🔲 Belum ada | Dependency belum ditambahkan |
| State Machine Logic | 🔲 Belum ada | UseCase belum dibuat |
| Audit Log | 🔲 Belum ada | Butuh tabel + repo |

---

## 10. Data Dummy / Placeholder

Semua screen pemusnahan menggunakan data dummy (hardcoded lists):
- `dummyArsipList` → DaftarArsipScreen
- `dummyBerkasDetail` → DetailBerkasUsulMusnahScreen (referenced di NavGraph)
- `dummyBeritaAcaraList` → BeritaAcaraScreen (referenced di NavGraph)

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

1. **Buat ViewModel + StateFlow** untuk LoginScreen (connect ke LoginUseCase yang sudah ada)
2. **Fix bug auth**: hash input password sebelum dibandingkan di `AuthRepositoryImpl`
3. **Tambah dependency Supabase** ke `build.gradle.kts`
4. **Buat entitas Room** untuk: ArsipDocument, BerkasUsulMusnah, BeritaAcara, AuditLog
5. **Implement UseCase**: GetEligibleDisposalArchivesUseCase, ProposeArchiveUseCase, dll.
6. **Replace dummy data** dengan Room Flow di setiap screen
7. **Buat screen Profil** & **Pengaturan** menggantikan PlaceholderScreen
8. **Tambah strings.xml** untuk terminologi pemerintahan

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
