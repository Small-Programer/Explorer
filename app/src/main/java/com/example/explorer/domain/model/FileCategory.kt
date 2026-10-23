package com.example.explorer.domain.model

enum class FileCategory(val title: String) {
    ALL("Todos"),
    IMAGE("Imágenes"),
    VIDEO("Videos"),
    AUDIO("Audio"),
    DOCUMENT("Documentos"),
    ARCHIVE("Comprimidos"),
    APK("Instaladores APK"),
    DOWNLOAD("Descargas"),
    OTHER("Otros")
}
