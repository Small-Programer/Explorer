package com.example.explorer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.explorer.domain.model.FileItem
import com.example.explorer.ui.components.FileListItem

enum class CollectionType(val title: String) {
    TRASH("Papelera"),
    SAFE_FOLDER("Carpeta segura"),
    FAVORITES("Favoritos")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    type: CollectionType,
    files: List<FileItem>,
    onBackClick: () -> Unit,
    onFileClick: (FileItem) -> Unit,
    onActionClick: (FileItem) -> Unit,
    onEmptyTrashClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(type.title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    if (type == CollectionType.TRASH && files.isNotEmpty() && onEmptyTrashClick != null) {
                        TextButton(onClick = onEmptyTrashClick) {
                            Text("Vaciar papelera", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (files.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = when (type) {
                            CollectionType.TRASH -> "La papelera está vacía"
                            CollectionType.SAFE_FOLDER -> "No hay archivos en la Carpeta segura"
                            CollectionType.FAVORITES -> "No tienes archivos favoritos aún"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(files, key = { it.path }) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                FileListItem(
                                    item = item,
                                    isSelected = false,
                                    isSelectionMode = false,
                                    onClick = { onFileClick(item) },
                                    onLongClick = {},
                                    onMoreClick = {}
                                )
                            }

                            when (type) {
                                CollectionType.TRASH -> {
                                    IconButton(onClick = { onActionClick(item) }) {
                                        Icon(Icons.Default.Restore, contentDescription = "Restaurar", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                CollectionType.SAFE_FOLDER -> {
                                    IconButton(onClick = { onActionClick(item) }) {
                                        Icon(Icons.Default.Lock, contentDescription = "Extraer a Descargas", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                CollectionType.FAVORITES -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}
