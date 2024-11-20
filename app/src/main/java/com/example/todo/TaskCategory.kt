package com.example.todo

sealed class TaskCategory(var isSelected: Boolean = true) {
    object Personal : TaskCategory()
    object Business : TaskCategory()
    object Other : TaskCategory()

    companion object {
        // Método para convertir un string en un objeto TaskCategory
        fun fromString(category: String): TaskCategory {
            return when (category) {
                "Personal" -> Personal
                "Business" -> Business
                else -> Other
            }
        }
    }

    // Método para obtener el nombre de la categoría
    override fun toString(): String {
        return when (this) {
            is Personal -> "Personal"
            is Business -> "Business"
            is Other -> "Other"
        }
    }
}
