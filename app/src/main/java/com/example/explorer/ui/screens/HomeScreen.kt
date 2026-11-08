package com.example.explorer.ui.screens

import android.os.Environment
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.explorer.domain.model.FileCategory
import com.example.explorer.domain.model.FileItem
import com.example.explorer.domain.model.StorageInfo
import com.example.explorer.ui.components.FileListItem
import com.example.explorer.ui.components.RecentMediaCard
import com.example.explorer.ui.theme.*

@Composable
fun HomeScreen(
    storageInfo: StorageInfo?,
    recentFiles: List<FileItem>,
    onMenuClick: () -> Unit = {},
    onCategoryClick: (FileCategory) -> Unit,
    onFileClick: (FileItem) -> Unit,
    onOpenStorageClick: () -> Unit,
    onShortcutClick: (path: String) -> Unit,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSafeFolderClick: () -> Unit,
    onTrashClick: () -> Unit,
    onAppsClick: () -> Unit = {},
    onInternalStorageClick: () -> Unit,
    onMoreClick: (FileItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val rootPath = Environment.getExternalStorageDirectory().absolutePath

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Barra Superior estilo EX Original con pestaña "Hogar"
        item {
            Spacer(modifier = Modifier.height(4.dp))
            ExOriginalTopBar(
                onMenuClick = onMenuClick,
                onSearchClick = onSearchClick,
                onCleanClick = onOpenStorageClick
            )
        }

        // 2. Tarjetas Duales Superiores (Internal Storage + Analizador de archivos)
        item {
            ExDualStorageCards(
                storageInfo = storageInfo,
                onStorageClick = onInternalStorageClick,
                onAnalyzeClick = onOpenStorageClick
            )
        }

        // 3. Cuadrícula Icónica de 12 Categorías (Estilo EX File Explorer con Squircles Sólidos)
        item {
            ExIconsCategoryGrid(
                onCategoryClick = onCategoryClick,
                onAppsClick = onAppsClick,
                onDownloadsClick = { onShortcutClick("$rootPath/Download") },
                onCleanClick = onOpenStorageClick,
                onSafeClick = onSafeFolderClick,
                onFavoritesClick = onFavoritesClick,
                onTrashClick = onTrashClick,
                onStorageClick = onInternalStorageClick
            )
        }

        // 4. Archivos Recientes (Carrusel Horizontal)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Archivos Recientes",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (recentFiles.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No hay archivos recientes aún",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(recentFiles.take(8)) { item ->
                        RecentMediaCard(
                            item = item,
                            onClick = { onFileClick(item) }
                        )
                    }
                }
            }
        }

        // 5. Lista vertical complementaria si hay más recientes
        if (recentFiles.size > 8) {
            item {
                Text(
                    text = "Más Recientes",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(recentFiles.drop(8)) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                ) {
                    FileListItem(
                        item = item,
                        isSelected = false,
                        isSelectionMode = false,
                        onClick = { onFileClick(item) },
                        onLongClick = {},
                        onMoreClick = { onMoreClick(item) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * Barra Superior estilo EX Original con pestaña "Hogar"
 */
@Composable
fun ExOriginalTopBar(
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCleanClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menú lateral",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Pestaña "Hogar" redondeada estilo EX
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.height(36.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Hogar",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Acciones: Corona PRO, Buscar, Opciones
        IconButton(onClick = onCleanClick) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "PRO",
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(22.dp)
            )
        }

        IconButton(onClick = onSearchClick) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = onCleanClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Más",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Tarjetas Duales Superiores (Internal Storage + Analizador de archivos)
 */
@Composable
fun ExDualStorageCards(
    storageInfo: StorageInfo?,
    onStorageClick: () -> Unit,
    onAnalyzeClick: () -> Unit
) {
    val usedPercentage = ((storageInfo?.usedPercentage ?: 0f) * 100).toInt()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Tarjeta 1: Internal Storage con medidor circular
        Card(
            modifier = Modifier
                .weight(1f)
                .height(92.dp)
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onStorageClick),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Medidor circular de porcentaje
                Box(
                    modifier = Modifier.size(46.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { storageInfo?.usedPercentage ?: 0f },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 4.dp,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = "$usedPercentage%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Internal Storage",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "${storageInfo?.formattedUsed ?: "0 B"} / ${storageInfo?.formattedTotal ?: "0 B"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.5.sp
                        ),
                        color = Color(0xFFFF7043)
                    )
                }
            }
        }

        // Tarjeta 2: Analizador de archivos
        Card(
            modifier = Modifier
                .weight(1f)
                .height(92.dp)
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onAnalyzeClick),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono tipo Pie Chart
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF29B6F6).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PieChart,
                        contentDescription = null,
                        tint = Color(0xFF0288D1),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Analizador",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Archivos para limpiar",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Cuadrícula de 12 Iconos estilo EX File Explorer (Squircles Sólidos con iconos blancos)
 */
@Composable
fun ExIconsCategoryGrid(
    onCategoryClick: (FileCategory) -> Unit,
    onAppsClick: () -> Unit,
    onDownloadsClick: () -> Unit,
    onCleanClick: () -> Unit,
    onSafeClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onTrashClick: () -> Unit,
    onStorageClick: () -> Unit
) {
    data class ExIconItem(
        val title: String,
        val icon: ImageVector,
        val color: Color,
        val onClick: () -> Unit
    )

    val items = listOf(
        // Fila 1
        ExIconItem("Imágenes", Icons.Default.Image, Color(0xFFEF5350)) { onCategoryClick(FileCategory.IMAGE) },
        ExIconItem("Videos", Icons.Default.Videocam, Color(0xFF29B6F6)) { onCategoryClick(FileCategory.VIDEO) },
        ExIconItem("Audio", Icons.Default.Headphones, Color(0xFFAB47BC)) { onCategoryClick(FileCategory.AUDIO) },
        ExIconItem("APP", Icons.Default.Android, Color(0xFF66BB6A), onAppsClick),

        // Fila 2
        ExIconItem("Limpiador", Icons.Default.CleaningServices, Color(0xFF26A69A), onCleanClick),
        ExIconItem("Documentos", Icons.Default.Description, Color(0xFF1E88E5)) { onCategoryClick(FileCategory.DOCUMENT) },
        ExIconItem("Descargas", Icons.Default.Download, Color(0xFF00ACC1), onDownloadsClick),
        ExIconItem("Nuevos", Icons.Default.Schedule, Color(0xFFFFA726)) { onCategoryClick(FileCategory.DOCUMENT) },

        // Fila 3
        ExIconItem("Comprimido", Icons.Default.FolderZip, Color(0xFF42A5F5)) { onCategoryClick(FileCategory.ARCHIVE) },
        ExIconItem("Carpeta segura", Icons.Default.Lock, Color(0xFF5C6BC0), onSafeClick),
        ExIconItem("Favoritos", Icons.Default.Star, Color(0xFFFFCA28), onFavoritesClick),
        ExIconItem("Papelera", Icons.Default.Delete, Color(0xFF78909C), onTrashClick)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        for (rowIndex in 0 until 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (colIndex in 0 until 4) {
                    val index = rowIndex * 4 + colIndex
                    val item = items[index]
                    ExSquircleGridItem(
                        title = item.title,
                        icon = item.icon,
                        backgroundColor = item.color,
                        onClick = item.onClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ExSquircleGridItem(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Squircle sólido estilo EX
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 11.5.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
