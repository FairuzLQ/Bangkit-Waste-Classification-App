package com.example.ecohero.ui


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ecohero.R
import com.example.ecohero.adapter.NewsAdapter
import com.example.ecohero.data.NewsRepository
import com.example.ecohero.data.response.ArticlesItem
import com.example.ecohero.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView
    private val newsRepository = NewsRepository()
    private var articlesList: List<ArticlesItem> = emptyList()

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setupRecyclerView()
        setupSwipeRefreshLayout()
        getInitialNews()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter { article -> onArticleClicked(article) }
        binding.rvArtikel.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
        }
    }

    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout = binding.pullRefresh
        swipeRefreshLayout.setOnRefreshListener {
            getInitialNews()
        }
    }

    private fun getInitialNews() {
        showLoading(true)
        newsRepository.getInitialNews { articles ->
            showLoading(false)
            if (articles != null) {
                if (articles.isNotEmpty()) {
                    articlesList = articles
                    showNews(articles)
                } else {
                    showNoData()
                }
            } else {
                showError()
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showNews(articles: List<ArticlesItem>) {
        binding.tvNoData.visibility = View.GONE
        binding.rvArtikel.visibility = View.VISIBLE
        newsAdapter.setData(articles)
    }

    private fun showNoData() {
        binding.tvNoData.visibility = View.VISIBLE
        binding.rvArtikel.visibility = View.GONE
        binding.tvNoData.text = getString(R.string.no_data_available)
    }

    private fun showError() {
        binding.tvNoData.visibility = View.VISIBLE
        binding.rvArtikel.visibility = View.GONE
        binding.tvNoData.text = getString(R.string.error_occurred)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun onArticleClicked(article: ArticlesItem) {
        val intent = Intent(requireContext(), DetailArticleActivity::class.java)
        intent.putExtra(DetailArticleActivity.Companion.ARTICLE, article)
        startActivity(intent)
    }



    private fun setupSearchView() {
        searchView = binding.etSearch
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchNews(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchNews(newText)
                return true
            }
        })
    }

    private fun searchNews(query: String) {
        val filteredList = articlesList.filter { article ->
            article.title?.contains(query, true) == true
        }
        newsAdapter.setData(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}
