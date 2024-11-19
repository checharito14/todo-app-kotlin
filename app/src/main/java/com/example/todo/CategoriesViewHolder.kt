package com.example.todo

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CategoriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {


    private val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)
    private val cvCategory: CardView = view.findViewById(R.id.cvCategory)

    fun render(taskCategory: TaskCategory, onItemSelected: (Int) -> Unit) {

        if (!taskCategory.isSelected) {
            cvCategory.setCardBackgroundColor(
                ContextCompat.getColor(cvCategory.context, R.color.background)
            )
        } else {
            val color = when(taskCategory) {
                TaskCategory.Business -> R.color.category_business
                TaskCategory.Other -> R.color.category_other
                TaskCategory.Personal -> R.color.category_personal
            }
            cvCategory.setCardBackgroundColor(
                ContextCompat.getColor(cvCategory.context, color)
            )
        }

        itemView.setOnClickListener { onItemSelected(layoutPosition) }

        when (taskCategory) {
            TaskCategory.Business -> {
                tvCategoryName.text = itemView.context.getString(R.string.business)
            }

            TaskCategory.Other -> {
                tvCategoryName.text = itemView.context.getString(R.string.others)
            }

            TaskCategory.Personal -> {
                tvCategoryName.text = itemView.context.getString(R.string.personal)
            }
        }

    }
}