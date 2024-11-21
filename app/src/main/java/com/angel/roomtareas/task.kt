package com.angel.roomtareas

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Tipo::class,
            parentColumns = ["id"],
            childColumns = ["id_tipo"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val descripcion: String,
    val id_tipo: Int
)
