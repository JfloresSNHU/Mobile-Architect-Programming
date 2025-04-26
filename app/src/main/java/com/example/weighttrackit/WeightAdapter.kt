package com.example.weighttrackit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class WeightEntry(val date: String, val weight: String)

class WeightAdapter(
    private val items: MutableList<WeightEntry>,
    private val onDeleteClick: (WeightEntry) -> Unit
) : RecyclerView.Adapter<WeightAdapter.WeightViewHolder>() {

    inner class WeightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvWeight: TextView = itemView.findViewById(R.id.tvWeight)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weight, parent, false)
        return WeightViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        val item = items[position]
        holder.tvDate.text = item.date
        holder.tvWeight.text = item.weight
        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun removeItem(item: WeightEntry) {
        val position = items.indexOf(item)
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}
