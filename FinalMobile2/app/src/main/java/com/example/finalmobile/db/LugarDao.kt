package com.example.finalmobile.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LugarDao {
    // Obtener todos los lugares
    @Query("SELECT * FROM lugares ORDER BY orden")
    fun getAll(): List<Lugar>
    // Obtener el número de lugares
    @Query("SELECT COUNT(*) FROM lugares")
    fun contar(): Int
    // obtener todos los lugares de un nombre determinado
    @Query("SELECT * FROM lugares WHERE nombre = :nombre")
    fun getByNombre(nombre: String): Lugar?

    // Insertar un nuevo lugar
    @Insert
    fun insertar(lugar: Lugar)

    // Actualizar un lugar
    @Update
    fun actualizar(lugar: Lugar)

    // Eliminar un lugar
    @Delete
    fun eliminar(lugar: Lugar)

    @Query("SELECT * FROM lugares WHERE id = :id")
    fun getById(id: Int): Lugar?
}