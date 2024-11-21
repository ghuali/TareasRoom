package com.angel.roomtareas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TaskApp(database: AppDatabase) {
    val taskDao = database.taskDao()
    val tipoDao = database.tipoDao()
    val scope = rememberCoroutineScope()

    // Estados para tareas y tipos
    var tasks by remember { mutableStateOf(listOf<task>()) }
    var tipos by remember { mutableStateOf(listOf<Tipo>()) }

    // Estados para nuevos valores
    var newTaskName by remember { mutableStateOf("") }
    var newTaskDescription by remember { mutableStateOf("") }
    var selectedTipoId by remember { mutableStateOf<Int?>(null) }
    var newTipoName by remember { mutableStateOf("") }

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        tasks = taskDao.getAllTasks()
        tipos = tipoDao.getAllTipos()
    }

    Spacer(modifier = Modifier.height(16.dp))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sección para agregar un nuevo tipo
        Text("Add a New Task Type")
        OutlinedTextField(
            value = newTipoName,
            onValueChange = { newTipoName = it },
            label = { Text("Type Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    val newTipo = Tipo(name = newTipoName)
                    tipoDao.insert(newTipo)
                    tipos = tipoDao.getAllTipos() // Actualizar la lista de tipos
                    newTipoName = ""
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Type")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sección para crear una nueva tarea
        Text("Create a New Task")
        OutlinedTextField(
            value = newTaskName,
            onValueChange = { newTaskName = it },
            label = { Text("Task Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = newTaskDescription,
            onValueChange = { newTaskDescription = it },
            label = { Text("Task Description") },
            modifier = Modifier.fillMaxWidth()
        )

        // Selección de tipo
        DropdownMenu(
            expanded = true,
            onDismissRequest = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            tipos.forEach { tipo ->
                DropdownMenuItem(
                    onClick = { selectedTipoId = tipo.id }
                ) {
                    Text(text = tipo.name)
                }
            }
        }

        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    if (selectedTipoId != null) {
                        val newTask = task(
                            name = newTaskName,
                            descripcion = newTaskDescription,
                            id_tipo = selectedTipoId!!
                        )
                        taskDao.insert(newTask)
                        tasks = taskDao.getAllTasks() // Actualizar la lista de tareas
                        newTaskName = ""
                        newTaskDescription = ""
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Task")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar tareas
        Text("Task List")
        tasks.forEach { task ->
            val tipoName = tipos.find { it.id == task.id_tipo }?.name ?: "Unknown"
            Text(
                text = "Task: ${task.name}, Description: ${task.descripcion}, Type: $tipoName",
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
