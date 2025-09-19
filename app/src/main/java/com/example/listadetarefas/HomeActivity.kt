package com.example.listadetarefas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration

class HomeActivity : AppCompatActivity() {

    private val taskLists = mutableListOf<TaskList>()  // Lista de listas
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskListAdapter
    private lateinit var taskListLauncher: ActivityResultLauncher<Intent>
    private lateinit var emptyView: TextView

    // SharedPreferences para persistência
    private lateinit var sharedPrefs: SharedPreferences
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recyclerViewLists)
        emptyView = findViewById(R.id.emptyView)
        val fabAddList = findViewById<FloatingActionButton>(R.id.fabAddList)

        // Inicializa SharedPreferences
        sharedPrefs = getSharedPreferences("TaskPrefs", MODE_PRIVATE)

        // Carrega listas salvas
        loadTaskLists()

        // Atualiza a view vazia após carregar as listas
        updateEmptyView()

        // Inicializa o ActivityResultLauncher
        taskListLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val title = data?.getStringExtra("LIST_TITLE") ?: return@registerForActivityResult
                val tasks = data.getStringArrayListExtra("TASKS") ?: arrayListOf()

                // Verifica se já existe lista com o mesmo título (atualiza em vez de duplicar)
                val existing = taskLists.find { it.title == title }
                if (existing != null) {
                    existing.tasks.clear()
                    existing.tasks.addAll(tasks)
                } else {
                    val newList = TaskList(title, tasks.toMutableList())
                    taskLists.add(newList)
                }

                saveTaskLists()
                adapter.notifyDataSetChanged()

                // Atualiza a view vazia após a alteração
                updateEmptyView()
            }
        }

        // Configuração RecyclerView
        adapter = TaskListAdapter(
            taskLists,
            onItemClick = { selectedList ->
                // Abre a lista
                val intent = Intent(this, TaskListActivity::class.java)
                intent.putExtra("LIST_TITLE", selectedList.title)
                intent.putStringArrayListExtra("TASKS", ArrayList(selectedList.tasks))
                taskListLauncher.launch(intent)
            },
            onDeleteClick = { selectedList ->
                // Confirmação antes de excluir
                AlertDialog.Builder(this)
                    .setTitle("Excluir lista")
                    .setMessage("Deseja excluir '${selectedList.title}'?")
                    .setPositiveButton("Excluir") { _, _ ->
                        taskLists.remove(selectedList)
                        saveTaskLists()              // Atualiza persistência
                        adapter.notifyDataSetChanged()

                        // Atualiza a view vazia após a exclusão
                        updateEmptyView()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Adicionar linhas entre os itens
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(divider)

        // Botão "+"
        fabAddList.setOnClickListener {
            val editText = EditText(this)
            editText.hint = "Nome da lista"

            AlertDialog.Builder(this)
                .setTitle("Criar nova lista")
                .setView(editText)
                .setPositiveButton("Criar") { _, _ ->
                    val listTitle = editText.text.toString().trim()     // Remove espaços desnecessários
                    if (listTitle.isNotEmpty()) {
                        val intent = Intent(this, TaskListActivity::class.java)
                        intent.putExtra("LIST_TITLE", listTitle)
                        taskListLauncher.launch(intent)
                    } else {
                        // Impede de criar uma lista vazia
                        editText.error = "Informe um nome para a lista"
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    // Metodo chamado para mostrar ou esconder a mensagem de uma lista vazia
    private fun updateEmptyView() {
        emptyView.visibility = if (taskLists.isEmpty()) View.VISIBLE else View.GONE
    }


    // -------- Persistência --------
    private fun saveTaskLists() {
        val json = gson.toJson(taskLists)
        sharedPrefs.edit().putString("task_lists", json).apply()
    }

    private fun loadTaskLists() {
        val json = sharedPrefs.getString("task_lists", null) ?: return
        val type = object : TypeToken<MutableList<TaskList>>() {}.type
        val savedLists: MutableList<TaskList> = gson.fromJson(json, type)
        taskLists.clear()
        taskLists.addAll(savedLists)
    }
}
