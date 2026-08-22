package com.bpkpad.arsip.presentation.staging

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bpkpad.arsip.core.domain.model.StagingDocument
import com.bpkpad.arsip.presentation.components.BpkpadButton
import com.bpkpad.arsip.presentation.components.BpkpadTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StagingScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToInput: () -> Unit,
    onNavigateToScan: () -> Unit,
    viewModel: StagingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var isFabExpanded by remember { mutableStateOf(false) }

    val excelPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let { viewModel.importFromExcel(context, it) }
        }
    )

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Staging Draf Dokumen Non-Keuangan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading) {
                Column(horizontalAlignment = Alignment.End) {
                    AnimatedVisibility(
                        visible = isFabExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            SmallFloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    onNavigateToInput()
                                },
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Form Manual")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.EditNote, contentDescription = null)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            SmallFloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    onNavigateToScan()
                                },
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Scan Document")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.DocumentScanner, contentDescription = null)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            SmallFloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    excelPickerLauncher.launch(
                                        arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                                    )
                                },
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Import Excel")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.FileUpload, contentDescription = null)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                    FloatingActionButton(
                        onClick = { isFabExpanded = !isFabExpanded },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            if (isFabExpanded) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = "Expand Options"
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Penetapan Lokasi Penyimpanan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    BpkpadTextField(
                        value = uiState.boxId,
                        onValueChange = viewModel::onBoxIdChange,
                        label = "Box ID (Contoh: BOX-NK-01)"
                    )
                    BpkpadTextField(
                        value = uiState.locationId,
                        onValueChange = viewModel::onLocationIdChange,
                        label = "Location ID (Contoh: RAK-A-01)"
                    )
                }
            }

            Text(
                text = "Draf Dokumen dalam Sesi (${uiState.documents.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(uiState.documents) { doc ->
                    StagingItem(
                        document = doc,
                        onEdit = { onNavigateToEdit(doc.id) },
                        onDelete = { viewModel.deleteDocument(doc.id) }
                    )
                }
            }

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "Unknown Error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            BpkpadButton(
                text = "Simpan Semua ke Arsip Utama",
                onClick = viewModel::pushToCloud,
                enabled = !uiState.isLoading && uiState.documents.isNotEmpty()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StagingItem(
    document: StagingDocument,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = document.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "${document.type} • Tahun ${document.year}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
