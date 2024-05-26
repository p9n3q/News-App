package com.example.flinfo.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.flinfo.fragmentClasses.*
import com.example.flinfo.utils.Constants.TOTAL_NEWS_TAB

class FragmentAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle){

    override fun getItemCount(): Int = TOTAL_NEWS_TAB

    override fun createFragment(position: Int): Fragment {

        when (position) {
            0 -> {
                return GeneralFragment()
            }
            1 -> {
                return Hsk1Fragment()
            }
            2 -> {
                return Hsk2Fragment()
            }
            3 -> {
                return Hsk3Fragment()
            }
            4 -> {
                return Hsk4Fragment()
            }
            5 -> {
                return Hsk5Fragment()
            }
            6 -> {
                return Hsk6Fragment()
            }

            else -> return GeneralFragment()

        }
    }
}