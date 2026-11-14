package com.example.explorer.ui.screens

import android.os.Environment
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.explorer.domain.model.FileCategory
import com.example.explorer.domain.model.FileItem
import com.example.explorer.domain.model.StorageInfo
import com.example.explorer.ui.components.FileListItem
import com.example.explorer.ui.components.GlassAmbientBackground
import com.example.explorer.ui.components.GlassCard
import com.example.explorer.ui.components.NeonCyanSoft
import com.example.explorer.ui.components.RecentMediaCard

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
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    onToggleDarkTheme: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val rootPath = Environment.getExternalStorageDirectory().absolutePath
    val primaryTextColor = if (isDarkTheme) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    val secondaryTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF475569)

    Box(modifier = modifier.fillMaxSize()) {
        // Fondo ambiental suave (Claro pastel / Oscuro aurora)
        GlassAmbientBackground(isDark = isDarkTheme)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Barra Superior flotante de cristal tipo píldora ("Hogar")
            item {
                Spacer(modifier = Modifier.height(6.dp))
                GlassTopBar(
                    isDark = isDarkTheme,
                    primaryTextColor = primaryTextColor,
                    onMenuClick = onMenuClick,
                    onToggleDarkTheme = onToggleDarkTheme,
                    onSearchClick = onSearchClick
                )
            }

            // 2. Tarjeta Hero de Almacenamiento Interno + botón Analizador (Opción B)
            item {
                GlassStorageHeroCard(
                    isDark = isDarkTheme,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    storageInfo = storageInfo,
                    onStorageClick = onInternalStorageClick,
                    onAnalyzeClick = onOpenStorageClick
                )
            }

            // 3. Cuadrícula 3x3 de Botones Grandes en Cristal (Neón solo al seleccionar)
            item {
                GlassCategoryGrid(
                    isDark = isDarkTheme,
                    primaryTextColor = primaryTextColor,
                    onCategoryClick = onCategoryClick,
                    onAppsClick = onAppsClick,
                    onDownloadsClick = { onShortcutClick("$rootPath/Download") },
                    onSafeClick = onSafeFolderClick,
                    onFavoritesClick = onFavoritesClick,
                    onTrashClick = onTrashClick
                )
            }

            // 4. Archivos Recientes en tarjeta de cristal
            item {
                GlassCard(
                    isDark = isDarkTheme,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Archivos Recientes",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.5.sp
                                ),
                                color = primaryTextColor
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (recentFiles.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 18.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = secondaryTextColor.copy(alpha = 0.7f),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "No hay archivos recientes aún",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = secondaryTextColor
                                    )
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
                }
            }

            // 5. Lista vertical complementaria si hay más recientes
            if (recentFiles.size > 8) {
                item {
                    Text(
                        text = "Más Recientes",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = secondaryTextColor
                    )
                }

                items(recentFiles.drop(8)) { item ->
                    GlassCard(
                        isDark = isDarkTheme,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
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
}

/**
 * Barra Superior flotante en forma de píldora de cristal ("Hogar")
 */
@Composable
fun GlassTopBar(
    isDark: Boolean,
    primaryTextColor: Color,
    onMenuClick: () -> Unit,
    onToggleDarkTheme: () -> Unit,
    onSearchClick: () -> Unit
) {
    GlassCard(
        isDark = isDark,
        shape = RoundedCornerShape(28.dp),
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú lateral",
                    tint = primaryTextColor
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Hogar",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = primaryTextColor,
                modifier = Modifier.weight(1f)
            )

            // Acceso rápido Modo Claro / Oscuro
            IconButton(onClick = onToggleDarkTheme) {
                Icon(
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = if (isDark) "Cambiar a modo claro" else "Cambiar a modo oscuro",
                    tint = primaryTextColor.copy(alpha = 0.85f)
                )
            }

            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = primaryTextColor
                )
            }
        }
    }
}

/**
 * Tarjeta Hero unificada de Almacenamiento Interno + botón Analizador (Opción B)
 */
