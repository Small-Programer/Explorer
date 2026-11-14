package com.example.explorer.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.explorer.domain.model.FileCategory
import com.example.explorer.domain.model.FileItem
import com.example.explorer.ui.components.FileItemsBrowser
import com.example.explorer.ui.components.FileViewMode
import com.example.explorer.ui.components.FileViewModeMenuButton
import com.example.explorer.ui.components.GlassAmbientBackground
import com.example.explorer.ui.components.GlassLoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    category: FileCategory,
    files: List<FileItem>,
    isLoading: Boolean,
    viewMode: FileViewMode = FileViewMode.LARGE,
    onSelectViewMode: (FileViewMode) -> Unit = {},
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    onBackClick: () -> Unit,
    onFileClick: (FileItem) -> Unit,
    onMoreClick: (FileItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("${category.title} (${files.size})") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    FileViewModeMenuButton(
                        currentMode = viewMode,
                        onSelectMode = onSelectViewMode
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            GlassAmbientBackground(isDark = isDarkTheme)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (isLoading) {
                    GlassLoadingView(
                        message = "Escaneando ${category.title}...",
                        isDark = isDarkTheme
                    )
                } else if (files.isEmpty()) {
                    Text(
                        text = "No se encontraron archivos en esta categoría",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    FileItemsBrowser(
                        files = files,
                        viewMode = viewMode,
                        selectedFiles = emptySet(),
                        isSelectionMode = false,
                        onFileClick = onFileClick,
                        onFileLongClick = {},
                        onMoreClick = onMoreClick,
                        contentPaddingBottom = 24.dp
                    )
                }
            }
        }
    }
}
