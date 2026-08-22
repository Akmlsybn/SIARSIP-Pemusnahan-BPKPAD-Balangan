package com.example.arsipbpkpad.presentation.storage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bpkpad.siarsip.R
import com.example.arsipbpkpad.domain.model.BoxDetails
import com.example.arsipbpkpad.domain.model.Room
import com.example.arsipbpkpad.domain.model.Shelf
import com.example.arsipbpkpad.domain.model.UserRole
import com.example.arsipbpkpad.presentation.components.BottomNavItem
import com.example.arsipbpkpad.presentation.components.BpkpadBottomNavigation
import com.example.arsipbpkpad.presentation.components.BpkpadLogoScreenTopAppBar
import com.example.arsipbpkpad.ui.theme.ArsipBPKPADTheme
import com.example.arsipbpkpad.utils.ResultState

@Composable
fun BoxManagementScreen(
    userRole: UserRole,
    onNavigateToBottomNav: (BottomNavItem) -> Unit,
    viewModel: BoxManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    BoxManagementContent(
        uiState = uiState,
        userRole = userRole,
        onNavigateToBottomNav = onNavigateToBottomNav,
        onFilterRoomSelected = { viewModel.setFilterRoom(it) },
        onFilterShelfSelected = { viewModel.setFilterShelf(it) },
        onResetFilters = { viewModel.resetFilters() },
        onClearErrors = { viewModel.clearErrors() }
    )
}

@Composable
fun BoxManagementContent(
    uiState: BoxManagementUiState,
    userRole: UserRole,
    onNavigateToBottomNav: (BottomNavItem) -> Unit,
    onFilterRoomSelected: (Room) -> Unit,
    onFilterShelfSelected: (Shelf) -> Unit,
    onResetFilters: () -> Unit,
    onClearErrors: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedBoxForView by remember { mutableStateOf<BoxDetails?>(null) }
    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            BpkpadLogoScreenTopAppBar(
                titleText = stringResource(R.string.title_location_management),
                onNavigationClick = { onNavigateToBottomNav(BottomNavItem.HOME) }
            )
        },
        bottomBar = {
            BpkpadBottomNavigation(
                currentRoute = BottomNavItem.STORAGE.route,
                userRole = userRole,
                onNavigate = onNavigateToBottomNav
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Top Section (Filters)
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.title_location_filter),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        
                        if (uiState.selectedFilterRoom != null || uiState.selectedFilterShelf != null) {
                            TextButton(
                                onClick = onResetFilters,
                                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(
                                    text = "Reset",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ReadOnlyLocationDropdown(
                        label = stringResource(R.string.label_select_warehouse),
                        value = uiState.selectedFilterRoom?.name ?: "",
                        placeholder = "Pilih Gudang",
                        options = uiState.rooms,
                        onOptionSelected = { onFilterRoomSelected(it) },
                        getItemName = { it.name },
                        leadingIcon = Icons.Default.Warehouse
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ReadOnlyLocationDropdown(
                        label = stringResource(R.string.label_select_rack),
                        value = uiState.selectedFilterShelf?.name ?: "",
                        placeholder = "Pilih Rak",
                        options = uiState.filterShelves,
                        onOptionSelected = { onFilterShelfSelected(it) },
                        getItemName = { it.name },
                        enabled = uiState.selectedFilterRoom != null,
                        leadingIcon = Icons.Default.Inventory2,
                        helperText = if (uiState.selectedFilterRoom == null) "Pilih gudang terlebih dahulu" else null
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize().weight(1f).background(MaterialTheme.colorScheme.surface)) {
                when (val boxesState = uiState.boxes) {
                    is ResultState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = primaryColor)
                    is ResultState.Success -> {
                        if (boxesState.data.isEmpty()) {
                            EmptyBoxState(
                                text = stringResource(R.string.msg_no_box_in_rack),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    top = 16.dp,
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 88.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(boxesState.data) { box ->
                                    BoxCard(
                                        box = box,
                                        onClick = { 
                                            selectedBoxForView = box
                                            showDialog = true 
                                        }
                                    )
                                }
                            }
                        }
                    }
                    is ResultState.Idle -> {
                        EmptyBoxState(
                            text = stringResource(R.string.msg_select_location_hint),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is ResultState.Error -> Text(
                        text = boxesState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
            }
        }
    }

    if (showDialog && selectedBoxForView != null) {
        BoxDetailDialog(
            box = selectedBoxForView!!,
            onDismiss = { 
                showDialog = false
                onClearErrors()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ReadOnlyLocationDropdown(
    label: String,
    value: String,
    placeholder: String,
    options: List<T>,
    onOptionSelected: (T) -> Unit,
    getItemName: (T) -> String,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
    helperText: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded && enabled,
            onExpandedChange = { if (enabled) expanded = !expanded }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                placeholder = {
                    Text(
                        text = placeholder
                    )
                },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                enabled = enabled,
                leadingIcon = leadingIcon?.let {
                    { 
                        Icon(
                            imageVector = it, 
                            contentDescription = null, 
                            tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        ) 
                    }
                },
                trailingIcon = { 
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.12f),
                    
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    disabledLabelColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    disabledBorderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                    
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledTextColor = MaterialTheme.colorScheme.onPrimary,
                    
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            )

            ExposedDropdownMenu(
                expanded = expanded && enabled,
                onDismissRequest = { expanded = false },
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        leadingIcon = leadingIcon?.let {
                            { Icon(it, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary) }
                        },
                        text = { 
                            Text(
                                text = getItemName(option), 
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge
                            ) 
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
        
        if (helperText != null) {
            Text(
                text = helperText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun BoxCard(box: BoxDetails, onClick: () -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.label_box_number, box.name),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${box.roomName} • ${box.shelfName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = box.itemCount.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor
                )
                Text(
                    text = "Arsip",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmptyBoxState(text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Inventory,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BoxDetailDialog(
    box: BoxDetails,
    onDismiss: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = null,
                            tint = primaryColor
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.title_box_location_detail), 
                            style = MaterialTheme.typography.labelLarge, 
                            color = primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.label_box_number, box.name),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                // Stats Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = box.itemCount.toString(),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "TOTAL DOKUMEN ARSIP",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                DetailItem(label = stringResource(R.string.label_warehouse), value = box.roomName)
                Spacer(modifier = Modifier.height(12.dp))
                DetailItem(label = stringResource(R.string.label_rack), value = box.shelfName)

                Spacer(modifier = Modifier.height(32.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        containerColor = primaryColor,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.btn_close),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        androidx.compose.material3.HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BoxManagementScreenPreview() {
    val sampleRooms = listOf(
        Room("1", "Gudang A"),
        Room("2", "Gudang B")
    )
    val sampleShelves = listOf(
        Shelf("1", "1", "Rak 01"),
        Shelf("2", "1", "Rak 02")
    )
    val sampleBoxes = listOf(
        BoxDetails("1", "BOX-001", "1", "Rak 01", "1", "Gudang A", 15),
        BoxDetails("2", "BOX-002", "1", "Rak 01", "1", "Gudang A", 8)
    )

    ArsipBPKPADTheme {
        BoxManagementContent(
            uiState = BoxManagementUiState(
                rooms = sampleRooms,
                filterShelves = sampleShelves,
                selectedFilterRoom = sampleRooms[0],
                selectedFilterShelf = sampleShelves[0],
                boxes = ResultState.Success(sampleBoxes)
            ),
            userRole = UserRole.ARSIPARIS,
            onNavigateToBottomNav = {},
            onFilterRoomSelected = {},
            onFilterShelfSelected = {},
            onResetFilters = {},
            onClearErrors = {}
        )
    }
}
