package com.example.listadetarefas

//Representa cada lista criada pelo usuário
data class TaskList(
    val title: String,      // Nome da lista (Ex: Compras)
    val tasks: MutableList<String> = mutableListOf()        // Itens dentro da lista
)