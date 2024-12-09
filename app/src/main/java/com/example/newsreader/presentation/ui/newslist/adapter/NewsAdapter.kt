package com.example.newsreader.presentation.ui.newslist.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newsreader.R
import com.example.newsreader.domain.model.NewsModel

class NewsAdapter (
    private var onItemClick: (NewsModel) -> Unit
) : RecyclerView.Adapter<NewsItemViewHolder>() {

    private val list: MutableList<NewsModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)

        return NewsItemViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: NewsItemViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    fun setData(items: List<NewsModel>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }
}