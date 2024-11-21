package com.angel.roomtareas

import androidx.room.*

@Dao interface
TaskDao { @Insert suspend fun insert(task: task)
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<task>
    @Update
    suspend fun update(task: task)
    @Delete
    suspend fun delete(task: task) }