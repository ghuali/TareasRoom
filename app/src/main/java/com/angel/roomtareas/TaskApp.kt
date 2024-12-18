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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskApp(database: AppDatabase) {
    val taskDao = database.taskDao()
    val tipoDao = database.tipoDao()
    val scope = rememberCoroutineScope()

    var isEditing by remember { mutableStateOf(false) }
    var isTipoEditing by remember { mutableStateOf(false) }
    var selectedTipo by remember { mutableStateOf<Tipo?>(null) }
    var selectedTask by remember { mutableStateOf<task?>(null) }

    // Estados para tareas y tipos
    var tasks by remember { mutableStateOf(listOf<task>()) }
    var tipos by remember { mutableStateOf(listOf<Tipo>()) }

    // Estados para nuevos valores
    var newTaskName by remember { mutableStateOf("") }
    var newTaskDescription by remember { mutableStateOf("") }
    var newTaskTipoId by remember { mutableStateOf("") }
    var selectedTipoName by remember { mutableStateOf("") }
    var newTipoName by remember { mutableStateOf("") }
    var newTipoid by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

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
            .padding(vertical = 50.dp),
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
            colors = TextFieldDefaults.colors(
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
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFFFFFF),
                focusedContainerColor = Color(0xFFFFFFFF)
            )
        )
        OutlinedTextField(
            value = newTaskDescription,
            onValueChange = { newTaskDescription = it },
            label = { Text("Descripción tarea") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFFFFFF),
                focusedContainerColor = Color(0xFFFFFFFF)
            )
        )

        // Dropdown para seleccionar el tipo
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedTipoName,
                onValueChange = {},
                label = { Text("Seleccionar Tipo") },
                readOnly = true,
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFFFFFF),
                    focusedContainerColor = Color(0xFFFFFFFF)
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                tipos.forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo.name) },
                        onClick = {
                            selectedTipoName = tipo.name
                            newTipoid = tipo.id.toString()
                            expanded = false
                        }
                    )
                }
            }
        }

        Row {
            Button(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        val tipoId = newTipoid.toIntOrNull()
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
                            selectedTipoName = ""
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
        Text("Lista de tareas", fontSize = 22.sp)
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
                            selectedTipoName = tipos.find { it.id == task.id_tipo }?.name ?: ""
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Text(text =
                        "Tarea: ${task.name}, Descripcion: ${task.descripcion}, Tipo: ${tipos.find { it.id == task.id_tipo }?.name ?: "Desconocido"}")
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

        if (isTipoEditing && selectedTipo != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Editar su tipo selecionado: ${selectedTipo?.name}")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    scope.launch(Dispatchers.IO) {
                        selectedTipo?.let {
                            tipoDao.delete(it)
                            tipos = tipoDao.getAllTipos()
                            selectedTipo = null
                            isTipoEditing = false
                        }
                    }
                }, colors = ButtonDefaults.buttonColors(Color(0xFFFF9800))) {
                    Text("Borrar")
                }
                Button(onClick = {
                    scope.launch(Dispatchers.IO) {
                        selectedTipo?.let {
                            val updatedTipo = it.copy(
                                name = newTipoName,
                            )
                            tipoDao.update(updatedTipo)
                            tipos = tipoDao.getAllTipos()
                            selectedTipo = null
                            isTipoEditing = false
                        }
                    }
                },colors = ButtonDefaults.buttonColors(Color(0xFFFF9800))) {
                    Text("Confirmar edición")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar lista de tipos
        Text("Tipo de tareas", fontSize = 22.sp)
        Text("toca para editar")
        tipos.forEach { tipo ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable{
                        selectedTipo = tipo
                        isTipoEditing = true
                        newTipoid = tipo.id.toString()
                        newTipoName = tipo.name
                    }
                    .padding(vertical = 4.dp)
            ){
                Text(text = "Id tipo: ${tipo.id}, Nombre: ${tipo.name},",
                    modifier = Modifier.padding(vertical = 4.dp))
            }


        }
    }
}
