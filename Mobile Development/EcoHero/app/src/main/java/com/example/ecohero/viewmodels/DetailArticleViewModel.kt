package com.example.ecohero.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecohero.data.response.ArticlesItem

class DetailArticleViewModel : ViewModel() {

    private val _selectedArticle = MutableLiveData<ArticlesItem>()
    val selectedArticle: LiveData<ArticlesItem>
        get() = _selectedArticle

    fun setSelectedArticle(article: ArticlesItem) {
        _selectedArticle.value = article
    }
}

