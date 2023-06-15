package com.example.ecohero.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecohero.data.response.ArticlesItem
import com.example.ecohero.databinding.ItemArtikelBinding
import com.example.ecohero.utils.DateFormatter

class NewsAdapter(private val onArticleClicked: (ArticlesItem) -> Unit) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    private val newsItems: MutableList<ArticlesItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemArtikelBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsItems[position]
        holder.bind(newsItem)
    }

    override fun getItemCount(): Int {
        return newsItems.size
    }

    fun setData(newData: Collection<ArticlesItem>?) {
        newsItems.clear()
        if (newData != null) {
            newsItems.addAll(newData)
        }
        notifyDataSetChanged()
    }


    inner class NewsViewHolder(private val binding: ItemArtikelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(newsItem: ArticlesItem) {
            binding.tvTitle.text = newsItem.title
            binding.tvDate.text = newsItem.publishedAt?.let { DateFormatter.formatDate(it) }
            Glide.with(binding.root).load(newsItem.urlToImage).into(binding.imgArtikel)

            binding.root.setOnClickListener {
                onArticleClicked(newsItem)
            }
        }
    }
}
