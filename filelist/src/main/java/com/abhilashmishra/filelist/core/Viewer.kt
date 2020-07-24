package com.abhilashmishra.filelist.core

import android.app.Activity
import android.app.ListActivity
import android.content.Intent
import com.abhilashmishra.filelist.ViewerActivity
import com.abhilashmishra.filelist.model.Item

class Viewer(private val activity: Activity) {
    companion object {
        const val VIEWER_REQUEST_CODE = 9874
        const val KEY_VIEWER_SELECTED_LIST = "viewer_list_fkajn"
    }

    fun startList() {
        val listActivity = Intent(activity, ListActivity::class.java)
        activity.startActivityForResult(listActivity, VIEWER_REQUEST_CODE)
    }

    fun startViewer(items: ArrayList<Item>) {
        val viewerActivity = Intent(activity, ViewerActivity::class.java)
        ViewerActivity.list.clear()
        ViewerActivity.list.addAll(items)
        activity.startActivity(viewerActivity)
    }
}