package com.abhilashmishra.filelist.fragment

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


class ListFragment : Fragment(), FileListAdapter.Listener {

    private var recyclerView : RecyclerView? = null

    private var adapter : FileListAdapter? = null
    private val files = ArrayList<File>()
    private var listener : Listener? = null

    companion object {

        @JvmStatic
        fun newInstance(listener : Listener) =
            ListFragment().apply {
                arguments = Bundle().apply {}
                this.listener = listener
            }
    }

    interface Listener{
        fun onFileClicked(file : File, isSelected: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_list, container, false)
        initViews(rootView)
        initRecyclerView()
        return rootView
    }

    fun addItems(files : ArrayList<File>){
        this.files.addAll(files)
    }

    private fun initViews(rootView : View){
        recyclerView = rootView.findViewById(R.id.file_list_recycler_view)
    }

    private fun initRecyclerView(){
        activity?.applicationContext?.let {
            adapter = FileListAdapter(this@ListFragment, files, Glide.with(it))
            recyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            recyclerView?.adapter = adapter
        }
    }

    override fun onFileClicked(file: File, isSelected : Boolean) {
        listener?.onFileClicked(file, isSelected)
    }
}