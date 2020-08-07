package com.abhilashmishra.filelist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abhilashmishra.filelist.R
import com.abhilashmishra.filelist.model.File
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager

class FileListAdapter(private val listener : Listener, private val files : ArrayList<File>, val glide : RequestManager) : RecyclerView.Adapter<FileListAdapter.ViewHolder>(){

    interface Listener{
        fun onFileClicked(file : File, isSelected : Boolean)
    }

    companion object{
        val selectedFiles = ArrayList<File>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.file_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val title = files[position].title
        holder.title.text = title

        if (selectedFiles.contains(files[position])) {
            holder.thumbnail.setImageResource(R.drawable.ic_tick)
        } else {
            setDefaultImage(holder, position)
        }

        holder.itemView.setOnClickListener {
            if (selectedFiles.contains(files[position])) {
                setDefaultImage(holder, position)
                selectedFiles.remove(files[position])
                listener.onFileClicked(files[position], false)
            } else {
                holder.thumbnail.setImageResource(R.drawable.ic_tick)
                selectedFiles.add(files[position])
                listener.onFileClicked(files[position], true)
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        glide.clear(holder.thumbnail)
        holder.title.text = ""
        super.onViewRecycled(holder)
    }

    private fun setDefaultImage(holder: ViewHolder, position: Int) {
        val path = files[position].path
        val type = files[position].type
        val iconDrawable = files[position].appIcon
        when (type) {
            File.Type.Photo -> {
                glide.load(path)
                    .into(holder.thumbnail)
            }

            File.Type.App -> {
                if (iconDrawable != null)
                    holder.thumbnail.setImageDrawable(iconDrawable)
            }

            File.Type.Audio -> {
                holder.thumbnail.setImageResource(R.drawable.ic_audio)
            }

            File.Type.Video -> {
                holder.thumbnail.setImageResource(R.drawable.ic_video)
            }

            File.Type.Pdf -> {
                holder.thumbnail.setImageResource(R.drawable.ic_pdf)
            }

            File.Type.Other -> {
                holder.thumbnail.setImageResource(R.drawable.ic_file)
            }
        }
    }

    fun clear(){
        notifyDataSetChanged()
    }

    fun addItems(){
        notifyDataSetChanged()
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val thumbnail: ImageView = itemView.findViewById(R.id.list_item_thumb)
        val title: TextView = itemView.findViewById(R.id.list_item_title)
    }
}