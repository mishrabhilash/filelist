package com.abhilashmishra.filelist.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhilashmishra.filelist.R
import com.bumptech.glide.Glide
import com.abhilashmishra.filelist.adapter.FileListAdapter
import com.abhilashmishra.filelist.model.File


class ListFragment(itemView : View, private val listener : Listener, private val context : Context) : RecyclerView.ViewHolder(itemView), FileListAdapter.Listener {

    private var recyclerView = itemView.findViewById<RecyclerView>(R.id.file_list_recycler_view)

    private var adapter : FileListAdapter? = null
    private val files = ArrayList<File>()

    interface Listener{
        fun onFileClicked(file : File, isSelected: Boolean)
    }

    init{
        initRecyclerView()
    }

    fun setItems(files : ArrayList<File>){
        this.files.clear()
        this.files.addAll(files)
    }

    fun insertItems(files : ArrayList<File>){
        this.files.addAll(files)
        adapter?.addItems()
    }

    fun clearItems(){
        files.clear()
        adapter?.clear()
    }

    private fun initRecyclerView(){
        adapter = FileListAdapter(this@ListFragment, files, Glide.with(context))
        recyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView?.adapter = adapter
    }

    override fun onFileClicked(file: File, isSelected : Boolean) {
        listener?.onFileClicked(file, isSelected)
    }
}