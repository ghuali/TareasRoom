package com.angel.roomtareas

import androidx.room.*

@Dao interface
TipoDao { @Insert suspend fun insert(tipo: Tipo)
    @Query("SELECT * FROM tipo")
    suspend fun getAllTtipos(): List<Tipo>
    @Update
    suspend fun update(tipo: Tipo)
    @Delete
    suspend fun delete(tipo: Tipo) }