package com.abhilashmishra.filelist.view_holder

import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.abhilashmishra.filelist.R
import com.google.android.exoplayer2.ui.PlayerView

class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val playerView = itemView.findViewById(R.id.main_video_player_view) as PlayerView
    val playButton = itemView.findViewById(R.id.play_button) as ImageButton

    private val container = itemView.findViewById(R.id.play_button_container) as LinearLayout

    var isExoVisible = false

    init {
        hideVideo()
    }

    fun showVideo() {
        playerView.visibility = View.VISIBLE
        container.visibility = View.GONE
        isExoVisible = true
    }

    fun hideVideo() {
        playerView.visibility = View.GONE
        container.visibility = View.VISIBLE
        isExoVisible = false
    }
}