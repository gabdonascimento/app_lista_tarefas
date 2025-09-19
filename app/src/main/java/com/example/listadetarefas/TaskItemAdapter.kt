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

    // ViewHolder representa cada item da lista
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
        holder.textTask.text = tasks[position]

        // Remove listener antes de atualizar para evitar comportamento indesejado
        holder.checkBox.setOnCheckedChangeListener(null)

        // Define o estado do checkbox baseado na lista de estados
        holder.checkBox.isChecked = checkedStates.getOrElse(position) { false }

        // Risca o texto se o checkbox estiver marcado
        holder.textTask.paintFlags = if (holder.checkBox.isChecked)
            holder.textTask.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
        else
            holder.textTask.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()

        // Atualiza o estado do checkbox quando o usuário clica
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            checkedStates[position] = isChecked
            holder.textTask.paintFlags = if (isChecked)
                holder.textTask.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            else
                holder.textTask.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // Botão de deletar tarefa
        holder.buttonDelete.setOnClickListener {
            tasks.removeAt(position)             // Remove item da lista
            checkedStates.removeAt(position)     // Remove estado do checkbox correspondente
            notifyItemRemoved(position)          // Animação de remoção
            notifyItemRangeChanged(position, tasks.size)  // Atualiza RecyclerView
            onDeleteClick(position)              // Callback externo, se necessário
        }
    }

    override fun getItemCount(): Int = tasks.size
}
