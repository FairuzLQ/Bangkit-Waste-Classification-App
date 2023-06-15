package com.example.ecohero.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.ecohero.data.response.ArticlesItem
import com.example.ecohero.databinding.ActivityDetailArticleBinding
import com.example.ecohero.utils.DateFormatter
import com.example.ecohero.viewmodels.DetailArticleViewModel

class DetailArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailArticleBinding
    private lateinit var viewModel: DetailArticleViewModel

    companion object {
        const val ARTICLE = "article"

        fun start(context: Context, article: ArticlesItem) {
            val intent = Intent(context, DetailArticleActivity::class.java)
            intent.putExtra(ARTICLE, article)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(DetailArticleViewModel::class.java)

        val article = intent.getParcelableExtra<ArticlesItem>(ARTICLE)
        article?.let { viewModel.setSelectedArticle(it) }

        observeSelectedArticle()

        binding.back.setOnClickListener {
            finish()
        }
    }

    private fun observeSelectedArticle() {
        viewModel.selectedArticle.observe(this) { article ->
            article?.let { setupViews(it) }
        }
    }

    private fun setupViews(article: ArticlesItem) {
        Glide.with(this)
            .load(article.urlToImage)
            .into(binding.articlePicture)

        binding.articleDate.text = article.publishedAt?.let { DateFormatter.formatDate(it) }
        binding.tvTitleDetail.text = article.title
        binding.articleAuthor.text = article.author
        binding.description.text = article.content
    }
}