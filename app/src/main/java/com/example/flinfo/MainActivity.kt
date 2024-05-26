package com.example.flinfo

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.flinfo.adapters.FragmentAdapter
import com.example.flinfo.architecture.NewsViewModel
import com.example.flinfo.retrofit.RetrofitHelper
import com.example.flinfo.utils.Constants.GENERAL
import com.example.flinfo.utils.Constants.HOME
import com.example.flinfo.utils.Constants.HSK1
import com.example.flinfo.utils.Constants.HSK2
import com.example.flinfo.utils.Constants.HSK3
import com.example.flinfo.utils.Constants.HSK4
import com.example.flinfo.utils.Constants.HSK5
import com.example.flinfo.utils.Constants.HSK6
import com.example.flinfo.utils.Constants.TOTAL_NEWS_TAB
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
class MainActivity : AppCompatActivity() {

    // Tabs Title
    private val newsCategories = arrayOf(
        HOME, HSK1,
        HSK2, HSK3,
        HSK4, HSK5, HSK6
    )

    private lateinit var viewModel: NewsViewModel
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var fragmentAdapter: FragmentAdapter
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private var totalRequestCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val authorizationToken = RetrofitHelper.getAuthorizationToken()
        if (authorizationToken.isNullOrEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Set Action Bar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        shimmerLayout = findViewById(R.id.shimmer_layout)
        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]

        if (!isNetworkAvailable(applicationContext)) {
            shimmerLayout.visibility = View.GONE
            val showError: TextView = findViewById(R.id.display_error)
            showError.text = getString(R.string.internet_warming)
            showError.visibility = View.VISIBLE
        }

        // Send request call for news data
        requestNews(GENERAL, generalNews)
        requestHskNews(HSK1, hsk1News)
        requestHskNews(HSK2, hsk2News)
        requestHskNews(HSK3, hsk3News)
        requestHskNews(HSK4, hsk4News)
        requestHskNews(HSK5, hsk5News)
        requestHskNews(HSK6, hsk6News)

        fragmentAdapter = FragmentAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = fragmentAdapter
        viewPager.visibility = View.GONE

    }

    private fun requestNews(newsCategory: String, newsData: MutableList<NewsModel>) {
        viewModel.getNews()?.observe(this) {
            newsData.addAll(it)
            totalRequestCount += 1

            // If main fragment loaded then attach the fragment to viewPager
            if (newsCategory == GENERAL) {
                shimmerLayout.stopShimmer()
                shimmerLayout.hideShimmer()
                shimmerLayout.visibility = View.GONE
                setViewPager()
            }

            if (totalRequestCount == TOTAL_NEWS_TAB) {
                viewPager.offscreenPageLimit = 7
            }
        }
    }

    private fun requestHskNews(newsCategory: String, newsData: MutableList<NewsModel>) {
        viewModel.getHskNews(newsCategory)?.observe(this) {
            newsData.addAll(it)
            totalRequestCount += 1

            // If main fragment loaded then attach the fragment to viewPager
            if (newsCategory == GENERAL) {
                shimmerLayout.stopShimmer()
                shimmerLayout.hideShimmer()
                shimmerLayout.visibility = View.GONE
                setViewPager()
            }

            if (totalRequestCount == TOTAL_NEWS_TAB) {
                viewPager.offscreenPageLimit = 7
            }
        }
    }

    private fun setViewPager() {
        if (!apiRequestError) {
            viewPager.visibility = View.VISIBLE
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = newsCategories[position]
            }.attach()
        } else {
            val showError: TextView = findViewById(R.id.display_error)
            showError.text = errorMessage
            showError.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item_mainactivity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        intent = Intent(applicationContext, SavedNewsActivity::class.java)
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }

    // Check internet connection
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // For 29 api or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            // For below 29 api
            if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                return true
            }
        }
        return false
    }

    companion object {
        var generalNews: ArrayList<NewsModel> = ArrayList()
        var hsk1News: MutableList<NewsModel> = mutableListOf()
        var hsk2News: MutableList<NewsModel> = mutableListOf()
        var hsk3News: MutableList<NewsModel> = mutableListOf()
        var hsk4News: MutableList<NewsModel> = mutableListOf()
        var hsk5News: MutableList<NewsModel> = mutableListOf()
        var hsk6News: MutableList<NewsModel> = mutableListOf()
        var apiRequestError = false
        var errorMessage = "error"
    }
}
