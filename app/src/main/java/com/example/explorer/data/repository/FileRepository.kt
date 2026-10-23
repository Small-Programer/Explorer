package com.example.explorer.data.repository

import android.content.Context
import android.os.Environment
import android.os.StatFs
import com.example.explorer.domain.model.FileCategory
import com.example.explorer.domain.model.FileItem
import com.example.explorer.domain.model.SortOrder
import com.example.explorer.domain.model.StorageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class FileRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("explorer_settings", Context.MODE_PRIVATE)

    val rootPath: String
        get() = Environment.getExternalStorageDirectory().absolutePath

    private val trashDir: File
        get() = File(context.filesDir, ".trash").apply { if (!exists()) mkdirs() }

    private val safeDir: File
        get() = File(context.filesDir, ".safe_folder").apply { if (!exists()) mkdirs() }

    // --- Control de PIN para Carpeta Segura ---
    fun isSafePinSet(): Boolean = prefs.contains("safe_folder_pin")

    fun setSafePin(pin: String) {
        prefs.edit().putString("safe_folder_pin", pin).apply()
    }

    fun verifySafePin(pin: String): Boolean {
        return prefs.getString("safe_folder_pin", "") == pin
    }

    // --- Favoritos ---
    fun toggleFavorite(path: String): Boolean {
        val favs = prefs.getStringSet("favorites", emptySet())?.toMutableSet() ?: mutableSetOf()
        val isFav = if (favs.contains(path)) {
            favs.remove(path)
            false
        } else {
            favs.add(path)
            true
        }
        prefs.edit().putStringSet("favorites", favs).apply()
        return isFav
    }

    fun isFavorite(path: String): Boolean {
        return prefs.getStringSet("favorites", emptySet())?.contains(path) ?: false
    }

    suspend fun getFavoriteFiles(): List<FileItem> = withContext(Dispatchers.IO) {
        val favs = prefs.getStringSet("favorites", emptySet()) ?: emptySet()
        favs.mapNotNull { path ->
            val file = File(path)
            if (file.exists()) FileItem(file) else null
        }
    }

    // --- Listado y Navegación ---
    suspend fun getFiles(
        directoryPath: String,
        sortOrder: SortOrder = SortOrder.NAME_ASC,
        showHidden: Boolean = false
    ): List<FileItem> = withContext(Dispatchers.IO) {
        val dir = File(directoryPath)
        if (!dir.exists() || !dir.isDirectory) return@withContext emptyList()

        val rawFiles = dir.listFiles() ?: return@withContext emptyList()

        val filtered = rawFiles.filter { file ->
            if (!showHidden && (file.isHidden || file.name.startsWith("."))) {
                false
            } else {
                true
            }
        }.map { file ->
            val count = if (file.isDirectory) file.listFiles()?.size ?: 0 else 0
            FileItem(file = file, itemCount = count)
        }

        filtered.sortedWith { a, b ->
            if (a.isDirectory && !b.isDirectory) {
                -1
            } else if (!a.isDirectory && b.isDirectory) {
                1
            } else {
                when (sortOrder) {
                    SortOrder.NAME_ASC -> a.name.compareTo(b.name, ignoreCase = true)
                    SortOrder.NAME_DESC -> b.name.compareTo(a.name, ignoreCase = true)
                    SortOrder.DATE_DESC -> b.lastModified.compareTo(a.lastModified)
                    SortOrder.DATE_ASC -> a.lastModified.compareTo(b.lastModified)
                    SortOrder.SIZE_DESC -> b.sizeBytes.compareTo(a.sizeBytes)
                    SortOrder.SIZE_ASC -> a.sizeBytes.compareTo(b.sizeBytes)
                    SortOrder.TYPE_ASC -> a.extension.compareTo(b.extension, ignoreCase = true)
                }
            }
        }
    }

    suspend fun getStorageInfo(): StorageInfo = withContext(Dispatchers.IO) {
        try {
            val path = Environment.getExternalStorageDirectory().path
            val stat = StatFs(path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong

            val total = totalBlocks * blockSize
            val free = availableBlocks * blockSize
            StorageInfo(totalBytes = total, freeBytes = free)
        } catch (e: Exception) {
            StorageInfo(totalBytes = 0L, freeBytes = 0L)
        }
    }

    // --- Papelera de Reciclaje (Trash) ---
    suspend fun moveToTrash(items: List<FileItem>): Result<Int> = withContext(Dispatchers.IO) {
        var count = 0
        try {
            for (item in items) {
                // Guardar metadato de ruta original
                val metadataFile = File(trashDir, "${item.name}.origin")
                metadataFile.writeText(item.path)

                val dest = File(trashDir, item.name)
                if (item.file.renameTo(dest)) {
                    count++
                } else {
                    copyRecursively(item.file, dest)
                    deleteRecursively(item.file)
                    count++
                }
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrashFiles(): List<FileItem> = withContext(Dispatchers.IO) {
        trashDir.listFiles()?.filter { !it.name.endsWith(".origin") }?.map { FileItem(it) } ?: emptyList()
    }

    suspend fun restoreFromTrash(item: FileItem): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val metaFile = File(trashDir, "${item.name}.origin")
            val originalPath = if (metaFile.exists()) metaFile.readText() else rootPath + "/" + item.name
            val dest = File(originalPath)
            if (item.file.renameTo(dest)) {
                metaFile.delete()
                Result.success(true)
            } else {
                copyRecursively(item.file, dest)
                item.file.delete()
                metaFile.delete()
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun emptyTrash(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val files = trashDir.listFiles() ?: return@withContext Result.success(0)
            var count = 0
            for (f in files) {
                if (deleteRecursively(f)) count++
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Carpeta Segura (Safe Folder) ---
    suspend fun moveToSafeFolder(items: List<FileItem>): Result<Int> = withContext(Dispatchers.IO) {
        var count = 0
        try {
            for (item in items) {
                val dest = File(safeDir, item.name)
                if (item.file.renameTo(dest)) {
                    count++
                } else {
                    copyRecursively(item.file, dest)
                    deleteRecursively(item.file)
                    count++
                }
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSafeFolderFiles(): List<FileItem> = withContext(Dispatchers.IO) {
        safeDir.listFiles()?.map { FileItem(it) } ?: emptyList()
    }

    suspend fun removeFromSafeFolder(item: FileItem): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val dest = File(rootPath + "/Download", item.name)
            if (item.file.renameTo(dest)) {
                Result.success(true)
            } else {
                copyRecursively(item.file, dest)
                item.file.delete()
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Limpieza Inteligente (Clean Tab) ---
    suspend fun getJunkFiles(): List<FileItem> = withContext(Dispatchers.IO) {
        val root = Environment.getExternalStorageDirectory()
        val junk = mutableListOf<FileItem>()
        fun scan(dir: File) {
            if (junk.size >= 50) return
            val children = dir.listFiles() ?: return
            for (c in children) {
                if (c.name.endsWith(".tmp", ignoreCase = true) ||
                    c.name.endsWith(".log", ignoreCase = true) ||
                    c.name.equals(".thumb", ignoreCase = true)) {
                    junk.add(FileItem(c))
                }
                if (c.isDirectory && !c.name.startsWith(".")) {
                    scan(c)
                }
            }
        }
        scan(root)
        junk
    }

    suspend fun cleanJunkFiles(): Result<Int> = withContext(Dispatchers.IO) {
        val junk = getJunkFiles()
        var count = 0
        for (item in junk) {
            if (deleteRecursively(item.file)) count++
        }
        Result.success(count)
    }

    suspend fun getLargeFiles(thresholdBytes: Long = 50 * 1024 * 1024): List<FileItem> = withContext(Dispatchers.IO) {
        val root = Environment.getExternalStorageDirectory()
        val large = mutableListOf<FileItem>()
        fun scan(dir: File) {
            if (large.size >= 30) return
            val children = dir.listFiles() ?: return
            for (c in children) {
                if (!c.isDirectory && c.length() >= thresholdBytes) {
                    large.add(FileItem(c))
                } else if (c.isDirectory && !c.name.startsWith(".") && !c.name.equals("Android", ignoreCase = true)) {
                    scan(c)
                }
            }
        }
        scan(root)
        large.sortedByDescending { it.sizeBytes }
    }

    // --- Operaciones Estándar CRUD ---
    suspend fun createFolder(parentPath: String, name: String): Result<FileItem> = withContext(Dispatchers.IO) {
        try {
            val newDir = File(parentPath, name)
            if (newDir.exists()) {
                return@withContext Result.failure(Exception("Ya existe una carpeta con ese nombre"))
            }
            if (newDir.mkdirs()) {
                Result.success(FileItem(newDir, itemCount = 0))
            } else {
                Result.failure(Exception("No se pudo crear la carpeta"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rename(file: File, newName: String): Result<FileItem> = withContext(Dispatchers.IO) {
        try {
            val dest = File(file.parentFile, newName)
            if (dest.exists()) {
                return@withContext Result.failure(Exception("Ya existe un archivo con ese nombre"))
            }
            if (file.renameTo(dest)) {
                val count = if (dest.isDirectory) dest.listFiles()?.size ?: 0 else 0
                Result.success(FileItem(dest, itemCount = count))
            } else {
                Result.failure(Exception("No se pudo renombrar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun delete(items: List<FileItem>): Result<Int> = withContext(Dispatchers.IO) {
        var count = 0
        try {
            for (item in items) {
                if (deleteRecursively(item.file)) {
                    count++
                }
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun deleteRecursively(file: File): Boolean {
        if (file.isDirectory) {
            val children = file.listFiles()
            if (children != null) {
                for (child in children) {
                    deleteRecursively(child)
                }
            }
        }
        return file.delete()
    }

    suspend fun copy(sources: List<FileItem>, destinationDir: File): Result<Int> = withContext(Dispatchers.IO) {
        var count = 0
        try {
            for (source in sources) {
                val destFile = File(destinationDir, source.name)
                copyRecursively(source.file, destFile)
                count++
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun move(sources: List<FileItem>, destinationDir: File): Result<Int> = withContext(Dispatchers.IO) {
        var count = 0
        try {
            for (source in sources) {
                val destFile = File(destinationDir, source.name)
                val renamed = source.file.renameTo(destFile)
                if (!renamed) {
                    copyRecursively(source.file, destFile)
                    deleteRecursively(source.file)
                }
                count++
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun copyRecursively(source: File, dest: File) {
        if (source.isDirectory) {
            if (!dest.exists()) dest.mkdirs()
            val children = source.listFiles() ?: return
            for (child in children) {
                copyRecursively(child, File(dest, child.name))
            }
        } else {
            FileInputStream(source).use { input ->
                FileOutputStream(dest).use { output ->
                    input.copyTo(output, bufferSize = 64 * 1024)
                }
            }
        }
    }

    suspend fun search(query: String, startPath: String = rootPath): List<FileItem> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val results = mutableListOf<FileItem>()
        val startFile = File(startPath)

        fun scan(file: File) {
            if (results.size >= 100) return
            val children = file.listFiles() ?: return
            for (child in children) {
                if (child.name.contains(query, ignoreCase = true)) {
                    val count = if (child.isDirectory) child.listFiles()?.size ?: 0 else 0
                    results.add(FileItem(child, itemCount = count))
                }
                if (child.isDirectory && !child.name.startsWith(".")) {
                    scan(child)
                }
            }
        }

        scan(startFile)
        results
    }

    suspend fun getCategoryFiles(category: FileCategory): List<FileItem> = withContext(Dispatchers.IO) {
        val results = mutableListOf<FileItem>()
        val root = Environment.getExternalStorageDirectory()

        fun scanCategory(dir: File) {
            if (results.size >= 150) return
            val children = dir.listFiles() ?: return
            for (child in children) {
                if (!child.name.startsWith(".")) {
                    if (child.isDirectory) {
                        if (!child.name.equals("Android", ignoreCase = true)) {
                            scanCategory(child)
                        }
                    } else {
                        val item = FileItem(child)
                        if (item.category == category) {
                            results.add(item)
                        }
                    }
                }
            }
        }

        scanCategory(root)
        results.sortedByDescending { it.lastModified }
    }

    suspend fun getRecentFiles(limit: Int = 15): List<FileItem> = withContext(Dispatchers.IO) {
        val root = Environment.getExternalStorageDirectory()
        val allFiles = mutableListOf<FileItem>()

        fun scan(dir: File) {
            if (allFiles.size >= 250) return
            val children = dir.listFiles() ?: return
            for (child in children) {
                if (!child.name.startsWith(".")) {
                    if (child.isDirectory) {
                        if (!child.name.equals("Android", ignoreCase = true)) {
                            scan(child)
                        }
                    } else {
                        allFiles.add(FileItem(child))
                    }
                }
            }
        }

        scan(root)
        allFiles.sortedByDescending { it.lastModified }.take(limit)
    }
}
