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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.view.View

class HomeActivity : AppCompatActivity() {

    private val taskLists = mutableListOf<TaskList>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskListAdapter
    private lateinit var taskListLauncher: ActivityResultLauncher<Intent>
    private lateinit var emptyView: TextView

    private lateinit var sharedPrefs: SharedPreferences
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recyclerViewLists)
        emptyView = findViewById(R.id.emptyView)
        val fabAddList = findViewById<FloatingActionButton>(R.id.fabAddList)

        sharedPrefs = getSharedPreferences("TaskPrefs", MODE_PRIVATE)

        // Carrega listas existentes
        loadTaskLists()
        updateEmptyView()

        // Inicializa ActivityResultLauncher
        taskListLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data ?: return@registerForActivityResult
                val title = data.getStringExtra("LIST_TITLE") ?: return@registerForActivityResult
                val tasks = data.getStringArrayListExtra("TASKS") ?: arrayListOf()

                val existing = taskLists.find { it.title == title }
                if (existing != null) {
                    existing.tasks.clear()
                    existing.tasks.addAll(tasks)
                } else {
                    taskLists.add(TaskList(title, tasks.toMutableList()))
                }

                saveTaskLists()
                adapter.notifyDataSetChanged()
                updateEmptyView()
            }
        }

        // Configura RecyclerView e Adapter
        adapter = TaskListAdapter(
            taskLists,
            onItemClick = { selectedList ->
                val intent = Intent(this, TaskListActivity::class.java)
                intent.putExtra("LIST_TITLE", selectedList.title)
                intent.putStringArrayListExtra("TASKS", ArrayList(selectedList.tasks))
                taskListLauncher.launch(intent)
            },
            onDeleteClick = { selectedList ->
                AlertDialog.Builder(this)
                    .setTitle("Excluir lista")
                    .setMessage("Deseja excluir '${selectedList.title}'?")
                    .setPositiveButton("Excluir") { _, _ ->
                        val index = taskLists.indexOf(selectedList)
                        if (index >= 0) {
                            taskLists.removeAt(index)
                            saveTaskLists()
                            adapter.notifyItemRemoved(index)
                            updateEmptyView()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Adiciona linhas entre itens
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
                    val listTitle = editText.text.toString().trim()
                    if (listTitle.isNotEmpty()) {
                        val intent = Intent(this, TaskListActivity::class.java)
                        intent.putExtra("LIST_TITLE", listTitle)
                        taskListLauncher.launch(intent)
                    } else {
                        editText.error = "Informe um nome para a lista"
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun updateEmptyView() {
        emptyView.visibility = if (taskLists.isEmpty()) View.VISIBLE else View.GONE
    }

    // Persistência
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
