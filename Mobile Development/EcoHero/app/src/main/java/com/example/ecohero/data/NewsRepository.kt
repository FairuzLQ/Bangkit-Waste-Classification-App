package com.example.ecohero.data

import com.example.ecohero.data.response.ArticlesItem
import com.example.ecohero.data.response.NewsResponse
import com.example.ecohero.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsRepository {
    private val apiService = ApiConfig.getApiService()



    fun getInitialNews(callback: (List<ArticlesItem>?) -> Unit) {
        val call = apiService.searchNews("waste", "3b9bfb227fa04eb9a27374c6f3150a32")
        call.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    val newsItems = newsResponse?.articles
                    callback(newsItems as List<ArticlesItem>?)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}

