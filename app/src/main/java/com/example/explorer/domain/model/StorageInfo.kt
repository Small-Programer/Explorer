package com.example.explorer.domain.model

import java.util.Locale

data class StorageInfo(
    val totalBytes: Long,
    val freeBytes: Long,
    val usedBytes: Long = totalBytes - freeBytes
) {
    val usedPercentage: Float
        get() = if (totalBytes > 0) (usedBytes.toFloat() / totalBytes.toFloat()) else 0f

    val formattedTotal: String get() = formatBytes(totalBytes)
    val formattedUsed: String get() = formatBytes(usedBytes)
    val formattedFree: String get() = formatBytes(freeBytes)

    private fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        if (digitGroups >= units.size) digitGroups = units.size - 1
        val value = bytes / Math.pow(1024.0, digitGroups.toDouble())
        return String.format(Locale.getDefault(), "%.1f %s", value, units[digitGroups])
    }
}

enum class ClipboardAction {
    COPY,
    CUT
}

data class ClipboardData(
    val action: ClipboardAction,
    val sourceFiles: List<FileItem>
)
