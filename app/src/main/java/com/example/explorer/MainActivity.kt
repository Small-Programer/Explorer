package com.example.explorer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.explorer.ui.ActiveScreen
import com.example.explorer.ui.DialogType
import com.example.explorer.ui.ExplorerViewModel
import com.example.explorer.ui.components.AppDialogHost
import com.example.explorer.ui.screens.*
import com.example.explorer.ui.theme.ExplorerTheme
import com.example.explorer.util.FileUtils
import com.example.explorer.util.PermissionUtils

class MainActivity : ComponentActivity() {

    private val viewModel: ExplorerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ExplorerTheme {
                val context = LocalContext.current
                var hasPermission by remember { mutableStateOf(PermissionUtils.hasStoragePermission(context)) }

                LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                    val granted = PermissionUtils.hasStoragePermission(context)
                    hasPermission = granted
                    if (granted) {
                        viewModel.loadFiles()
                        viewModel.loadStorageInfo()
                        viewModel.loadRecentFiles()
                        viewModel.loadCleanStats()
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!hasPermission) {
                        PermissionScreen(
                            onRequestPermission = {
                                PermissionUtils.requestStoragePermission(this@MainActivity)
                            }
                        )
                    } else {
                        MainAppContent(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: ExplorerViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(1) } // Default tab: Explorar (Google style)

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.dismissToast()
        }
    }

