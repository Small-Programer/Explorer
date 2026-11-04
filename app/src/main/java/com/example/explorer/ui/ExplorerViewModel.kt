package com.example.explorer.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.explorer.data.repository.FileRepository
import com.example.explorer.domain.model.ClipboardAction
import com.example.explorer.domain.model.ClipboardData
import com.example.explorer.domain.model.FileCategory
import com.example.explorer.domain.model.FileItem
import com.example.explorer.domain.model.SortOrder
import com.example.explorer.domain.model.StorageInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

sealed interface DialogType {
    object CreateFolder : DialogType
    data class Rename(val item: FileItem) : DialogType
    data class DeleteConfirm(val items: List<FileItem>, val permanent: Boolean = false) : DialogType
    data class FileDetails(
        val item: FileItem,
        val md5: String? = null,
        val sha256: String? = null
    ) : DialogType
    data class CompressZip(val items: List<FileItem>) : DialogType
    data class TextEditor(val file: File, val content: String) : DialogType
    object SortPicker : DialogType
    object SetSafePin : DialogType
    object EnterSafePin : DialogType
}

enum class ActiveScreen {
    BROWSE,
    CLEAN,
    SHARE,
    EXPLORER_FOLDER,
    TRASH,
    SAFE_FOLDER,
    FAVORITES,
    APP_MANAGER
}

data class ExplorerUiState(
    val activeScreen: ActiveScreen = ActiveScreen.BROWSE,
    val currentPath: String = "",
    val files: List<FileItem> = emptyList(),
    val isLoading: Boolean = false,
    val selectedFiles: Set<FileItem> = emptySet(),
    val clipboard: ClipboardData? = null,
    val sortOrder: SortOrder = SortOrder.NAME_ASC,
    val isGridView: Boolean = false,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val searchResults: List<FileItem> = emptyList(),
    val storageInfo: StorageInfo? = null,
    val recentFiles: List<FileItem> = emptyList(),
    val selectedCategory: FileCategory? = null,
    val categoryFiles: List<FileItem> = emptyList(),
    val isCategoryLoading: Boolean = false,
    val activeDialog: DialogType? = null,
    val toastMessage: String? = null,

    // Colecciones Google Files
    val trashFiles: List<FileItem> = emptyList(),
    val safeFolderFiles: List<FileItem> = emptyList(),
    val isSafeFolderUnlocked: Boolean = false,
    val favoriteFiles: List<FileItem> = emptyList(),

    // Gestor de Aplicaciones (App Manager)
    val installedApps: List<com.example.explorer.domain.model.AppInfoItem> = emptyList(),
    val isAppsLoading: Boolean = false,

    // Limpieza Inteligente
    val junkFilesCount: Int = 0,
    val largeFiles: List<FileItem> = emptyList(),
    val isCleaning: Boolean = false
) {
    val isSelectionMode: Boolean get() = selectedFiles.isNotEmpty()
    
    val breadcrumbs: List<Pair<String, String>>
        get() {
            if (currentPath.isBlank()) return emptyList()
            val list = mutableListOf<Pair<String, String>>()
            val parts = currentPath.split(File.separator).filter { it.isNotEmpty() }
            var builtPath = ""
            for (part in parts) {
                builtPath += File.separator + part
                val displayName = if (part == "0" || part == "emulated") "Almacenamiento" else part
                list.add(Pair(displayName, builtPath))
            }
            return list
        }
}

class ExplorerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FileRepository(application)

    private val _uiState = MutableStateFlow(ExplorerUiState())
    val uiState: StateFlow<ExplorerUiState> = _uiState.asStateFlow()

    init {
        val root = repository.rootPath
        _uiState.update { it.copy(currentPath = root) }
        loadFiles(root)
        loadStorageInfo()
        loadRecentFiles()
        loadCleanStats()
    }

    fun setScreen(screen: ActiveScreen) {
        _uiState.update { it.copy(activeScreen = screen) }
        when (screen) {
            ActiveScreen.CLEAN -> loadCleanStats()
            ActiveScreen.TRASH -> loadTrashFiles()
            ActiveScreen.FAVORITES -> loadFavorites()
            ActiveScreen.APP_MANAGER -> loadInstalledApps()
            ActiveScreen.SAFE_FOLDER -> {
                if (!_uiState.value.isSafeFolderUnlocked) {
                    if (repository.isSafePinSet()) {
                        showDialog(DialogType.EnterSafePin)
                    } else {
                        showDialog(DialogType.SetSafePin)
                    }
                } else {
                    loadSafeFiles()
                }
            }
            else -> {}
        }
    }

    fun loadFiles(path: String = _uiState.value.currentPath) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentPath = path, selectedFiles = emptySet()) }
            val items = repository.getFiles(
                directoryPath = path,
                sortOrder = _uiState.value.sortOrder
            )
            _uiState.update { it.copy(files = items, isLoading = false) }
        }
    }

    fun loadStorageInfo() {
        viewModelScope.launch {
            val info = repository.getStorageInfo()
            _uiState.update { it.copy(storageInfo = info) }
        }
    }

    fun loadRecentFiles() {
        viewModelScope.launch {
            val recents = repository.getRecentFiles()
            _uiState.update { it.copy(recentFiles = recents) }
        }
    }

    fun loadCleanStats() {
        viewModelScope.launch {
            val junk = repository.getJunkFiles()
            val large = repository.getLargeFiles()
            _uiState.update { it.copy(junkFilesCount = junk.size, largeFiles = large) }
        }
    }

    fun cleanJunk() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCleaning = true) }
            val res = repository.cleanJunkFiles()
            _uiState.update {
                it.copy(
                    isCleaning = false,
                    junkFilesCount = 0,
                    toastMessage = "Se liberaron archivos temporales con éxito"
                )
            }
            loadStorageInfo()
        }
    }

    fun navigateTo(path: String) {
        _uiState.update { it.copy(activeScreen = ActiveScreen.EXPLORER_FOLDER) }
        loadFiles(path)
    }

    fun navigateUp(): Boolean {
        val current = File(_uiState.value.currentPath)
        val root = repository.rootPath
        val parent = current.parentFile ?: return false
        if (current.absolutePath == root) {
            return false
        }
        navigateTo(parent.absolutePath)
        return true
    }

    // --- Papelera de Reciclaje (Trash) ---
    fun loadTrashFiles() {
        viewModelScope.launch {
            val trash = repository.getTrashFiles()
            _uiState.update { it.copy(trashFiles = trash) }
        }
    }

    fun moveToTrash(items: List<FileItem>) {
        viewModelScope.launch {
            val result = repository.moveToTrash(items)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        toastMessage = "${result.getOrDefault(0)} archivo(s) movidos a la Papelera",
                        selectedFiles = emptySet()
                    )
                }
                loadFiles()
                loadStorageInfo()
            }
        }
    }

    fun restoreTrashItem(item: FileItem) {
        viewModelScope.launch {
            val res = repository.restoreFromTrash(item)
            if (res.isSuccess) {
                _uiState.update { it.copy(toastMessage = "Archivo restaurado a su ubicación original") }
                loadTrashFiles()
                loadFiles()
            }
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            val res = repository.emptyTrash()
            if (res.isSuccess) {
                _uiState.update { it.copy(trashFiles = emptyList(), toastMessage = "Papelera vaciada") }
                loadStorageInfo()
            }
        }
    }

    // --- Carpeta Segura ---
    fun setSafePin(pin: String) {
        repository.setSafePin(pin)
        _uiState.update { it.copy(isSafeFolderUnlocked = true, activeDialog = null) }
        loadSafeFiles()
    }

    fun unlockSafeFolder(pin: String): Boolean {
        if (repository.verifySafePin(pin)) {
            _uiState.update { it.copy(isSafeFolderUnlocked = true, activeDialog = null) }
            loadSafeFiles()
            return true
        } else {
            _uiState.update { it.copy(toastMessage = "PIN incorrecto") }
            return false
        }
    }

    fun loadSafeFiles() {
        viewModelScope.launch {
            val safe = repository.getSafeFolderFiles()
            _uiState.update { it.copy(safeFolderFiles = safe) }
        }
    }

    fun moveToSafeFolder(items: List<FileItem>) {
        if (!repository.isSafePinSet()) {
            showDialog(DialogType.SetSafePin)
            return
        }
        viewModelScope.launch {
            val res = repository.moveToSafeFolder(items)
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(
                        toastMessage = "${res.getOrDefault(0)} archivo(s) movidos a Carpeta Segura",
                        selectedFiles = emptySet()
                    )
                }
                loadFiles()
            }
        }
    }

    fun removeFromSafeFolder(item: FileItem) {
        viewModelScope.launch {
            val res = repository.removeFromSafeFolder(item)
            if (res.isSuccess) {
                _uiState.update { it.copy(toastMessage = "Archivo extraído a Descargas") }
                loadSafeFiles()
            }
        }
    }

    // --- Favoritos ---
    fun loadFavorites() {
        viewModelScope.launch {
            val favs = repository.getFavoriteFiles()
            _uiState.update { it.copy(favoriteFiles = favs) }
        }
    }

    fun toggleFavorite(item: FileItem) {
        val isFav = repository.toggleFavorite(item.path)
        val msg = if (isFav) "Añadido a Favoritos" else "Quitado de Favoritos"
        _uiState.update { it.copy(toastMessage = msg) }
        loadFavorites()
    }

    fun isFavorite(item: FileItem): Boolean = repository.isFavorite(item.path)

    // --- Selección y Vistas ---
    fun toggleSelection(item: FileItem) {
        _uiState.update { state ->
            val set = state.selectedFiles.toMutableSet()
            if (set.contains(item)) set.remove(item) else set.add(item)
            state.copy(selectedFiles = set)
        }
    }

    fun selectAll() {
        _uiState.update { it.copy(selectedFiles = it.files.toSet()) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedFiles = emptySet()) }
    }

    fun toggleViewMode() {
        _uiState.update { it.copy(isGridView = !it.isGridView) }
    }

    fun setSortOrder(order: SortOrder) {
        _uiState.update { it.copy(sortOrder = order) }
        loadFiles()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList()) }
            return
        }
        viewModelScope.launch {
            val results = repository.search(query, _uiState.value.currentPath)
            _uiState.update { it.copy(searchResults = results) }
        }
    }

    fun setSearchActive(active: Boolean) {
        _uiState.update {
            it.copy(
                isSearchActive = active,
                searchQuery = if (!active) "" else it.searchQuery,
                searchResults = if (!active) emptyList() else it.searchResults
            )
        }
    }

    fun openCategory(category: FileCategory) {
        _uiState.update { it.copy(selectedCategory = category, isCategoryLoading = true) }
        viewModelScope.launch {
            val items = repository.getCategoryFiles(category)
            _uiState.update { it.copy(categoryFiles = items, isCategoryLoading = false) }
        }
    }

    fun closeCategory() {
        _uiState.update { it.copy(selectedCategory = null, categoryFiles = emptyList()) }
    }

    // --- Portapapeles ---
    fun copySelected() {
        val selected = _uiState.value.selectedFiles.toList()
        if (selected.isEmpty()) return
        _uiState.update {
            it.copy(
                clipboard = ClipboardData(action = ClipboardAction.COPY, sourceFiles = selected),
                selectedFiles = emptySet(),
                toastMessage = "${selected.size} elemento(s) copiado(s)"
            )
        }
    }

    fun cutSelected() {
        val selected = _uiState.value.selectedFiles.toList()
        if (selected.isEmpty()) return
        _uiState.update {
            it.copy(
                clipboard = ClipboardData(action = ClipboardAction.CUT, sourceFiles = selected),
                selectedFiles = emptySet(),
                toastMessage = "${selected.size} elemento(s) listo(s) para mover"
            )
        }
    }

    fun pasteClipboard() {
        val clip = _uiState.value.clipboard ?: return
        val currentDir = File(_uiState.value.currentPath)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = when (clip.action) {
                ClipboardAction.COPY -> repository.copy(clip.sourceFiles, currentDir)
                ClipboardAction.CUT -> repository.move(clip.sourceFiles, currentDir)
            }
            if (result.isSuccess) {
                _uiState.update { it.copy(clipboard = null, toastMessage = "Pegado completado") }
            } else {
                _uiState.update { it.copy(toastMessage = "Error: ${result.exceptionOrNull()?.message}") }
            }
            loadFiles()
            loadStorageInfo()
        }
    }

    fun clearClipboard() {
        _uiState.update { it.copy(clipboard = null) }
    }

    // --- Diálogos ---
    fun showDialog(dialog: DialogType?) {
        _uiState.update { it.copy(activeDialog = dialog) }
    }

    fun createFolder(name: String) {
        viewModelScope.launch {
            val result = repository.createFolder(_uiState.value.currentPath, name.trim())
            if (result.isSuccess) {
                _uiState.update { it.copy(toastMessage = "Carpeta creada", activeDialog = null) }
                loadFiles()
            } else {
                _uiState.update { it.copy(toastMessage = result.exceptionOrNull()?.message ?: "Error al crear") }
            }
        }
    }

    fun renameFile(item: FileItem, newName: String) {
        viewModelScope.launch {
            val result = repository.rename(item.file, newName.trim())
            if (result.isSuccess) {
                _uiState.update { it.copy(toastMessage = "Renombrado a '$newName'", activeDialog = null) }
                loadFiles()
            } else {
                _uiState.update { it.copy(toastMessage = result.exceptionOrNull()?.message ?: "Error al renombrar") }
            }
        }
    }

    fun deleteFiles(items: List<FileItem>, permanent: Boolean = false) {
        if (!permanent) {
            moveToTrash(items)
            _uiState.update { it.copy(activeDialog = null) }
            return
        }
        viewModelScope.launch {
            val result = repository.delete(items)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        toastMessage = "${result.getOrDefault(0)} elemento(s) eliminado(s)",
                        activeDialog = null,
                        selectedFiles = emptySet()
                    )
                }
                loadFiles()
                loadStorageInfo()
            }
        }
    }

    fun dismissToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    // --- Gestor de Aplicaciones ---
    fun loadInstalledApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAppsLoading = true) }
            val apps = repository.getInstalledApps()
            _uiState.update { it.copy(installedApps = apps, isAppsLoading = false) }
        }
    }

    fun backupApp(app: com.example.explorer.domain.model.AppInfoItem) {
        viewModelScope.launch {
            val downloadDir = File(repository.rootPath, "Download")
            val res = repository.backupAppApk(app, downloadDir)
            if (res.isSuccess) {
                _uiState.update { it.copy(toastMessage = "APK extraído en Descargas: ${res.getOrNull()?.name}") }
            } else {
                _uiState.update { it.copy(toastMessage = "Error al extraer: ${res.exceptionOrNull()?.message}") }
            }
        }
    }

    // --- Editor de Texto ---
    fun openTextEditor(file: File) {
        viewModelScope.launch {
            val res = repository.readTextFile(file)
            if (res.isSuccess) {
                _uiState.update { it.copy(activeDialog = DialogType.TextEditor(file, res.getOrDefault(""))) }
            } else {
                _uiState.update { it.copy(toastMessage = res.exceptionOrNull()?.message ?: "Error al abrir texto") }
            }
        }
    }

    fun saveTextFile(file: File, content: String) {
        viewModelScope.launch {
            val res = repository.writeTextFile(file, content)
            if (res.isSuccess) {
                _uiState.update { it.copy(toastMessage = "Archivo guardado con éxito", activeDialog = null) }
                loadFiles()
            } else {
                _uiState.update { it.copy(toastMessage = "Error al guardar: ${res.exceptionOrNull()?.message}") }
            }
        }
    }

    // --- Compresión y Descompresión ZIP ---
    fun compressFiles(items: List<FileItem>, zipName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, activeDialog = null) }
            val cleanZipName = if (zipName.endsWith(".zip", ignoreCase = true)) zipName else "$zipName.zip"
            val destZip = File(_uiState.value.currentPath, cleanZipName)
            val res = repository.zipFiles(items, destZip)
            if (res.isSuccess) {
                _uiState.update {
                    it.copy(
                        toastMessage = "Archivo ZIP creado: ${res.getOrNull()?.name}",
                        selectedFiles = emptySet()
                    )
                }
            } else {
                _uiState.update { it.copy(toastMessage = "Error al comprimir: ${res.exceptionOrNull()?.message}") }
            }
            loadFiles()
        }
    }

    fun extractZip(zipFile: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val folderName = zipFile.nameWithoutExtension
            val targetDir = File(zipFile.parentFile ?: File(repository.rootPath), folderName)
            val res = repository.extractZip(zipFile, targetDir)
            if (res.isSuccess) {
                _uiState.update { it.copy(toastMessage = "Extraídos ${res.getOrDefault(0)} archivos en /$folderName") }
            } else {
                _uiState.update { it.copy(toastMessage = "Error al extraer: ${res.exceptionOrNull()?.message}") }
            }
            loadFiles()
        }
    }

    // --- Checksum (MD5 y SHA-256) ---
    fun loadChecksums(item: FileItem) {
        viewModelScope.launch {
            val md5Res = repository.calculateChecksum(item.file, "MD5")
            val sha256Res = repository.calculateChecksum(item.file, "SHA-256")
            val currentDialog = _uiState.value.activeDialog
            if (currentDialog is DialogType.FileDetails && currentDialog.item.path == item.path) {
                _uiState.update {
                    it.copy(
                        activeDialog = DialogType.FileDetails(
                            item = item,
                            md5 = md5Res.getOrNull() ?: "Error",
                            sha256 = sha256Res.getOrNull() ?: "Error"
                        )
                    )
                }
            }
        }
    }
}
