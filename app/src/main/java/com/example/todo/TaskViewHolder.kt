package com.example.todo

import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val tvTask: TextView = view.findViewById(R.id.tvTask)
    private val cbTask: CheckBox = view.findViewById(R.id.cbTask)

    fun render(task: Task) {

        if (task.isSelected) {
            tvTask.paintFlags = tvTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            tvTask.paintFlags = tvTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        cbTask.isChecked = task.isSelected
        tvTask.text = task.name

        val color = when (TaskCategory.fromString(task.category)) {
            TaskCategory.Business -> R.color.category_business
            TaskCategory.Other -> R.color.category_other
            TaskCategory.Personal -> R.color.category_personal
        }
        cbTask.buttonTintList = ColorStateList.valueOf(
            ContextCompat.getColor(cbTask.context, color)
        )

    }
}