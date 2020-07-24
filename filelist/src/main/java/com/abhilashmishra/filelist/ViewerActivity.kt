package com.abhilashmishra.filelist

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.abhilashmishra.filelist.adapter.ViewerAdapter
import com.abhilashmishra.filelist.model.Item
import com.bumptech.glide.Glide

class ViewerActivity : AppCompatActivity() {

    companion object {
        val list = ArrayList<Item>()
    }

    private var itemsRecyclerView: RecyclerView? = null
    private var itemsAdapter: ViewerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewer)
        hideSystemUI()
        initViews()
        initRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        list.clear()
    }

    private fun initViews() {
        itemsRecyclerView = findViewById(R.id.items_recycler_view)
    }

    private fun initRecyclerView() {
        itemsAdapter = ViewerAdapter(this, Glide.with(this), list, getPageWidth())
        itemsRecyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        PagerSnapHelper().attachToRecyclerView(itemsRecyclerView)
        itemsRecyclerView?.setItemViewCacheSize(0)
        itemsRecyclerView?.adapter = itemsAdapter
    }

    private fun getPageWidth(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}