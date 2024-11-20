package com.example.todo


data class Task(
    val name: String = "",
    val category: String = "", // Aquí almacenaremos el nombre como texto
    var isSelected: Boolean = false
)