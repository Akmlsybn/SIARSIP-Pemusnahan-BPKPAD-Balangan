package com.bpkpad.siarsip.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bpkpad.siarsip.feature.auth.presentation.ProfilViewModel
import com.bpkpad.siarsip.feature.auth.presentation.UserProfileState
import com.bpkpad.siarsip.ui.components.DrawerRoutes
import com.bpkpad.siarsip.ui.components.PemusnahanBottomBar
import com.bpkpad.siarsip.ui.theme.*

// ─────────────────────────────────────────────────────────────
//  Screen Utama
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: ProfilViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showGuideDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // State untuk Dialog Password
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val user = userProfile ?: UserProfileState(
        username = "admin",
        namaPegawai = "Administrator SIARSIP",
        nip = "198904122015031002",
        jabatan = "Arsiparis Ahli Pertama - BPKPAD Balangan"
    )

    // Dialog Konfirmasi Keluar
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Keluar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextHead
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin keluar dari aplikasi?",
                    fontSize = 14.sp,
                    color = TextBody
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogout()
                    }
                ) {
                    Text(text = "Keluar", color = DangerText, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(text = "Batal", color = TextHint)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Dialog Ubah Kata Sandi
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { 
                showPasswordDialog = false
                oldPassword = ""
                newPassword = ""
                confirmPassword = ""
            },
            title = {
                Text(
                    text = "Ubah Kata Sandi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextHead
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                ) {
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Kata Sandi Lama", fontSize = 12.sp) },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = null,
                                    tint = TextHint
                                )
                            }
                        },
                        colors = arsipFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Kata Sandi Baru", fontSize = 12.sp) },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = arsipFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Konfirmasi Kata Sandi Baru", fontSize = 12.sp) },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = arsipFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                            Toast.makeText(context, "Semua bidang wajib diisi!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (newPassword != confirmPassword) {
                            Toast.makeText(context, "Konfirmasi kata sandi baru tidak cocok!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.changePassword(oldPassword, newPassword) { result ->
                            result.onSuccess {
                                Toast.makeText(context, "Kata sandi berhasil diubah!", Toast.LENGTH_SHORT).show()
                                showPasswordDialog = false
                                oldPassword = ""
                                newPassword = ""
                                confirmPassword = ""
                            }.onFailure {
                                Toast.makeText(context, it.message ?: "Gagal mengubah kata sandi", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                ) {
                    Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showPasswordDialog = false
                        oldPassword = ""
                        newPassword = ""
                        confirmPassword = ""
                    }
                ) {
                    Text("Batal", color = TextHint)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Dialog Panduan Penggunaan
    if (showGuideDialog) {
        AlertDialog(
            onDismissRequest = { showGuideDialog = false },
            title = {
                Text(
                    text = "Panduan Penggunaan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextHead
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = "1. Kelola Arsip:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GreenPrimary
                    )
                    Text(
                        text = "Gunakan tab 'Arsip' untuk memantau data kearsipan. Filter berdasarkan modul atau tahun arsip.",
                        fontSize = 12.sp,
                        color = TextBody
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = BorderGray)

                    Text(
                        text = "2. Usul Pemusnahan:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GreenPrimary
                    )
                    Text(
                        text = "Masuk ke menu 'Usul Musnah' -> Klik tombol '[+] Buat Berkas' untuk mengajukan penghapusan berkas arsip yang telah melewati jangka retensi.",
                        fontSize = 12.sp,
                        color = TextBody
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = BorderGray)

                    Text(
                        text = "3. Cetak Berita Acara:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GreenPrimary
                    )
                    Text(
                        text = "Saat berkas usulan disetujui (APPROVED), buat Berita Acara Pemusnahan di menu 'Berita Acara', tentukan penandatangan, lalu unduh dokumen PDF resmi beserta lampiran tabel arsip via Excel.",
                        fontSize = 12.sp,
                        color = TextBody
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showGuideDialog = false }) {
                    Text("Tutup", color = GreenPrimary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Dialog Tentang Aplikasi
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(
                    text = "Tentang Aplikasi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextHead
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(GreenLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SA",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                    }
                    Text(
                        text = "SIARSIP",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextHead
                    )
                    Text(
                        text = "v1.0.0",
                        fontSize = 12.sp,
                        color = TextHint
                    )
                    Text(
                        text = "Sistem Informasi Arsip Usul Musnah\nBPKPAD Kabupaten Balangan",
                        fontSize = 13.sp,
                        color = TextBody,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Tutup", color = GreenPrimary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        containerColor = BgDashboard,
        topBar = { ProfileTopBar() },
        bottomBar = {
            PemusnahanBottomBar(
                currentRoute = DrawerRoutes.PROFIL,
                onNavigate   = onNavigate
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            // ── Kartu Identitas Pegawai (Header) ─────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                border = BorderStroke(1.dp, BorderGray),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(GreenLight)
                            .border(3.dp, GreenPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = user.namaPegawai,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextHead,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "NIP. ${user.nip}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextHint
                    )

                    Spacer(Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .background(GreenLight, RoundedCornerShape(99.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = user.jabatan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Section Keamanan Akun ─────────────────────────
            Text(
                text = "Keamanan Akun",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextHint,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            ProfileMenuItem(
                icon = Icons.Filled.Lock,
                iconBg = BlueBg,
                iconTint = BlueText,
                title = "Ubah Kata Sandi",
                subtitle = "Ganti kredensial akses akun Anda",
                onClick = { showPasswordDialog = true }
            )

            Spacer(Modifier.height(20.dp))

            // ── Section Bantuan & Informasi ───────────────────
            Text(
                text = "Bantuan & Informasi",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextHint,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                iconBg = GreenLight,
                iconTint = GreenPrimary,
                title = "Panduan Penggunaan",
                subtitle = "Cara penggunaan sistem pemusnahan arsip",
                onClick = { showGuideDialog = true }
            )

            Spacer(Modifier.height(10.dp))

            ProfileMenuItem(
                icon = Icons.Filled.Info,
                iconBg = BlueBg,
                iconTint = BlueText,
                title = "Tentang Aplikasi",
                subtitle = "SIARSIP v1.0.0 - BPKPAD Kab. Balangan",
                onClick = { showAboutDialog = true }
            )

            Spacer(Modifier.height(28.dp))

            // ── Tombol Keluar (Logout) ─────────────────────────
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp),
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = "Keluar dari Aplikasi",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Top Bar
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(GreenPrimary, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("S", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Text(
                    "SIARSIP Balangan",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = GreenPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CardWhite
        )
    )
    HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
}

// ─────────────────────────────────────────────────────────────
//  Profile Menu Item
// ─────────────────────────────────────────────────────────────
@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    titleColor: Color = TextHead,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            ),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint     = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = titleColor
                )
                Text(
                    subtitle,
                    fontSize   = 11.sp,
                    color      = TextHint,
                    lineHeight = 15.sp,
                    modifier   = Modifier.padding(top = 2.dp)
                )
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint     = TextHint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun arsipFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = GreenPrimary,
    unfocusedBorderColor    = BorderGray,
    focusedContainerColor   = CardWhite,
    unfocusedContainerColor = CardWhite,
    focusedTextColor        = TextHead,
    unfocusedTextColor      = TextHead,
    cursorColor             = GreenPrimary
)

// ─────────────────────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}