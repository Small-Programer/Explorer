package com.example.explorer.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Paleta Principal Material You Moderna (Deep Royal Blue / Android 14/15 Style)
val PrimaryBlue = Color(0xFF0B57D0)
val PrimaryBlueLight = Color(0xFFD3E3FD)
val PrimaryBlueDark = Color(0xFFA8C7FA)

val SurfaceLight = Color(0xFFF8F9FC)
val SurfaceDark = Color(0xFF111318)
val SurfaceVariantLight = Color(0xFFEEF0F6)
val SurfaceVariantDark = Color(0xFF22242A)

val CardBackgroundLight = Color(0xFFFFFFFF)
val CardBackgroundDark = Color(0xFF1E2025)

// Gradientes de Cabecera y Tarjetas Premium
val DrawerHeaderGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0842A0),
        Color(0xFF0B57D0),
        Color(0xFF1A73E8)
    )
)

val StorageCardGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF0B57D0),
        Color(0xFF1A73E8),
        Color(0xFF4285F4)
    )
)

// Colores de Categorías Vibrantes con fondo pastel suave
val CategoryImage = Color(0xFFE91E63)
val CategoryImageContainer = Color(0xFFFCE4EC)

val CategoryVideo = Color(0xFF8E24AA)
val CategoryVideoContainer = Color(0xFFF3E5F5)

val CategoryAudio = Color(0xFF00897B)
val CategoryAudioContainer = Color(0xFFE0F2F1)

val CategoryDocument = Color(0xFF1E88E5)
val CategoryDocumentContainer = Color(0xFFE3F2FD)

val CategoryArchive = Color(0xFFFF6F00)
val CategoryArchiveContainer = Color(0xFFFFF3E0)

val CategoryApk = Color(0xFF2E7D32)
val CategoryApkContainer = Color(0xFFE8F5E9)

val CategoryFolder = Color(0xFFFB8C00)
val CategoryFolderContainer = Color(0xFFFFF8E1)

val CategoryDownloads = Color(0xFF0288D1)
val CategoryDownloadsContainer = Color(0xFFE1F5FE)

val CategoryTrash = Color(0xFFD32F2F)
val CategoryTrashContainer = Color(0xFFFFEBEE)

val CategorySafe = Color(0xFF5C6BC0)
val CategorySafeContainer = Color(0xFFE8EAF6)

val StorageBarFree = Color(0xFFE2E4E9)
val StorageBarUsed = Color(0xFF0B57D0)
