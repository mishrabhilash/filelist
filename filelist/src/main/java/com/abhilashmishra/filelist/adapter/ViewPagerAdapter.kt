package com.abhilashmishra.filelist.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.abhilashmishra.filelist.fragment.ListFragment
import com.abhilashmishra.filelist.model.File

class ViewPagerAdapter(activity : AppCompatActivity
                       , private val dataset : HashMap<String, ArrayList<File>>
                       , private val keyPositions : ArrayList<String>) : FragmentStateAdapter(activity){

    private val listener : ListFragment.Listener
    private val positionFragmentMap = HashMap<Int, ListFragment>()

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

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = ListFragment.newInstance(listener)
        keyPositions[position]?.let{ key ->
            dataset[key]?.let{ list ->
                fragment.addItems(list)
            }
        }
        positionFragmentMap[position] = fragment
        return fragment
    }

    fun itemInserted(position : Int){
        notifyItemInserted(position)
    }

    fun itemListInserted(position : Int, list : ArrayList<File>){
        positionFragmentMap[position]?.let{
            it.insertItems(list)
        }
//        notifyItemChanged(position)
    }
}