# 📁 Explorer - Gestor de Archivos para Android

Una aplicación gestora de archivos moderna, rápida y gratuita para Android, diseñada con la estética de **Material You (Material 3 Expressive)** inspirada en *Files by Google*.

---

## ✨ Características Principales

### 🧹 1. Pestaña Limpiar (Clean)
* **Analizador de Almacenamiento:** Gráfico de espacio ocupado, libre y total en tiempo real.
* **Archivos Basura:** Detección y limpieza de archivos temporales (`.tmp`, `.log` y archivos residuales).
* **Papelera de Reciclaje:** Gestión y vaciado seguro de archivos eliminados.
* **Archivos Grandes:** Identificador rápido de archivos de gran volumen (+50 MB).

### 🧭 2. Pestaña Explorar (Browse)
* **Barra de Búsqueda:** Búsqueda rápida de archivos y carpetas en tiempo real.
* **Carrusel de Recientes:** Vista previa horizontal de imágenes y videos recientes.
* **Categorías Rápidas:** Acceso directo a Descargas, Imágenes, Videos, Audio, Documentos y APKs.
* **Colecciones Especiales:**
  * ⭐ **Favoritos:** Destaca archivos importantes para acceso inmediato.
  * 🔒 **Carpeta Segura (Safe Folder):** Protección con **PIN numérico** para documentos y fotos privadas.
  * 🗑️ **Papelera:** Recuperación o eliminación definitiva de archivos.
* **Explorador de Carpetas:** Navegación por directorios con migas de pan (*breadcrumbs*), modos lista y cuadrícula, ordenamiento múltiple y creación de carpetas.

### 📡 3. Pestaña Compartir (Quick Share)
* Interfaz para transferir archivos a otros dispositivos cercanos.

---

## 🛠️ Stack Tecnológico
* **Lenguaje:** Kotlin
* **UI:** Jetpack Compose (Material 3)
* **Arquitectura:** Clean Architecture + MVVM con `StateFlow`
* **Concurrencia:** Kotlin Coroutines (`Dispatchers.IO`)
* **Carga de Imágenes:** Coil Compose
* **Compatibilidad:** Android 8.0 (API 26) hasta Android 15+

---

## 🚀 Instalación
Puedes compilar el proyecto directamente con Android Studio o generar el APK con:
```bash
./gradlew assembleDebug
```
El instalador se generará en `app/build/outputs/apk/debug/app-debug.apk`.
