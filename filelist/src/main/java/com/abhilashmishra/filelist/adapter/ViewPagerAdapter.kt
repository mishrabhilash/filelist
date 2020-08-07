package com.abhilashmishra.filelist.adapter

import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.abhilashmishra.filelist.R
import com.abhilashmishra.filelist.fragment.ListFragment
import com.abhilashmishra.filelist.model.File

class ViewPagerAdapter(private val activity : AppCompatActivity
                       , private val dataset : HashMap<String, ArrayList<File>>
                       , private val keyPositions : ArrayList<String>) : RecyclerView.Adapter<ListFragment>(){

    private val listener : ListFragment.Listener
//    private val positionFragmentMap = HashMap<Int, ListFragment>()

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

    override fun onBindViewHolder(holder: ListFragment, position: Int) {
        Log.d("JHAFVAHFBAJKFBAJKF", "Binding: $position")
        keyPositions[position]?.let{ key ->
            Log.d("JHAFVAHFBAJKFBAJKF", "Key: $key")
            dataset[key]?.let{ list ->
                Log.d("JHAFVAHFBAJKFBAJKF", "List: ${list.size}")
                holder.setItems(list)
            }
        }
//        positionFragmentMap[position] = holder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListFragment {
        return ListFragment(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_list, parent, false),
            listener,
            activity.applicationContext
        )
    }

    override fun onViewRecycled(holder: ListFragment) {
        holder.clearItems()
        super.onViewRecycled(holder)
    }

    fun itemInserted(position : Int){
//        val keys : StringBuilder = java.lang.StringBuilder()
//        dataset.forEach{
//            keys.append(" " + it.key)
//        }

        notifyItemInserted(position)
    }

    fun itemListInserted(position : Int, list : ArrayList<File>){
//        positionFragmentMap[position]?.let{
//            it.insertItems(list)
//        }
        notifyItemChanged(position)
    }
}