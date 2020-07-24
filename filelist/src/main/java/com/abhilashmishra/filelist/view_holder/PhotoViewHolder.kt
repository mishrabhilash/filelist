package com.abhilashmishra.filelist.view_holder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.abhilashmishra.filelist.R

class PhotoViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
    val imageView = itemView.findViewById(R.id.main_photo_image_view) as ImageView
}