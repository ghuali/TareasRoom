package com.angel.roomtareas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun TaskApp(database: AppDatabase) {
    val taskDao = database.taskDao()
    val tipoDao = database.tipoDao()
    val scope = rememberCoroutineScope()

    var isEditing by remember { mutableStateOf(false) }
    var selectedTipo by remember { mutableStateOf<Tipo?>(null) }
    var selectedTask by remember { mutableStateOf<task?>(null) }

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
            .background(color = Color(0xFF2196F3))
            .padding(16.dp)
            .padding(vertical = 50.dp)
            ,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sección para agregar un nuevo tipo
        Text("Añadir nuevo tipo", fontSize = 20.sp)
        OutlinedTextField(
            value = newTipoName,
            onValueChange = { newTipoName = it },
            label = { Text("Nombre del tipo") },
            modifier = Modifier.fillMaxWidth(),
            colors =  TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFFFFFF),
                focusedContainerColor = Color(0xFFFFFFFF)
            )
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
            modifier = Modifier.padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFFF9800))
        ) {
            Text("Añadir tipo")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sección para crear una nueva tarea
        Text("Crear una nueva tarea", fontSize = 20.sp)
        OutlinedTextField(
            value = newTaskName,
            onValueChange = { newTaskName = it },
            label = { Text("Nombre tarea") },
            modifier = Modifier.fillMaxWidth(),
            colors =  TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFFFFFF),
                focusedContainerColor = Color(0xFFFFFFFF)
            )
        )
        OutlinedTextField(
            value = newTaskDescription,
            onValueChange = { newTaskDescription = it },
            label = { Text("Descripcion tarea") },
            modifier = Modifier.fillMaxWidth(),
            colors =  TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFFFFFF),
                focusedContainerColor = Color(0xFFFFFFFF)
            )
        )
        OutlinedTextField(
            value = newTaskTipoId,
            onValueChange = { newTaskTipoId = it },
            label = { Text("Tipo ID") },
            modifier = Modifier.fillMaxWidth(),
            colors =  TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFFFFFF),
                focusedContainerColor = Color(0xFFFFFFFF)
            )
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
                modifier = Modifier.padding(8.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFFF9800))
            ) {
                Text("Añadir")
            }
        }



        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar lista de tareas
        Text("Lista de tareas", fontSize = 24.9.sp)
        Text(("Toca para editar"))
            tasks.forEach { task ->
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedTask = task
                            isEditing = true
                            newTaskName = task.name
                            newTaskDescription = task.descripcion
                            newTaskTipoId = task.id_tipo.toString()
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Text(text =
                        "Tarea: ${task.name}, Descripcion: ${task.descripcion}, Tipo ID: ${task.id_tipo}")
                }
            }

        // voy a mostrar los botones de Borrar y editar cuando is editing sea true
        if (isEditing && selectedTask != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Editar su tarea selecionada: ${selectedTask?.name}")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    scope.launch(Dispatchers.IO) {
                        selectedTask?.let {
                            taskDao.delete(it)
                            tasks = taskDao.getAllTasks()
                            selectedTask = null
                            isEditing = false
                        }
                    }
                }, colors = ButtonDefaults.buttonColors(Color(0xFFFF9800))) {
                    Text("Borrar")
                }
                Button(onClick = {
                    scope.launch(Dispatchers.IO) {
                        selectedTask?.let {
                            val updatedTask = it.copy(
                                name = newTaskName,
                                descripcion = newTaskDescription,
                                id_tipo = newTaskTipoId.toIntOrNull() ?: it.id_tipo
                            )
                            taskDao.update(updatedTask)
                            tasks = taskDao.getAllTasks()
                            selectedTask = null
                            isEditing = false
                        }
                    }
                },colors = ButtonDefaults.buttonColors(Color(0xFFFF9800))) {
                    Text("Confirmar edición")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar lista de tipos
        Text("Tipo de tareas")
        Text("toca para editar")
        tipos.forEach { tipo ->
            Text(
                text = "Id tipo: ${tipo.id}, Nombre: ${tipo.name},",
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
