package com.example.explorer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.explorer.domain.model.FileCategory
import com.example.explorer.domain.model.StorageInfo
import com.example.explorer.ui.ActiveScreen
import com.example.explorer.ui.theme.*

@Composable
fun AppDrawerContent(
    currentScreen: ActiveScreen,
    storageInfo: StorageInfo?,
    onNavigateHome: () -> Unit,
    onNavigateInternalStorage: () -> Unit,
    onNavigateCategory: (FileCategory) -> Unit,
    onNavigateTrash: () -> Unit,
    onNavigateSafeFolder: () -> Unit,
    onNavigateFavorites: () -> Unit,
    onNavigateAppManager: () -> Unit,
    onNavigateClean: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header con degradado azul al estilo clásico EX / ES File Explorer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DrawerHeaderGradient)
                    .padding(horizontal = 20.dp, vertical = 26.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Explorer",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 22.sp
                                )
                            )
                            Text(
                                text = "Gestor de Archivos Pro",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Indicador de almacenamiento rápido en el header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Almacenamiento: ${storageInfo?.formattedUsed ?: "0 B"} / ${storageInfo?.formattedTotal ?: "0 B"}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.95f),
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "${((storageInfo?.usedPercentage ?: 0f) * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFFFFD54F),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { storageInfo?.usedPercentage ?: 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFFFFD54F),
                        trackColor = Color.White.copy(alpha = 0.35f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SECCIÓN LOCAL
            DrawerSectionTitle("ESPACIO LOCAL")
            DrawerItem(
                label = "Inicio",
                icon = Icons.Default.Home,
                selected = currentScreen == ActiveScreen.BROWSE,
                onClick = {
                    onNavigateHome()
                    onCloseDrawer()
                }
            )
            DrawerItem(
                label = "Almacenamiento interno",
                icon = Icons.Default.PhoneAndroid,
                selected = currentScreen == ActiveScreen.EXPLORER_FOLDER,
                onClick = {
                    onNavigateInternalStorage()
                    onCloseDrawer()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
            )

            // SECCIÓN BIBLIOTECA
            DrawerSectionTitle("CATEGORÍAS")
            DrawerItem(
                label = "Imágenes",
                icon = Icons.Default.Image,
                iconTint = CategoryImage,
                onClick = {
                    onNavigateCategory(FileCategory.IMAGE)
                    onCloseDrawer()
                }
            )
            DrawerItem(
                label = "Música / Audio",
                icon = Icons.Default.AudioFile,
                iconTint = CategoryAudio,
                onClick = {
                    onNavigateCategory(FileCategory.AUDIO)
                    onCloseDrawer()
                }
            )
            DrawerItem(
                label = "Videos",
                icon = Icons.Default.VideoFile,
                iconTint = CategoryVideo,
                onClick = {
                    onNavigateCategory(FileCategory.VIDEO)
                    onCloseDrawer()
                }
            )
            DrawerItem(
                label = "Documentos",
                icon = Icons.Default.Description,
                iconTint = CategoryDocument,
                onClick = {
                    onNavigateCategory(FileCategory.DOCUMENT)
                    onCloseDrawer()
                }
            )
            DrawerItem(
                label = "Aplicaciones (App Manager)",
                icon = Icons.Default.Apps,
                iconTint = CategoryApk,
                selected = currentScreen == ActiveScreen.APP_MANAGER,
                onClick = {
                    onNavigateAppManager()
                    onCloseDrawer()
                }
            )
            DrawerItem(
                label = "Archivos comprimidos (ZIP)",
                icon = Icons.Default.FolderZip,
                iconTint = CategoryArchive,
                onClick = {
                    onNavigateCategory(FileCategory.ARCHIVE)
                    onCloseDrawer()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
            )

            // SECCIÓN HERRAMIENTAS
            DrawerSectionTitle("HERRAMIENTAS Y SEGURIDAD")
            DrawerItem(
                label = "Papelera de reciclaje",
                icon = Icons.Default.Delete,
                iconTint = CategoryTrash,
                selected = currentScreen == ActiveScreen.TRASH,
                onClick = {
                    onNavigateTrash()
                    onCloseDrawer()
                }
            )
            DrawerItem(
                label = "Carpeta segura",
                icon = Icons.Default.Lock,
                iconTint = CategorySafe,
                selected = currentScreen == ActiveScreen.SAFE_FOLDER,
                onClick = {
                    onNavigateSafeFolder()
                    onCloseDrawer()
                }
            )
            DrawerItem(
                label = "Favoritos",
                icon = Icons.Default.Star,
                iconTint = CategoryFolder,
                selected = currentScreen == ActiveScreen.FAVORITES,
                onClick = {
                    onNavigateFavorites()
                    onCloseDrawer()
                }
            )
            DrawerItem(
                label = "Analizador de espacio",
                icon = Icons.Default.CleaningServices,
                iconTint = PrimaryBlue,
                selected = currentScreen == ActiveScreen.CLEAN,
                onClick = {
                    onNavigateClean()
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun DrawerSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 0.8.sp
        ),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
fun DrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean = false,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                )
            )
        },
        icon = {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (selected) Color.Transparent else iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.primary else iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        selected = selected,
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
    )
}
