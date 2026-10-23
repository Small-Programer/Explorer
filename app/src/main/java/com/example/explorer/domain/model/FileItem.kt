package com.example.explorer.domain.model

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FileItem(
    val file: File,
    val name: String = file.name,
    val path: String = file.absolutePath,
    val isDirectory: Boolean = file.isDirectory,
    val sizeBytes: Long = if (file.isDirectory) 0L else file.length(),
    val lastModified: Long = file.lastModified(),
    val isHidden: Boolean = file.isHidden || file.name.startsWith("."),
    val extension: String = if (file.isDirectory) "" else file.extension.lowercase(Locale.ROOT),
    val itemCount: Int = 0 // Sub-elementos si es directorio
) {
    val category: FileCategory
        get() {
            if (isDirectory) return FileCategory.OTHER
            return when (extension) {
                "jpg", "jpeg", "png", "gif", "bmp", "webp", "heic", "svg" -> FileCategory.IMAGE
                "mp4", "mkv", "avi", "mov", "flv", "webm", "3gp", "ts" -> FileCategory.VIDEO
                "mp3", "m4a", "wav", "flac", "aac", "ogg", "wma", "opus" -> FileCategory.AUDIO
                "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "md", "csv", "json", "xml" -> FileCategory.DOCUMENT
                "zip", "rar", "7z", "tar", "gz", "bz2", "xz" -> FileCategory.ARCHIVE
                "apk", "xapk", "apks" -> FileCategory.APK
                else -> FileCategory.OTHER
            }
        }

    val formattedSize: String
        get() {
            if (isDirectory) {
                return if (itemCount >= 0) "$itemCount elementos" else "Carpeta"
            }
            if (sizeBytes <= 0) return "0 B"
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            var digitGroups = (Math.log10(sizeBytes.toDouble()) / Math.log10(1024.0)).toInt()
            if (digitGroups >= units.size) digitGroups = units.size - 1
            val value = sizeBytes / Math.pow(1024.0, digitGroups.toDouble())
            return String.format(Locale.getDefault(), "%.1f %s", value, units[digitGroups])
        }

    val formattedDate: String
        get() {
            if (lastModified <= 0) return ""
            val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            return formatter.format(Date(lastModified))
        }
}
