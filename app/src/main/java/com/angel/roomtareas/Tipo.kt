package com.angel.roomtareas

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "tipo")
data class Tipo( @PrimaryKey(autoGenerate = true)
                 val id: Int = 0,
                 val name: String,)