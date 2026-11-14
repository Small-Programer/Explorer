package com.example.explorer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.explorer.domain.model.FileItem

/**
 * Modos de vista y tamaño de iconos estilo Explorador de Windows.
 */
enum class FileViewMode(
    val label: String,
    val isGrid: Boolean,
    val minCellDp: Int,
    val thumbnailDp: Int,
    val icon: ImageVector
) {
    EXTRA_LARGE("Iconos muy grandes", true, 270, 180, Icons.Default.PhotoSizeSelectLarge),
    LARGE("Iconos grandes", true, 190, 120, Icons.Default.GridView),
    MEDIUM("Iconos medianos", true, 130, 80, Icons.Default.Apps),
    LIST("Lista compacta", false, 0, 38, Icons.AutoMirrored.Filled.FormatListBulleted),
    DETAILS("Detalles", false, 0, 48, Icons.AutoMirrored.Filled.ViewList)
}

/**
 * Botón con menú desplegable para elegir tamaño de carpetas, fotos y archivos (estilo Windows).
 */
@Composable
fun FileViewModeMenuButton(
    currentMode: FileViewMode,
    onSelectMode: (FileViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = currentMode.icon,
                contentDescription = "Tamaño y vista",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Text(
                text = "Vista y tamaño",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

            FileViewMode.entries.forEach { mode ->
                val isSelected = mode == currentMode
                DropdownMenuItem(
                    text = {
                        Text(
                            text = mode.label,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = mode.icon,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        onSelectMode(mode)
                    }
                )
            }
        }
    }
}

/**
 * Pantalla de carga limpia en cristal para carpetas y categorías.
 */
@Composable
fun GlassLoadingView(
    message: String,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            isDark = isDark,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    strokeWidth = 3.5.dp,
                    color = NeonCyanSoft,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .width(140.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50)),
                    color = NeonCyanSoft,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}

/**
 * Renderizador compartido de archivos/carpetas/fotos según el tamaño y modo de vista elegido.
 */
@Composable
fun FileItemsBrowser(
    files: List<FileItem>,
    viewMode: FileViewMode,
    selectedFiles: Set<FileItem>,
    isSelectionMode: Boolean,
    onFileClick: (FileItem) -> Unit,
    onFileLongClick: (FileItem) -> Unit,
    onMoreClick: (FileItem) -> Unit,
    contentPaddingBottom: Dp = 88.dp,
    modifier: Modifier = Modifier
) {
    if (viewMode.isGrid) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = viewMode.minCellDp.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = contentPaddingBottom),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(files, key = { it.path }) { item ->
                val isSelected = selectedFiles.contains(item)
                FileGridItem(
                    item = item,
                    isSelected = isSelected,
                    isSelectionMode = isSelectionMode,
                    thumbnailSize = viewMode.thumbnailDp.dp,
                    onClick = {
                        if (isSelectionMode) {
                            onFileLongClick(item)
                        } else {
                            onFileClick(item)
                        }
                    },
                    onLongClick = { onFileLongClick(item) }
                )
            }
        }
    } else {
        val showDetails = viewMode == FileViewMode.DETAILS
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = contentPaddingBottom)
        ) {
            items(files, key = { it.path }) { item ->
                val isSelected = selectedFiles.contains(item)
                FileListItem(
                    item = item,
                    isSelected = isSelected,
                    isSelectionMode = isSelectionMode,
                    iconSize = viewMode.thumbnailDp.dp,
                    showDetails = showDetails,
                    onClick = {
                        if (isSelectionMode) {
                            onFileLongClick(item)
                        } else {
                            onFileClick(item)
                        }
                    },
                    onLongClick = { onFileLongClick(item) },
                    onMoreClick = { onMoreClick(item) }
                )
            }
        }
    }
}
