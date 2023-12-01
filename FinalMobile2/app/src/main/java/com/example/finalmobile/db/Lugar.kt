package com.example.finalmobile.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lugares")
data class Lugar(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var nombre: String,
    var imagenReferencial: String,
    var latitud: String,
    var orden: Int,
    var costoNoche: Int,
    var costoTraslado: Int,
    var comentarios: String
)
