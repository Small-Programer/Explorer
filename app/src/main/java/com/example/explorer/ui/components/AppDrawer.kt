package com.example.explorer.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    showHidden: Boolean = false,
    onToggleShowHidden: () -> Unit = {},
    isDarkTheme: Boolean = false,
    onToggleDarkTheme: () -> Unit = {},
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
    var isAlmacenamientoExpanded by remember { mutableStateOf(true) }
    var isTipoExpanded by remember { mutableStateOf(true) }
    var isHerramientasExpanded by remember { mutableStateOf(true) }

    ModalDrawerSheet(
        modifier = Modifier.width(310.dp),
        drawerShape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header estilo EX Pro con Corona Dorada
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFB300).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Pro",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "EX Explorador",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = "EX Pro Edition",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 1. SECCIÓN: ALMACENAMIENTO (Desplegable)
            DrawerCollapsibleHeader(
                title = "Almacenamiento",
                icon = Icons.Default.SdStorage,
                isExpanded = isAlmacenamientoExpanded,
                onToggle = { isAlmacenamientoExpanded = !isAlmacenamientoExpanded }
            )

            AnimatedVisibility(visible = isAlmacenamientoExpanded) {
                Column {
                    DrawerItemRow(
                        label = "Inicio",
                        icon = Icons.Default.Home,
                        selected = currentScreen == ActiveScreen.BROWSE,
                        onClick = {
                            onNavigateHome()
                            onCloseDrawer()
                        }
                    )
                    DrawerItemRow(
                        label = "Almacenamiento interno",
                        icon = Icons.Default.PhoneAndroid,
                        selected = currentScreen == ActiveScreen.EXPLORER_FOLDER,
                        onClick = {
                            onNavigateInternalStorage()
                            onCloseDrawer()
                        }
                    )
                }
            }

            // 2. SECCIÓN: TIPO / CATEGORÍAS (Desplegable)
            DrawerCollapsibleHeader(
                title = "Tipo",
                icon = Icons.Default.Category,
                isExpanded = isTipoExpanded,
                onToggle = { isTipoExpanded = !isTipoExpanded }
            )

            AnimatedVisibility(visible = isTipoExpanded) {
                Column {
                    DrawerItemRow(
                        label = "Imágenes",
                        icon = Icons.Default.Image,
                        iconTint = Color(0xFFEF5350),
                        onClick = {
                            onNavigateCategory(FileCategory.IMAGE)
                            onCloseDrawer()
                        }
                    )
                    DrawerItemRow(
                        label = "Videos",
                        icon = Icons.Default.Videocam,
                        iconTint = Color(0xFF29B6F6),
                        onClick = {
                            onNavigateCategory(FileCategory.VIDEO)
                            onCloseDrawer()
                        }
                    )
                    DrawerItemRow(
                        label = "Audio",
                        icon = Icons.Default.Headphones,
                        iconTint = Color(0xFFAB47BC),
                        onClick = {
                            onNavigateCategory(FileCategory.AUDIO)
                            onCloseDrawer()
                        }
                    )
                    DrawerItemRow(
                        label = "Documentos",
                        icon = Icons.Default.Description,
                        iconTint = Color(0xFF1E88E5),
                        onClick = {
                            onNavigateCategory(FileCategory.DOCUMENT)
                            onCloseDrawer()
                        }
                    )
                    DrawerItemRow(
                        label = "Aplicaciones (APP)",
                        icon = Icons.Default.Android,
                        iconTint = Color(0xFF66BB6A),
                        selected = currentScreen == ActiveScreen.APP_MANAGER,
                        onClick = {
                            onNavigateAppManager()
                            onCloseDrawer()
                        }
                    )
                    DrawerItemRow(
                        label = "Comprimido",
                        icon = Icons.Default.FolderZip,
                        iconTint = Color(0xFF42A5F5),
                        onClick = {
                            onNavigateCategory(FileCategory.ARCHIVE)
                            onCloseDrawer()
                        }
                    )
                }
            }

            // 3. SECCIÓN: HERRAMIENTAS (Desplegable)
            DrawerCollapsibleHeader(
                title = "Herramientas",
                icon = Icons.Default.Build,
                isExpanded = isHerramientasExpanded,
                onToggle = { isHerramientasExpanded = !isHerramientasExpanded }
            )

            AnimatedVisibility(visible = isHerramientasExpanded) {
                Column {
                    DrawerItemRow(
                        label = "Limpiador",
                        icon = Icons.Default.CleaningServices,
                        iconTint = Color(0xFF26A69A),
                        selected = currentScreen == ActiveScreen.CLEAN,
                        onClick = {
                            onNavigateClean()
                            onCloseDrawer()
                        }
                    )
                    DrawerItemRow(
                        label = "Carpeta segura",
                        icon = Icons.Default.Lock,
                        iconTint = Color(0xFF5C6BC0),
                        selected = currentScreen == ActiveScreen.SAFE_FOLDER,
                        onClick = {
                            onNavigateSafeFolder()
                            onCloseDrawer()
                        }
                    )
                    DrawerItemRow(
                        label = "Papelera",
                        icon = Icons.Default.Delete,
                        iconTint = Color(0xFF78909C),
                        selected = currentScreen == ActiveScreen.TRASH,
                        onClick = {
                            onNavigateTrash()
                            onCloseDrawer()
                        }
                    )
                    DrawerItemRow(
                        label = "Favoritos",
                        icon = Icons.Default.Star,
                        iconTint = Color(0xFFFFCA28),
                        selected = currentScreen == ActiveScreen.FAVORITES,
                        onClick = {
                            onNavigateFavorites()
                            onCloseDrawer()
                        }
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
            )

            // 4. OPCIONES DIRECTAS ESTILO EX ORIGINAL: MOSTRAR ARCHIVOS OCULTOS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleShowHidden() }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Mostrar Archivos Ocultos",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = showHidden,
                    onCheckedChange = { onToggleShowHidden() }
                )
            }

            // Modo Oscuro / Cristal Claro
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleDarkTheme() }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Modo Oscuro",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { onToggleDarkTheme() }
                )
            }

            // Ajustes
            DrawerItemRow(
                label = "Ajustes",
                icon = Icons.Default.Settings,
                onClick = { onCloseDrawer() }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DrawerCollapsibleHeader(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun DrawerItemRow(
    label: String,
    icon: ImageVector,
    selected: Boolean = false,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else Color.Transparent
            )
            .padding(horizontal = 28.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.primary else iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 14.sp
            ),
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
