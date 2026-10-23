package com.example.explorer.domain.model

enum class SortOrder(val label: String) {
    NAME_ASC("Nombre (A-Z)"),
    NAME_DESC("Nombre (Z-A)"),
    DATE_DESC("Más reciente primero"),
    DATE_ASC("Más antiguo primero"),
    SIZE_DESC("Mayor tamaño primero"),
    SIZE_ASC("Menor tamaño primero"),
    TYPE_ASC("Tipo / Extensión")
}
