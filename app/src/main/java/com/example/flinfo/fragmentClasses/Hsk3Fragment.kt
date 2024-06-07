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
import com.example.flinfo.NewsModel
import com.example.flinfo.R
import com.example.flinfo.ReadFlinfoNewsActivity
import com.example.flinfo.adapters.CustomAdapter
import com.example.flinfo.architecture.NewsViewModel
import com.google.gson.Gson

class Hsk3Fragment : Fragment() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var adapter: CustomAdapter
    private lateinit var newsData: MutableList<NewsModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_hsk3, container, false)
        newsData = mutableListOf()

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = CustomAdapter(newsData)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)

        // Fetch HSK1 news for Business
        viewModel.getHskNews("mandarin", "hsk3")?.observe(viewLifecycleOwner, { newsList ->
            newsData.clear()
            newsData.addAll(newsList)
            adapter.notifyDataSetChanged()
        })

        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val combinedList = newsData.toList()
                val intent = Intent(context, ReadFlinfoNewsActivity::class.java).apply {
                    putExtra("news_list", Gson().toJson(combinedList))  // Pass the combined list of NewsModel objects
                    putExtra("current_position", position)  // Pass the current position
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
