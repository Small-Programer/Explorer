package com.example.explorer.ui.screens

import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
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
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
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
        // Barra Superior Estilo Clásico EX File Explorer con Menú Hamburguesa
        item {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1976D2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Explorer",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                    IconButton(onClick = onOpenStorageClick) {
                        Icon(Icons.Default.CleaningServices, contentDescription = "Analizar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }

        // 1. Barra de Búsqueda
        item {
            GoogleFilesSearchBar(onClick = onSearchClick)
        }

        // 2. Tarjeta Principal de Almacenamiento
        item {
            StorageBannerCard(
                storageInfo = storageInfo,
                onAnalyzeClick = onOpenStorageClick
            )
        }

        // 3. Accesos directos a carpetas populares (Descargas, Fotos, Documentos)
        item {
            ShortcutsRow(onShortcutClick = onShortcutClick)
        }

        // 4. Categorías Principales (Cuadrícula 4x2 icónica de EX File Explorer)
        item {
            Text(
                text = "Biblioteca",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            ExCategoriesGrid(
                onCategoryClick = onCategoryClick,
                onAppsClick = onAppsClick,
                onDownloadsClick = { onShortcutClick("$rootPath/Download") },
                onTrashClick = onTrashClick
            )
        }

        // 5. Colecciones (Favoritos, Carpeta Segura, Papelera)
        item {
            Text(
                text = "Colecciones",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    CollectionRowItem(
                        icon = Icons.Default.Star,
                        title = "Favoritos",
                        subtitle = "Archivos destacados",
                        tint = CategoryFolder,
                        onClick = onFavoritesClick
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    CollectionRowItem(
                        icon = Icons.Default.Lock,
                        title = "Carpeta segura",
                        subtitle = "Protegida con PIN",
                        tint = MaterialTheme.colorScheme.primary,
                        onClick = onSafeFolderClick
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    CollectionRowItem(
                        icon = Icons.Default.Delete,
                        title = "Papelera",
                        subtitle = "Archivos eliminados recientemente",
                        tint = MaterialTheme.colorScheme.error,
                        onClick = onTrashClick
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    CollectionRowItem(
                        icon = Icons.Default.Apps,
                        title = "Gestor de aplicaciones",
                        subtitle = "Ver apps y extraer APKs de backup",
                        tint = CategoryApk,
                        onClick = onAppsClick
                    )
                }
            }
        }

        // 6. Dispositivos de almacenamiento
        item {
            Text(
                text = "Dispositivos de almacenamiento",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onInternalStorageClick),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Almacenamiento interno",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "${storageInfo?.formattedFree ?: "0 B"} libres de ${storageInfo?.formattedTotal ?: "0 B"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 7. Archivos Recientes (Carrusel Horizontal)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Archivos Recientes",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (recentFiles.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay archivos recientes aún",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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

        // 6. Lista vertical complementaria de recientes
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
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
            Spacer(modifier = Modifier.height(80.dp)) // Padding para BottomBar
        }
    }
}

/**
 * Barra superior de búsqueda tipo píldora de Google
 */
@Composable
fun GoogleFilesSearchBar(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Buscar en tus archivos...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Tarjeta de almacenamiento con diseño premium
 */
@Composable
fun StorageBannerCard(
    storageInfo: StorageInfo?,
    onAnalyzeClick: () -> Unit
) {
    val usedPercent = ((storageInfo?.usedPercentage ?: 0f) * 100).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SdStorage,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Almacenamiento interno",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "${storageInfo?.formattedFree ?: "0 B"} disponibles",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "$usedPercent%",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de progreso estilizada
            LinearProgressIndicator(
                progress = { storageInfo?.usedPercentage ?: 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${storageInfo?.formattedUsed ?: "0 B"} de ${storageInfo?.formattedTotal ?: "0 B"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TextButton(
                    onClick = onAnalyzeClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Analizar espacio")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

/**
 * Fila de accesos directos rápidos a carpetas clave
 */
@Composable
fun ShortcutsRow(onShortcutClick: (path: String) -> Unit) {
    val root = Environment.getExternalStorageDirectory().absolutePath
    val shortcuts = listOf(
        Pair("Descargas", "$root/Download"),
        Pair("Cámara", "$root/DCIM/Camera"),
        Pair("Documentos", "$root/Documents"),
        Pair("Fotos", "$root/Pictures")
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        shortcuts.forEach { (name, path) ->
            FilterChip(
                selected = false,
                onClick = { onShortcutClick(path) },
                label = { Text(name) },
                leadingIcon = {
                    Icon(
                        imageVector = when (name) {
                            "Descargas" -> Icons.Default.Download
                            "Cámara" -> Icons.Default.PhotoCamera
                            "Documentos" -> Icons.AutoMirrored.Filled.Article
                            else -> Icons.Default.Image
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

/**
 * Cuadrícula clásica de 8 categorías al estilo EX / ES File Explorer (4x2)
 */
@Composable
fun ExCategoriesGrid(
    onCategoryClick: (FileCategory) -> Unit,
    onAppsClick: () -> Unit,
    onDownloadsClick: () -> Unit,
    onTrashClick: () -> Unit
) {
    data class CategoryItemDef(
        val title: String,
        val icon: ImageVector,
        val tint: Color,
        val container: Color,
        val onClick: () -> Unit
    )

    val items = listOf(
        CategoryItemDef("Imágenes", Icons.Default.Image, CategoryImage, CategoryImageContainer) { onCategoryClick(FileCategory.IMAGE) },
        CategoryItemDef("Música", Icons.Default.AudioFile, CategoryAudio, CategoryAudioContainer) { onCategoryClick(FileCategory.AUDIO) },
        CategoryItemDef("Videos", Icons.Default.VideoFile, CategoryVideo, CategoryVideoContainer) { onCategoryClick(FileCategory.VIDEO) },
        CategoryItemDef("Documentos", Icons.Default.Description, CategoryDocument, CategoryDocumentContainer) { onCategoryClick(FileCategory.DOCUMENT) },
        CategoryItemDef("Apps", Icons.Default.Apps, CategoryApk, CategoryApkContainer, onAppsClick),
        CategoryItemDef("Comprimidos", Icons.Default.FolderZip, CategoryArchive, CategoryArchiveContainer) { onCategoryClick(FileCategory.ARCHIVE) },
        CategoryItemDef("Descargas", Icons.Default.Download, Color(0xFF0288D1), Color(0xFFE1F5FE), onDownloadsClick),
        CategoryItemDef("Papelera", Icons.Default.Delete, Color(0xFFE53935), Color(0xFFFFEBEE), onTrashClick)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        for (row in 0 until 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (col in 0 until 4) {
                    val index = row * 4 + col
                    val def = items[index]
                    ExCategoryCard(
                        title = def.title,
                        icon = def.icon,
                        tintColor = def.tint,
                        containerColor = def.container,
                        onClick = def.onClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ExCategoryCard(
    title: String,
    icon: ImageVector,
    tintColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tintColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                ),
                color = Color.Black.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CollectionRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

