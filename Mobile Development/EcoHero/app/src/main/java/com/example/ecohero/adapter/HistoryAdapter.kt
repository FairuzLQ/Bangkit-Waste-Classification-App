package com.example.ecohero.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecohero.data.History
import com.example.ecohero.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private val historyList: List<History>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    class ViewHolder (var binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = historyList[position]
        holder.binding.textTitleHistory.text = history.result
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(history.date)
        holder.binding.textDateHistory.text = formattedDate
        Glide.with(holder.itemView.context).load(history.photo).into(holder.binding.imgHistory)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }
}
