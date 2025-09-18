package com.example.listadetarefas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskListAdapter(
    private val lists: List<TaskList>,       // Dados (listas criadas)
    private val onItemClick: (TaskList) -> Unit     // Ação ao clicar no título
) : RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder>() {

    //ViewHolder: representa cada item da Recylcler View
    class TaskListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return TaskListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        val taskList = lists[position]
        holder.textTitle.text = taskList.title

        //Quando clicar em um título, abre a lista correspondente
        holder.itemView.setOnClickListener {
            onItemClick(taskList)
        }
    }

    override fun getItemCount(): Int = lists.size
}