    // Manejo de botón Atrás
    BackHandler(enabled = true) {
        when {
            uiState.selectedCategory != null -> viewModel.closeCategory()
            uiState.activeScreen == ActiveScreen.TRASH ||
            uiState.activeScreen == ActiveScreen.SAFE_FOLDER ||
            uiState.activeScreen == ActiveScreen.FAVORITES ||
            uiState.activeScreen == ActiveScreen.APP_MANAGER -> {
                viewModel.setScreen(ActiveScreen.BROWSE)
            }
            uiState.isSelectionMode -> viewModel.clearSelection()
            uiState.isSearchActive -> viewModel.setSearchActive(false)
            uiState.activeScreen == ActiveScreen.EXPLORER_FOLDER -> {
                val handled = viewModel.navigateUp()
                if (!handled) {
                    viewModel.setScreen(ActiveScreen.BROWSE)
                }
            }
            selectedTab != 1 -> selectedTab = 1
            else -> (context as? ComponentActivity)?.finish()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (uiState.selectedCategory == null &&
                !uiState.isSelectionMode &&
                uiState.activeScreen != ActiveScreen.EXPLORER_FOLDER &&
                uiState.activeScreen != ActiveScreen.TRASH &&
                uiState.activeScreen != ActiveScreen.SAFE_FOLDER &&
                uiState.activeScreen != ActiveScreen.FAVORITES &&
                uiState.activeScreen != ActiveScreen.APP_MANAGER) {

                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            viewModel.setScreen(ActiveScreen.CLEAN)
                        },
                        icon = { Icon(Icons.Default.CleaningServices, contentDescription = "Limpiar") },
                        label = { Text("Limpiar") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            viewModel.setScreen(ActiveScreen.BROWSE)
                        },
                        icon = { Icon(Icons.Default.Explore, contentDescription = "Explorar") },
                        label = { Text("Explorar") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = {
                            selectedTab = 2
                            viewModel.setScreen(ActiveScreen.SHARE)
                        },
                        icon = { Icon(Icons.Default.Share, contentDescription = "Compartir") },
                        label = { Text("Compartir") }
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        // 1. Pantalla de categoría activa
        if (uiState.selectedCategory != null) {
            CategoryScreen(
                category = uiState.selectedCategory!!,
                files = uiState.categoryFiles,
                isLoading = uiState.isCategoryLoading,
                onBackClick = { viewModel.closeCategory() },
                onFileClick = { item ->
                    if (item.isDirectory) {
                        viewModel.navigateTo(item.path)
                        viewModel.closeCategory()
                    } else {
                        FileUtils.openFile(context, item)
                    }
                },
                onMoreClick = { item -> viewModel.showDialog(DialogType.FileDetails(item)) },
                modifier = modifier
            )
        }
        // 2. Pantalla de Colección: Papelera
        else if (uiState.activeScreen == ActiveScreen.TRASH) {
            CollectionsScreen(
                type = CollectionType.TRASH,
                files = uiState.trashFiles,
                onBackClick = { viewModel.setScreen(ActiveScreen.BROWSE) },
                onFileClick = { item -> FileUtils.openFile(context, item) },
                onActionClick = { item -> viewModel.restoreTrashItem(item) },
                onEmptyTrashClick = { viewModel.emptyTrash() },
                modifier = modifier
            )
        }
        // 3. Pantalla de Colección: Carpeta Segura
        else if (uiState.activeScreen == ActiveScreen.SAFE_FOLDER) {
            CollectionsScreen(
                type = CollectionType.SAFE_FOLDER,
                files = uiState.safeFolderFiles,
                onBackClick = { viewModel.setScreen(ActiveScreen.BROWSE) },
                onFileClick = { item -> FileUtils.openFile(context, item) },
                onActionClick = { item -> viewModel.removeFromSafeFolder(item) },
                modifier = modifier
            )
        }
        // 4. Pantalla de Colección: Favoritos
        else if (uiState.activeScreen == ActiveScreen.FAVORITES) {
            CollectionsScreen(
                type = CollectionType.FAVORITES,
                files = uiState.favoriteFiles,
                onBackClick = { viewModel.setScreen(ActiveScreen.BROWSE) },
                onFileClick = { item -> FileUtils.openFile(context, item) },
                onActionClick = {},
                modifier = modifier
            )
        }
        // 5. Gestor de Aplicaciones (App Manager)
        else if (uiState.activeScreen == ActiveScreen.APP_MANAGER) {
            AppManagerScreen(
                apps = uiState.installedApps,
                isLoading = uiState.isAppsLoading,
                onBackClick = { viewModel.setScreen(ActiveScreen.BROWSE) },
                onBackupApp = { app -> viewModel.backupApp(app) },
                modifier = modifier
            )
        }
        // 5. Explorador de carpetas
        else if (uiState.activeScreen == ActiveScreen.EXPLORER_FOLDER) {
            ExplorerScreen(
                currentPath = uiState.currentPath,
                breadcrumbs = uiState.breadcrumbs,
                files = uiState.files,
                isLoading = uiState.isLoading,
                selectedFiles = uiState.selectedFiles,
                isGridView = uiState.isGridView,
                isSearchActive = uiState.isSearchActive,
                searchQuery = uiState.searchQuery,
                searchResults = uiState.searchResults,
                clipboard = uiState.clipboard,
                onNavigateBack = {
                    val handled = viewModel.navigateUp()
                    if (!handled) viewModel.setScreen(ActiveScreen.BROWSE)
                },
                onBreadcrumbClick = { path -> viewModel.navigateTo(path) },
                onFileClick = { item ->
                    if (item.isDirectory) {
                        viewModel.navigateTo(item.path)
                    } else if (item.isZipArchive) {
                        viewModel.extractZip(item.file)
                    } else if (item.isTextFile) {
                        viewModel.openTextEditor(item.file)
                    } else {
                        FileUtils.openFile(context, item)
                    }
                },
                onFileLongClick = { item -> viewModel.toggleSelection(item) },
                onMoreClick = { item -> viewModel.showDialog(DialogType.FileDetails(item)) },
                onToggleViewMode = { viewModel.toggleViewMode() },
                onOpenSortDialog = { viewModel.showDialog(DialogType.SortPicker) },
                onOpenCreateFolderDialog = { viewModel.showDialog(DialogType.CreateFolder) },
                onSearchQueryChanged = { query -> viewModel.onSearchQueryChanged(query) },
                onSearchToggle = { viewModel.setSearchActive(!uiState.isSearchActive) },
                onPasteClipboard = { viewModel.pasteClipboard() },
                onCancelClipboard = { viewModel.clearClipboard() },
                onCopySelected = { viewModel.copySelected() },
                onCutSelected = { viewModel.cutSelected() },
                onCompressSelected = {
                    viewModel.showDialog(DialogType.CompressZip(uiState.selectedFiles.toList()))
                },
                onDeleteSelected = {
                    viewModel.showDialog(DialogType.DeleteConfirm(uiState.selectedFiles.toList()))
                },
                onShareSelected = {
                    FileUtils.shareFiles(context, uiState.selectedFiles.toList())
                    viewModel.clearSelection()
                },
                onSelectAll = { viewModel.selectAll() },
                onClearSelection = { viewModel.clearSelection() },
                modifier = modifier
            )
        }
        // 6. Navegación por pestañas principales (Limpiar, Explorar, Compartir)
        else {
            when (selectedTab) {
                0 -> CleanScreen(
                    storageInfo = uiState.storageInfo,
                    junkCount = uiState.junkFilesCount,
                    largeFiles = uiState.largeFiles,
                    trashCount = uiState.trashFiles.size,
                    isCleaning = uiState.isCleaning,
                    onCleanJunk = { viewModel.cleanJunk() },
                    onOpenTrash = { viewModel.setScreen(ActiveScreen.TRASH) },
                    onFileClick = { item -> FileUtils.openFile(context, item) },
                    modifier = modifier
                )
                1 -> HomeScreen(
                    storageInfo = uiState.storageInfo,
                    recentFiles = uiState.recentFiles,
                    onCategoryClick = { cat -> viewModel.openCategory(cat) },
                    onFileClick = { item ->
                        if (item.isDirectory) {
                            viewModel.navigateTo(item.path)
                        } else if (item.isZipArchive) {
                            viewModel.extractZip(item.file)
                        } else if (item.isTextFile) {
                            viewModel.openTextEditor(item.file)
                        } else {
                            FileUtils.openFile(context, item)
                        }
                    },
                    onOpenStorageClick = {
                        selectedTab = 0
                        viewModel.setScreen(ActiveScreen.CLEAN)
                    },
                    onShortcutClick = { path -> viewModel.navigateTo(path) },
                    onSearchClick = {
                        viewModel.navigateTo(viewModel.uiState.value.currentPath)
                        viewModel.setSearchActive(true)
                    },
                    onFavoritesClick = { viewModel.setScreen(ActiveScreen.FAVORITES) },
                    onSafeFolderClick = { viewModel.setScreen(ActiveScreen.SAFE_FOLDER) },
                    onTrashClick = { viewModel.setScreen(ActiveScreen.TRASH) },
                    onAppsClick = { viewModel.setScreen(ActiveScreen.APP_MANAGER) },
                    onInternalStorageClick = { viewModel.navigateTo(viewModel.uiState.value.currentPath) },
                    onMoreClick = { item -> viewModel.showDialog(DialogType.FileDetails(item)) },
                    modifier = modifier
                )
                2 -> ShareScreen(
                    onSendClick = {
                        viewModel.navigateTo(viewModel.uiState.value.currentPath)
                        Toast.makeText(context, "Selecciona los archivos que deseas enviar", Toast.LENGTH_SHORT).show()
                    },
                    onReceiveClick = {
                        Toast.makeText(context, "Listo para recibir con Quick Share", Toast.LENGTH_SHORT).show()
                    },
                    modifier = modifier
                )
            }
        }
    }

    AppDialogHost(
        dialogType = uiState.activeDialog,
        onDismiss = { viewModel.showDialog(null) },
        onCreateFolder = { name -> viewModel.createFolder(name) },
        onRename = { item, newName -> viewModel.renameFile(item, newName) },
        onDeleteConfirm = { items -> viewModel.deleteFiles(items) },
        onSetSafePin = { pin -> viewModel.setSafePin(pin) },
        onEnterSafePin = { pin -> viewModel.unlockSafeFolder(pin) },
        onCompress = { items, zipName -> viewModel.compressFiles(items, zipName) },
        onSaveTextFile = { file, content -> viewModel.saveTextFile(file, content) },
        onCalculateChecksum = { item -> viewModel.loadChecksums(item) },
        currentSortOrder = uiState.sortOrder,
        onSelectSortOrder = { order -> viewModel.setSortOrder(order) }
    )
}
