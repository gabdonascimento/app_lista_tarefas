package com.example.listadetarefas

// Importações necessárias
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Activity principal do app, mostra todas as listas de tarefas
class HomeActivity : AppCompatActivity() {

    // Lista que armazena todas as listas de tarefas criadas
    private val taskLists = mutableListOf<TaskList>()

    // Componentes da interface
    private lateinit var recyclerView: RecyclerView     // Para exibir as listas
    private lateinit var adapter: TaskListAdapter       // Adapter do RecyclerView
    private lateinit var taskListLauncher: ActivityResultLauncher<Intent>       // Para abrir TaskListActivity e receber o resultado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Conecta a RecyclerView e botão flutuante "+"
        recyclerView = findViewById(R.id.recyclerViewLists)
        val fabAddList = findViewById<FloatingActionButton>(R.id.fabAddList)

        // Inicializa o ActivityResultLauncher
        taskListLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {       // Verifica se a lista foi salva
                val data = result.data
                val title = data?.getStringExtra("LIST_TITLE") ?: return@registerForActivityResult
                val tasks = data.getStringArrayListExtra("TASKS") ?: arrayListOf()
                val newList = TaskList(title, tasks.toMutableList())
                taskLists.add(newList)          // Adiciona nova lista na lista principal
                adapter.notifyDataSetChanged()      // Atualiza RecyclerView
            }
        }

        // Configuração RecyclerView
        adapter = TaskListAdapter(taskLists) { selectedList ->
            // Ao clicar em uma lista existente, abre a TaskListActivity para edição
            val intent = Intent(this, TaskListActivity::class.java)
            intent.putExtra("LIST_TITLE", selectedList.title)       // Passa o título
            intent.putStringArrayListExtra("TASKS", ArrayList(selectedList.tasks))      // Passa tarefas
            taskListLauncher.launch(intent)                                 // Abre a Activity
        }

        recyclerView.layoutManager = LinearLayoutManager(this)      // Layout vertical
        recyclerView.adapter = adapter

        // Botão flutuante "+" para criar nova lista
        fabAddList.setOnClickListener {
            val editText = EditText(this)
            editText.hint = "Nome da lista"

            // Mostra um AlertDialog para o usuário digitar o nome da nova lista
            AlertDialog.Builder(this)
                .setTitle("Criar nova lista")
                .setView(editText)
                .setPositiveButton("Criar") { _, _ ->
                    val listTitle = editText.text.toString()
                    if (listTitle.isNotEmpty()) {
                        val intent = Intent(this, TaskListActivity::class.java)
                        intent.putExtra("LIST_TITLE", listTitle)
                        taskListLauncher.launch(intent)     // Abre a TaskListActivity
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}

/*
    Resumo da HomeActivity

    * RecyclerView -> Lista todas as listas criadas
    * TaskListAdapter -> Conecta taskLists ao RecyclerView
    * FloatingActionButton("+") -> Cria novas listas com o título digitado
    * ActivityResultLauncher -> Substitui startActivityForResult e recebe os dados salvos da TaskListActivity
    * AlertDialog -> Caixa de diálogo que permite digitar o nome da nova lista
 */
