package com.example.explorer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.example.explorer.domain.model.FileItem
import com.example.explorer.domain.model.SortOrder
import com.example.explorer.ui.DialogType
import com.example.explorer.util.FileUtils

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun AppDialogHost(
    dialogType: DialogType?,
    onDismiss: () -> Unit,
    onCreateFolder: (name: String) -> Unit,
    onRename: (item: FileItem, newName: String) -> Unit,
    onDeleteConfirm: (items: List<FileItem>) -> Unit,
    onSetSafePin: (pin: String) -> Unit = {},
    onEnterSafePin: (pin: String) -> Unit = {},
    onCompress: (items: List<FileItem>, zipName: String) -> Unit = { _, _ -> },
    onSaveTextFile: (file: java.io.File, content: String) -> Unit = { _, _ -> },
    onCalculateChecksum: (item: FileItem) -> Unit = {},
    currentSortOrder: SortOrder,
    onSelectSortOrder: (SortOrder) -> Unit
) {
    if (dialogType == null) return

    when (dialogType) {
        is DialogType.CreateFolder -> {
            var folderName by remember { mutableStateOf("") }
            AlertDialog(
                shape = RoundedCornerShape(28.dp),
                onDismissRequest = onDismiss,
                title = { Text("Nueva Carpeta") },
                text = {
                    OutlinedTextField(
                        value = folderName,
                        onValueChange = { folderName = it },
                        label = { Text("Nombre de carpeta") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (folderName.isNotBlank()) {
                                onCreateFolder(folderName)
                            }
                        },
                        enabled = folderName.isNotBlank()
                    ) {
                        Text("Crear")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                }
            )
        }

        is DialogType.Rename -> {
            var newName by remember { mutableStateOf(dialogType.item.name) }
            AlertDialog(
                shape = RoundedCornerShape(28.dp),
                onDismissRequest = onDismiss,
                title = { Text("Renombrar") },
                text = {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Nuevo nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newName.isNotBlank() && newName != dialogType.item.name) {
                                onRename(dialogType.item, newName)
                            }
                        },
                        enabled = newName.isNotBlank() && newName != dialogType.item.name
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                }
            )
        }

        is DialogType.DeleteConfirm -> {
            AlertDialog(
                shape = RoundedCornerShape(28.dp),
                onDismissRequest = onDismiss,
                title = { Text("¿Eliminar definitivamente?") },
                text = {
                    Text(
                        if (dialogType.items.size == 1) {
                            "¿Estás seguro de que deseas eliminar permanentemente \"${dialogType.items.first().name}\"?"
                        } else {
                            "¿Estás seguro de que deseas eliminar ${dialogType.items.size} elementos seleccionados?"
                        }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { onDeleteConfirm(dialogType.items) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                }
            )
        }

        is DialogType.FileDetails -> {
            val item = dialogType.item
            AlertDialog(
                shape = RoundedCornerShape(28.dp),
                onDismissRequest = onDismiss,
                title = { Text("Detalles del Archivo") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DetailRow("Nombre", item.name)
                        DetailRow("Tipo", if (item.isDirectory) "Carpeta" else item.category.title)
                        DetailRow("Tamaño", item.formattedSize)
                        DetailRow("Modificado", item.formattedDate)
                        DetailRow("Ruta", item.path)
                        if (!item.isDirectory) {
                            DetailRow("MIME Type", FileUtils.getMimeType(item.file))

                            Spacer(modifier = Modifier.height(4.dp))
                            if (dialogType.md5 != null) {
                                DetailRow("MD5", dialogType.md5)
                                DetailRow("SHA-256", dialogType.sha256 ?: "")
                            } else {
                                OutlinedButton(
                                    onClick = { onCalculateChecksum(item) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Calcular Checksum (MD5 / SHA-256)")
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = onDismiss) { Text("Cerrar") }
                }
            )
        }

        is DialogType.CompressZip -> {
            val defaultName = if (dialogType.items.size == 1) {
                "${dialogType.items.first().name.substringBeforeLast('.')}.zip"
            } else {
                "archivo_comprimido.zip"
            }
            var zipName by remember { mutableStateOf(defaultName) }
            AlertDialog(
                shape = RoundedCornerShape(28.dp),
                onDismissRequest = onDismiss,
                title = { Text("Comprimir a ZIP") },
                text = {
                    Column {
                        Text("Se comprimirán ${dialogType.items.size} elemento(s).")
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = zipName,
                            onValueChange = { zipName = it },
                            label = { Text("Nombre del archivo ZIP") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { if (zipName.isNotBlank()) onCompress(dialogType.items, zipName) },
                        enabled = zipName.isNotBlank()
                    ) {
                        Text("Comprimir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                }
            )
        }

        is DialogType.TextEditor -> {
            var textContent by remember { mutableStateOf(dialogType.content) }
            AlertDialog(
                shape = RoundedCornerShape(20.dp),
                onDismissRequest = onDismiss,
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(
                            text = dialogType.file.name,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${textContent.length} car.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                text = {
                    OutlinedTextField(
                        value = textContent,
                        onValueChange = { textContent = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    )
                },
                confirmButton = {
                    Button(onClick = { onSaveTextFile(dialogType.file, textContent) }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text("Cerrar") }
                }
            )
        }

        is DialogType.SetSafePin -> {
            var pin by remember { mutableStateOf("") }
            AlertDialog(
                shape = RoundedCornerShape(28.dp),
                onDismissRequest = onDismiss,
                title = { Text("Configurar PIN de Carpeta Segura") },
                text = {
                    Column {
                        Text("Ingresa un PIN numérico de 4 dígitos para proteger tus archivos sensibles.", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = pin,
                            onValueChange = { if (it.length <= 6) pin = it },
                            label = { Text("PIN de seguridad") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { if (pin.length >= 4) onSetSafePin(pin) },
                        enabled = pin.length >= 4
                    ) {
                        Text("Guardar PIN")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                }
            )
        }

        is DialogType.EnterSafePin -> {
            var pin by remember { mutableStateOf("") }
            AlertDialog(
                shape = RoundedCornerShape(28.dp),
                onDismissRequest = onDismiss,
                title = { Text("Carpeta Segura Bloqueada") },
                text = {
                    Column {
                        Text("Introduce tu PIN para acceder a tus archivos protegidos.", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = pin,
                            onValueChange = { pin = it },
                            label = { Text("PIN") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { onEnterSafePin(pin) },
                        enabled = pin.isNotBlank()
                    ) {
                        Text("Desbloquear")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                }
            )
        }

        is DialogType.SortPicker -> {
            AlertDialog(
                shape = RoundedCornerShape(28.dp),
                onDismissRequest = onDismiss,
                title = { Text("Ordenar por") },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SortOrder.values().forEach { order ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(
                                    onClick = {
                                        onSelectSortOrder(order)
                                        onDismiss()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = order.label,
                                        color = if (order == currentSortOrder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = onDismiss) { Text("Cerrar") }
                }
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
