package com.example.flinfo.fragmentClasses

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flinfo.MainActivity
import com.example.flinfo.NewsModel
import com.example.flinfo.R
import com.example.flinfo.ReadFlinfoNewsActivity
import com.example.flinfo.ReadNewsActivity
import com.example.flinfo.adapters.CustomAdapter
import com.example.flinfo.architecture.NewsViewModel
import com.example.flinfo.utils.Constants
import com.example.flinfo.utils.Constants.NEWS_CONTENT
import com.example.flinfo.utils.Constants.NEWS_DESCRIPTION
import com.example.flinfo.utils.Constants.NEWS_IMAGE_URL
import com.example.flinfo.utils.Constants.NEWS_PUBLICATION_TIME
import com.example.flinfo.utils.Constants.NEWS_SOURCE
import com.example.flinfo.utils.Constants.NEWS_TITLE
import com.example.flinfo.utils.Constants.NEWS_URL
import com.example.flinfo.utils.Constants.NEWS_UUID

class ScienceFragment : Fragment() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var adapter: CustomAdapter
    private lateinit var newsData: MutableList<NewsModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_science, container, false)
        newsData = mutableListOf()

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = CustomAdapter(newsData)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)

        // Fetch HSK3 news
        viewModel.getHskNews("mandarin", "hsk3")?.observe(viewLifecycleOwner, { newsList ->
            newsData.clear()
            newsData.addAll(newsList)
            adapter.notifyDataSetChanged()
        })

        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(context, ReadFlinfoNewsActivity::class.java).apply {
                    putExtra(NEWS_UUID, newsData[position].uuid)
                    putExtra(NEWS_URL, newsData[position].url)
                    putExtra(NEWS_TITLE, newsData[position].headLine)
                    putExtra(NEWS_IMAGE_URL, newsData[position].image)
                    putExtra(NEWS_DESCRIPTION, newsData[position].description)
                    putExtra(NEWS_SOURCE, newsData[position].source)
                    putExtra(NEWS_PUBLICATION_TIME, newsData[position].time)
                    putExtra(NEWS_CONTENT, newsData[position].content)
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
}
