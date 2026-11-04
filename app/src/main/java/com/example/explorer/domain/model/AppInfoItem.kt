package com.example.explorer.domain.model

import android.graphics.drawable.Drawable
import java.io.File
import java.util.Locale

data class AppInfoItem(
    val name: String,
    val packageName: String,
    val versionName: String,
    val icon: Drawable?,
    val sourceFile: File,
    val sizeBytes: Long = sourceFile.length(),
    val isSystemApp: Boolean = false
) {
    val formattedSize: String
        get() {
            if (sizeBytes <= 0) return "0 B"
            val units = arrayOf("B", "KB", "MB", "GB")
            var digitGroups = (Math.log10(sizeBytes.toDouble()) / Math.log10(1024.0)).toInt()
            if (digitGroups >= units.size) digitGroups = units.size - 1
            val value = sizeBytes / Math.pow(1024.0, digitGroups.toDouble())
            return String.format(Locale.getDefault(), "%.1f %s", value, units[digitGroups])
        }
}
