package com.abhilashmishra.filelist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.abhilashmishra.filelist.adapter.ViewerAdapter
import com.abhilashmishra.filelist.core.Viewer
import com.abhilashmishra.filelist.model.Sendable

class MainActivity : AppCompatActivity() {
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == Viewer.VIEWER_REQUEST_CODE){
                Log.d("FIUJAFAFAJFAAFAFA", "List: ${data?.getParcelableArrayListExtra<Sendable>(Viewer.KEY_VIEWER_SELECTED_LIST)}")
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Viewer(this).startList()
    }
}