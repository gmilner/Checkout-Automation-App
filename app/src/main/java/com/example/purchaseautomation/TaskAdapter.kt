package com.example.purchaseautomation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_task.view.*

class TaskAdapter(private val tasks: MutableList<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val curTask = tasks[position]
        holder.itemView.apply {
            tvTask.text = curTask.taskName
            tvStore.text = "smets.lu"
            tvProduct.text = curTask.variant
            tvStatus.text = curTask.status
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun addTask(task: Task) {
        tasks.add(task)
        notifyItemInserted(tasks.size - 1)
    }

    fun clearTasks() {
        tasks.removeAll { true }
        notifyDataSetChanged()
    }

    fun updateStatus(status : String, itemPosition : Int) {
        tasks[itemPosition].status = status
        notifyItemChanged(itemPosition)
    }


}