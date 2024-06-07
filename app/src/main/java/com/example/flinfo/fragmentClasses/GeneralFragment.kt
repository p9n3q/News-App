package com.example.flinfo.fragmentClasses

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.flinfo.MainActivity
import com.example.flinfo.NewsModel
import com.example.flinfo.R
import com.example.flinfo.ReadFlinfoNewsActivity
import com.example.flinfo.adapters.CustomAdapter
import com.example.flinfo.architecture.NewsViewModel
import com.example.flinfo.utils.Constants
import com.jama.carouselview.CarouselView
import com.jama.carouselview.enums.IndicatorAnimationType
import com.jama.carouselview.enums.OffsetType
import com.squareup.picasso.Picasso
import com.google.gson.Gson

class GeneralFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var carouselView: CarouselView
    private lateinit var adapter: CustomAdapter
    private lateinit var newsDataForTopHeadlines: List<NewsModel>
    private lateinit var newsDataForDown: MutableList<NewsModel>
    private lateinit var viewModel: NewsViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var position = Constants.INITIAL_POSITION

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_general, container, false)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        carouselView = view.findViewById(R.id.home_carousel)

        // Setting recyclerView's adapter
        newsDataForDown = MainActivity.generalNews.slice(Constants.TOP_HEADLINES_COUNT until MainActivity.generalNews.size).toMutableList()
        adapter = CustomAdapter(newsDataForDown)
        recyclerView.adapter = adapter

        // Set up ViewModel
        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)

        // Set up carousel
        setupCarousel()

        // Set up swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            refreshNews()
        }

        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val combinedList = (newsDataForTopHeadlines + newsDataForDown).toMutableList()
                val combinedPosition = position + Constants.TOP_HEADLINES_COUNT
                val intent = Intent(context, ReadFlinfoNewsActivity::class.java).apply {
                    putExtra("news_list", Gson().toJson(combinedList))  // Pass the combined list of NewsModel objects
                    putExtra("current_position", combinedPosition)  // Pass the adjusted current position
                }
                startActivity(intent)
            }
        })

        // Ignore
        adapter.setOnItemLongClickListener(object : CustomAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) = Unit
        })

        return view
    }

    private fun setupCarousel() {
        newsDataForTopHeadlines = MainActivity.generalNews.slice(0 until Constants.TOP_HEADLINES_COUNT)

        carouselView.apply {
            size = newsDataForTopHeadlines.size
            autoPlay = true
            indicatorAnimationType = IndicatorAnimationType.THIN_WORM
            carouselOffset = OffsetType.CENTER
            setCarouselViewListener { view, position ->
                val imageView = view.findViewById<ImageView>(R.id.img)
                Picasso.get()
                    .load(newsDataForTopHeadlines[position].image)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.samplenews)
                    .into(imageView)

                val newsTitle = view.findViewById<TextView>(R.id.headline)
                newsTitle.text = newsDataForTopHeadlines[position].headLine

                view.setOnClickListener {
                    val combinedList = (newsDataForTopHeadlines + newsDataForDown).toMutableList()
                    val intent = Intent(context, ReadFlinfoNewsActivity::class.java).apply {
                        putExtra("news_list", Gson().toJson(combinedList))  // Pass the combined list of NewsModel objects
                        putExtra("current_position", position)  // Pass the current position
                    }
                    startActivity(intent)
                }
            }
            // After you finish setting up, show the CarouselView
            show()
        }
    }

    private fun refreshNews() {
        viewModel.getNews()?.observe(viewLifecycleOwner, { newsList ->
            MainActivity.generalNews.clear()
            MainActivity.generalNews.addAll(newsList)
            newsDataForTopHeadlines = MainActivity.generalNews.slice(0 until Constants.TOP_HEADLINES_COUNT)
            newsDataForDown.clear()
            newsDataForDown.addAll(MainActivity.generalNews.slice(Constants.TOP_HEADLINES_COUNT until MainActivity.generalNews.size))
            adapter.notifyDataSetChanged()
            setupCarousel()
            swipeRefreshLayout.isRefreshing = false
        })
    }
}