@Composable
fun GlassStorageHeroCard(
    isDark: Boolean,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    storageInfo: StorageInfo?,
    onStorageClick: () -> Unit,
    onAnalyzeClick: () -> Unit
) {
    val usedFloat = (storageInfo?.usedPercentage ?: 0f).coerceIn(0f, 1f)
    val usedPercentage = (usedFloat * 100).toInt()
    val accentRingColor = if (isDark) NeonCyanSoft else Color(0xFF38BDF8)
    val trackColor = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFCBD5E1).copy(alpha = 0.45f)

    GlassCard(
        isDark = isDark,
        shape = RoundedCornerShape(24.dp),
        onClick = onStorageClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Almacenamiento Interno",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.5.sp
                    ),
                    color = primaryTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = storageInfo?.formattedUsed ?: "0 B",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 21.sp
                        ),
                        color = primaryTextColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "/ ${storageInfo?.formattedTotal ?: "0 B"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = secondaryTextColor,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón píldora "Analizador" en cristal
                GlassCard(
                    isDark = isDark,
                    shape = RoundedCornerShape(50),
                    elevation = 2.dp,
                    onClick = onAnalyzeClick
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = null,
                            tint = accentRingColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Analizador",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            ),
                            color = primaryTextColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Medidor circular de porcentaje
            Box(
                modifier = Modifier.size(76.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { usedFloat },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 7.dp,
                    strokeCap = StrokeCap.Round,
                    color = accentRingColor,
                    trackColor = trackColor
                )
                Text(
                    text = "$usedPercentage%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = primaryTextColor
                )
            }
        }
    }
}

/**
 * Cuadrícula 3x3 de Botones Grandes en Cristal (Opción B) con neón solo al seleccionar
 */
@Composable
fun GlassCategoryGrid(
    isDark: Boolean,
    primaryTextColor: Color,
    onCategoryClick: (FileCategory) -> Unit,
    onAppsClick: () -> Unit,
    onDownloadsClick: () -> Unit,
    onSafeClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onTrashClick: () -> Unit
) {
    data class GlassTileItem(
        val title: String,
        val icon: ImageVector,
        val accentTint: Color,
        val onClick: () -> Unit
    )

    var selectedTile by remember { mutableStateOf<String?>(null) }

    val items = listOf(
        // Fila 1
        GlassTileItem("Imágenes", Icons.Default.Image, Color(0xFF0EA5E9)) { onCategoryClick(FileCategory.IMAGE) },
        GlassTileItem("Videos", Icons.Default.OndemandVideo, Color(0xFF8B5CF6)) { onCategoryClick(FileCategory.VIDEO) },
        GlassTileItem("Audio", Icons.Default.MusicNote, Color(0xFF10B981)) { onCategoryClick(FileCategory.AUDIO) },

        // Fila 2
        GlassTileItem("Documentos", Icons.Default.Description, Color(0xFF3B82F6)) { onCategoryClick(FileCategory.DOCUMENT) },
        GlassTileItem("Descargas", Icons.Default.Download, Color(0xFF06B6D4), onDownloadsClick),
        GlassTileItem("Apps", Icons.Default.Apps, Color(0xFF6366F1), onAppsClick),

        // Fila 3
        GlassTileItem("Favoritos", Icons.Default.StarOutline, Color(0xFFF59E0B), onFavoritesClick),
        GlassTileItem("Carpeta Segura", Icons.Default.FolderShared, Color(0xFF64748B), onSafeClick),
        GlassTileItem("Papelera", Icons.Default.DeleteOutline, Color(0xFFEF4444), onTrashClick)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        for (rowIndex in 0 until 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (colIndex in 0 until 3) {
                    val item = items[rowIndex * 3 + colIndex]
                    GlassCategoryTile(
                        title = item.title,
                        icon = item.icon,
                        accentTint = item.accentTint,
                        isDark = isDark,
                        isSelected = selectedTile == item.title,
                        primaryTextColor = primaryTextColor,
                        onClick = {
                            selectedTile = item.title
                            item.onClick()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun GlassCategoryTile(
    title: String,
    icon: ImageVector,
    accentTint: Color,
    isDark: Boolean,
    isSelected: Boolean,
    primaryTextColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        isDark = isDark,
        isSelected = isSelected,
        shape = RoundedCornerShape(22.dp),
        elevation = 4.dp,
        onClick = onClick,
        modifier = modifier.height(106.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        accentTint.copy(alpha = if (isDark) 0.20f else 0.14f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) NeonCyanSoft else if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.5.sp,
                    lineHeight = 14.sp
                ),
                color = primaryTextColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
