package com.example.listadetarefas

// Importações necessárias para usar componentes do Android
import android.content.Intent             // Para enviar dados de volta à HomeActivity
import android.os.Bundle                 // Para receber o estado da Activity
import android.widget.*                  // EditText, Button, Toast, etc.
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Classe que representa a tela de cada lista de tarefas
class TaskListActivity : AppCompatActivity() {

    // Declaração de variáveis que correspondem aos elementos da interface
    private lateinit var editTextTask: EditText        // Campo de texto para digitar nova tarefa
    private lateinit var buttonAdd: Button             // Botão para adicionar a tarefa digitada
    private lateinit var recyclerViewTasks: RecyclerView  // RecyclerView para mostrar a lista de tarefas
    private lateinit var buttonSave: Button            // Botão para salvar a lista completa
    private lateinit var buttonCancel: Button          // Botão para cancelar/voltar à tela inicial

    // Lista de tarefas dessa lista específica
    private val tasks = mutableListOf<String>()        // MutableList para permitir adição e remoção
    private lateinit var adapter: TaskItemAdapter     // Adapter customizado para cada item da lista
    private var listTitle: String = ""                // Título da lista (ex: "Compras")

    // Método chamado quando a Activity é criada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)  // Define o layout XML específico para essa Activity

        // Recebe o título da lista enviado da HomeActivity.
        // Se não houver título, usa "Nova Lista"
        listTitle = intent.getStringExtra("LIST_TITLE") ?: "Nova Lista"

        // Recebe tarefas existentes, caso esteja editando uma lista já criada
        val existingTasks = intent.getStringArrayListExtra("TASKS") ?: arrayListOf()
        tasks.addAll(existingTasks)    // Adiciona as tarefas recebidas à lista local

        // Define o título da Activity (barra superior) como o título da lista
        title = listTitle

        // Conecta as variáveis Kotlin com os elementos do XML
        editTextTask = findViewById(R.id.editTextTask)
        buttonAdd = findViewById(R.id.buttonAdd)
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks)
        buttonSave = findViewById(R.id.buttonSave)
        buttonCancel = findViewById(R.id.buttonCancel)

        // Configura o RecyclerView com o Adapter customizado
        // onDeleteClick: função que define o que acontece ao clicar na lixeira
        adapter = TaskItemAdapter(tasks) { position ->
            tasks.removeAt(position)            // Remove o item clicado na posição
            adapter.notifyDataSetChanged()      // Atualiza a RecyclerView para refletir a remoção
        }

        // Define o layout do RecyclerView como Linear (vertical)
        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewTasks.adapter = adapter

        // Configura o botão "Adicionar"
        buttonAdd.setOnClickListener {
            val task = editTextTask.text.toString()    // Pega o texto digitado no EditText
            if (task.isNotEmpty()) {                   // Verifica se o campo não está vazio
                tasks.add(task)                        // Adiciona a tarefa à lista
                adapter.notifyDataSetChanged()         // Atualiza a RecyclerView
                editTextTask.text.clear()              // Limpa o campo para digitar nova tarefa
            } else {
                // Mostra mensagem na tela se o campo estiver vazio
                Toast.makeText(this, "Digite uma tarefa!", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura o botão "Salvar"
        buttonSave.setOnClickListener {
            val resultIntent = Intent()                // Cria um Intent para enviar dados de volta
            resultIntent.putExtra("LIST_TITLE", listTitle)              // Envia o título
            resultIntent.putStringArrayListExtra("TASKS", ArrayList(tasks)) // Envia todas as tarefas
            setResult(RESULT_OK, resultIntent)        // Define o resultado como OK
            finish()                                  // Fecha a Activity e volta para HomeActivity
        }

        // Configura o botão "Cancelar" (voltar para HomeActivity sem salvar)
        buttonCancel.setOnClickListener {
            finish()                                  // Fecha a Activity sem enviar dados
        }
    }
}

/*
    Resumo
    * EditText -> Onde o usuário digita cada tarefa
    * ListView -> Exibe todas as tarefas da lista
    * buttonAdd -> Adiciona uma tarefa à lista
    * butonSave -> Envia a lista completa de volta à HomeActivity
    * ArrayAdapter -> "Conecta" a lista de tarefas ao ListView para exibição
    * Intent -> Permite enviar dados de volta para outra Activity (HomeActivity)
    * RecyclerView + Adapter customizado -> Permite adicionar icone de lixeira em cada item
    * Botão Cancelar -> Fecha a Activity, mantendo o app mais amigável
    * MutableList de tarefas -> Facilita adicionar/remover itens dinamicamente
    * Intent para enviar resultado -> setResult() envia dados de volta à HomeActivity quando o usuário salva
    * notifyDataSetChanged() -> Atualiza o RecyclerView sempre que a lista muda
 */