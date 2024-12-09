package com.example.newsreader.presentation.ui.newslist.adapter

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsreader.R
import com.example.newsreader.domain.model.NewsModel

class NewsItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
    private val tvDescription: TextView = view.findViewById(R.id.tvDescription)
    private val tvSource: TextView = view.findViewById(R.id.tvSource)

    fun bind(news: NewsModel) {
        tvTitle.text = news.title
        tvDescription.text = news.description
        tvSource.text = news.sourceName
    }
}