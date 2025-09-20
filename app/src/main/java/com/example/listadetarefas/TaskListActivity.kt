package com.example.listadetarefas

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TaskListActivity : AppCompatActivity() {

    private lateinit var editTextTask: EditText
    private lateinit var buttonAdd: Button
    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: Button

    private val tasks = mutableListOf<String>()
    private lateinit var adapter: TaskItemAdapter
    private var listTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        // Recebe título e tarefas existentes
        listTitle = intent.getStringExtra("LIST_TITLE") ?: "Nova Lista"
        val existingTasks = intent.getStringArrayListExtra("TASKS") ?: arrayListOf()
        tasks.addAll(existingTasks)

        title = listTitle

        editTextTask = findViewById(R.id.editTextTask)
        buttonAdd = findViewById(R.id.buttonAdd)
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks)
        buttonSave = findViewById(R.id.buttonSave)
        buttonCancel = findViewById(R.id.buttonCancel)

        // Configura Adapter e RecyclerView
        adapter = TaskItemAdapter(tasks) { position ->
            // Nada adicional aqui — Adapter já cuida da remoção
        }

        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewTasks.adapter = adapter

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerViewTasks.addItemDecoration(divider)

        // Adicionar tarefa
        buttonAdd.setOnClickListener {
            val task = editTextTask.text.toString().trim()
            if (task.isNotEmpty()) {
                tasks.add(task)
                adapter.notifyItemInserted(tasks.size - 1)
                recyclerViewTasks.scrollToPosition(tasks.size - 1)
                editTextTask.text.clear()
            } else {
                Toast.makeText(this, "Digite um item!", Toast.LENGTH_SHORT).show()
            }
        }

        // Salvar lista
        buttonSave.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("LIST_TITLE", listTitle)
                putStringArrayListExtra("TASKS", ArrayList(tasks))
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // Cancelar
        buttonCancel.setOnClickListener { finish() }
    }
}
