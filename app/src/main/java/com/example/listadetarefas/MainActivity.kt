//Pacote onde a classe está localizada
package com.example.listadetarefas

import android.os.Bundle
import android.widget.*     //Importa componentes de interface como EditText, Button, ListView, Toast
import androidx.appcompat.app.AppCompatActivity     //Importa a classe base AppCompatActivity, usada para criar telas modernas no Android

//Declaração da classe MainActivity, que representa a tela principal do app
class MainActivity : AppCompatActivity() {
    //Declaração das variáveis que vão se conectar com os elementos da interface
    private lateinit var editTextTask: EditText
    private lateinit var buttonAdd: Button
    private lateinit var listViewTasks: ListView

    // Lista para armazenar as tarefas em memória (Ex: ArrayList de Strings)
    private val tasks = ArrayList<String>()

    //Adaptador: responsável por ligar a lista de dados (tasks) com a ListView
    private lateinit var adapter: ArrayAdapter<String>

    //Metodo chamado automaticamente quando a tela é criada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Conecta as váriaveis Kotlin com elementos XML através do ID
        editTextTask = findViewById(R.id.editTextTask)
        buttonAdd = findViewById(R.id.buttonAdd)
        listViewTasks = findViewById(R.id.listViewTasks)

        // Cria o adaptador dizendo que a lista será exibida
        // (this = contexto da activity, layout simples de item da lista. Dados = tasks)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)

        // Conecta o adaptador com a ListView
        listViewTasks.adapter = adapter

        // Ação do botão Adicionar
        buttonAdd.setOnClickListener {

            // Pega o texto digitado no EditText e converte para String
            val task = editTextTask.text.toString()

            // Verifica se o campo não está vazio
            if (task.isNotEmpty()) {
                tasks.add(task)                    // Adiciona a tarefa na lista
                adapter.notifyDataSetChanged()     // Atualiza a ListView para exibir a nova tarefa
                editTextTask.text.clear()          // Limpa o campo de texto
            } else {
                // Se o campo estiver vazio, mostra uma mensagem rápida na tela (Toast)
                Toast.makeText(this, "Digite uma tarefa!", Toast.LENGTH_SHORT).show()
            }
        }

        // Define a ação ao clicar em um item da lista
        listViewTasks.setOnItemClickListener { _, _, position, _ ->
            tasks.removeAt(position) //Remove o item clicado da lista
            adapter.notifyDataSetChanged()  //Atualiza a ListView
        }
    }
}
