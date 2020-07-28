package com.abhilashmishra.filelist.adapter

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abhilashmishra.filelist.R
import com.abhilashmishra.filelist.model.Item
import com.abhilashmishra.filelist.view_holder.PdfViewHolder
import com.abhilashmishra.filelist.view_holder.PhotoViewHolder
import com.abhilashmishra.filelist.view_holder.UnsupportedMimeTypeViewHolder
import com.abhilashmishra.filelist.view_holder.VideoViewHolder
import com.bumptech.glide.RequestManager
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.io.File


class ViewerAdapter(private val context : Context
                    , private val glide : RequestManager
                    , private val items : ArrayList<Item>
                    , private val pageWidth : Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val TYPE_PHOTO = 1
    private val TYPE_VIDEO = 2
    private val TYPE_PDF = 3

    private var currentDataSource = DefaultDataSourceFactory(context, Util.getUserAgent(context, "Viewer"))
    private var currentVideoViewHolder : VideoViewHolder? = null

    override fun getItemViewType(position: Int): Int {
        return when(items[position].type){
            Item.Type.Photo -> {
                TYPE_PHOTO
            }

            Item.Type.Video -> {
                TYPE_VIDEO
            }

            Item.Type.PDF -> {
                TYPE_PDF
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            TYPE_PHOTO -> {
                return PhotoViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.photo_view_holder, parent, false))
            }

            TYPE_VIDEO -> {
                return VideoViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.video_view_holder, parent, false))
            }

            TYPE_PDF -> {
                return PdfViewHolder(context, LayoutInflater.from(parent.context)
                    .inflate(R.layout.pdf_view_holder, parent, false)
                    , pageWidth)
            }

            else -> {
                return UnsupportedMimeTypeViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.unsupported_mime_type_view_holder, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            TYPE_PHOTO -> {
                holder as PhotoViewHolder
                loadPhotoType(holder, position)
            }

            TYPE_VIDEO -> {
                holder as VideoViewHolder
                loadVideoType(holder, position)
            }

            TYPE_PDF -> {
                holder as PdfViewHolder
                loadPdfType(holder, position)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if(holder is VideoViewHolder){
            holder.playerView.player?.stop(true)
            holder.playerView.player?.release()
            holder.playerView.player = null
        }else if(holder is PhotoViewHolder){
            glide.clear(holder.imageView)
        }
    }

    private fun loadPhotoType(holder : PhotoViewHolder, position: Int){
        glide.load(items[position].path)
            .into(holder.imageView)
    }

    private fun loadVideoType(holder : VideoViewHolder, position : Int){
        holder.playButton.setOnClickListener{
            if(holder.isExoVisible) {
                holder.playerView.player = null
                holder.hideVideo()
            }
            else{
                if(currentVideoViewHolder != null){
                    currentVideoViewHolder?.playerView?.player?.stop(true)
                    currentVideoViewHolder?.playerView?.player?.release()
                    currentVideoViewHolder?.playerView?.player = null
                    currentVideoViewHolder?.hideVideo()
                }

                val player = SimpleExoPlayer.Builder(context).build()
                holder.showVideo()
                val uri = Uri.parse("file://${items[position].path}")
                // This is the MediaSource representing the media to be played.
                val videoSource: MediaSource = ProgressiveMediaSource.Factory(currentDataSource)
                    .createMediaSource(uri)
                // Prepare the player with the source.
                player.prepare(videoSource)
                player.playWhenReady = true
                player.seekTo(0)
                holder.playerView.player = player
                currentVideoViewHolder = holder
            }
        }
        holder.hideVideo()
    }

    private fun loadPdfType(holder : PdfViewHolder, position : Int){
        val input = ParcelFileDescriptor.open(File(items[position].path), ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(input)
        holder.setRenderer(renderer)
    }

}