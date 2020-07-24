package com.abhilashmishra.filelist.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.abhilashmishra.filelist.fragment.ListFragment
import com.abhilashmishra.filelist.model.File

class ViewPagerAdapter(activity : AppCompatActivity
                       , private val dataset : HashMap<String, ArrayList<File>>
                       , private val keyPositions : ArrayList<String>) : FragmentStateAdapter(activity){

    private val listener : ListFragment.Listener

    init {
        if(activity is ListFragment.Listener){
            listener = activity
        }else{
            throw IllegalStateException("Must Implement Listener")
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = ListFragment.newInstance(listener)
        keyPositions[position]?.let{ key ->
            dataset[key]?.let{ list ->
                fragment.addItems(list)
            }
        }
        return fragment
    }
}