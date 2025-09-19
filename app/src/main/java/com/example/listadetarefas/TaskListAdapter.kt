package com.example.listadetarefas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskListAdapter(
    private val lists: MutableList<TaskList>,             // listas criadas
    private val onItemClick: (TaskList) -> Unit,          // abrir lista
    private val onDeleteClick: (TaskList) -> Unit         // deletar lista
) : RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder>() {

    //ViewHolder: representa cada item da Recylcler View
    class TaskListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.textListTitle)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task_list, parent, false)
        return TaskListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        val taskList = lists[position]
        holder.textTitle.text = taskList.title

        //Quando clicar em um título, abre a lista correspondente
        holder.itemView.setOnClickListener {
            onItemClick(taskList)
        }

        // Quando clicar no botão da Lixeira -> Remove a lista
        holder.btnDelete.setOnClickListener {
            onDeleteClick(taskList)
        }
    }

    override fun getItemCount(): Int = lists.size
}