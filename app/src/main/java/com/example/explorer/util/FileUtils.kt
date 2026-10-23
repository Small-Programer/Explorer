package com.example.explorer.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.explorer.domain.model.FileCategory
import com.example.explorer.domain.model.FileItem
import java.io.File
import java.util.Locale

object FileUtils {

    fun getMimeType(file: File): String {
        val extension = file.extension.lowercase(Locale.ROOT)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: when (extension) {
            "apk" -> "application/vnd.android.package-archive"
            "json" -> "application/json"
            "md" -> "text/markdown"
            "log" -> "text/plain"
            "7z" -> "application/x-7z-compressed"
            "rar" -> "application/x-rar-compressed"
            else -> "*/*"
        }
    }

    fun openFile(context: Context, fileItem: FileItem) {
        try {
            val authority = "${context.packageName}.provider"
            val uri: Uri = FileProvider.getUriForFile(context, authority, fileItem.file)
            val mimeType = getMimeType(fileItem.file)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Abrir con...").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "No se encontró una aplicación para abrir este archivo: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun shareFiles(context: Context, files: List<FileItem>) {
        if (files.isEmpty()) return
        try {
            val authority = "${context.packageName}.provider"
            if (files.size == 1) {
                val file = files.first()
                val uri = FileProvider.getUriForFile(context, authority, file.file)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = getMimeType(file.file)
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "Compartir archivo").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            } else {
                val uris = ArrayList<Uri>()
                for (item in files) {
                    uris.add(FileProvider.getUriForFile(context, authority, item.file))
                }
                val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "*/*"
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "Compartir archivos").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error al compartir: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
