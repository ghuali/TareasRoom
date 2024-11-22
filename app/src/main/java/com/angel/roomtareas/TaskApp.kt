package com.angel.roomtareas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

    var isEditing by remember { mutableStateOf(false) }
    // Estados para tareas y tipos
    var tasks by remember { mutableStateOf(listOf<task>()) }
    var tipos by remember { mutableStateOf(listOf<Tipo>()) }

    // Estados para nuevos valores
    var newTaskName by remember { mutableStateOf("") }
    var newTaskDescription by remember { mutableStateOf("") }
    var newTaskTipoId by remember { mutableStateOf("") }
    var newTipoName by remember { mutableStateOf("") }

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        tasks = taskDao.getAllTasks()
        tipos = tipoDao.getAllTipos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sección para agregar un nuevo tipo
        Text("Añadir nuevo tipo")
        OutlinedTextField(
            value = newTipoName,
            onValueChange = { newTipoName = it },
            label = { Text("nombre del tipo") },
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
            Text("Añadir tipo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sección para crear una nueva tarea
        Text("Crear una nueva tarea")
        OutlinedTextField(
            value = newTaskName,
            onValueChange = { newTaskName = it },
            label = { Text("nombre tarea") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = newTaskDescription,
            onValueChange = { newTaskDescription = it },
            label = { Text("Descripcion tarea") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = newTaskTipoId,
            onValueChange = { newTaskTipoId = it },
            label = { Text("Tipo ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Row {
            Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    val tipoId = newTaskTipoId.toIntOrNull()
                    if (tipoId != null) {
                        val newTask = task(
                            name = newTaskName,
                            descripcion = newTaskDescription,
                            id_tipo = tipoId
                        )
                        taskDao.insert(newTask)
                        tasks = taskDao.getAllTasks() // Actualizar la lista de tareas
                        newTaskName = ""
                        newTaskDescription = ""
                        newTaskTipoId = ""
                    }
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Add Task")
        }
        Button(onClick = {}, modifier = Modifier.padding(8.dp))
        {
            Text("Delete")
        }
        Button(onClick = {}, modifier = Modifier.padding(8.dp))
        { Text("Edit") }
        }



        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar lista de tareas
        Text("Task List")
            tasks.forEach { task ->
                Text(
                    text = "Task: ${task.name}, Description: ${task.descripcion}, Type ID: ${task.id_tipo}",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar lista de tipos
        Text("Task Types")
        tipos.forEach { tipo ->
            Text(
                text = "Type ID: ${tipo.id}, Name: ${tipo.name},",
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}