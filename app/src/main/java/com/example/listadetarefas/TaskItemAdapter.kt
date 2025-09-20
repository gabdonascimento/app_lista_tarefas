package com.example.listadetarefas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskItemAdapter(
    private val tasks: MutableList<String>,
    private val onDeleteClick: (position: Int) -> Unit
) : RecyclerView.Adapter<TaskItemAdapter.TaskItemViewHolder>() {

    // Lista para manter o estado dos checkboxes
    private val checkedStates = MutableList(tasks.size) { false }

    class TaskItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTask: TextView = itemView.findViewById(R.id.textTask)
        val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
        // Atualiza o estado do checkbox se necessário
        if (checkedStates.size < tasks.size) checkedStates.add(false)
        if (checkedStates.size > tasks.size) checkedStates.removeAt(checkedStates.size - 1)

        holder.textTask.text = tasks[position]

        // Remove listener para evitar loops
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = checkedStates[position]

        // Risca o texto se estiver marcado
        holder.textTask.paintFlags = if (holder.checkBox.isChecked)
            holder.textTask.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
        else
            holder.textTask.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()

        // Atualiza estado do checkbox
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            checkedStates[position] = isChecked
            holder.textTask.paintFlags = if (isChecked)
                holder.textTask.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            else
                holder.textTask.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // Botão de deletar item
        holder.buttonDelete.setOnClickListener {
            if (position in tasks.indices) {
                tasks.removeAt(position)
                checkedStates.removeAt(position)
                notifyItemRemoved(position)
                onDeleteClick(position) // callback externo, só para avisar
            }
        }
    }

    override fun getItemCount(): Int = tasks.size
}
