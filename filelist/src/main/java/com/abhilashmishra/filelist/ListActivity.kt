package com.abhilashmishra.filelist

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.abhilashmishra.filelist.adapter.FileListAdapter
import com.abhilashmishra.filelist.adapter.ViewPagerAdapter
import com.abhilashmishra.filelist.core.Viewer
import com.abhilashmishra.filelist.dataset.Dataset
import com.abhilashmishra.filelist.dataset.listener.Listener
import com.abhilashmishra.filelist.fragment.ListFragment
import com.abhilashmishra.filelist.model.File
import com.abhilashmishra.filelist.model.Sendable
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_list.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ListActivity : AppCompatActivity(), ListFragment.Listener {

    private val FILE_CHOOSER_REQUEST_CODE: Int = 1001
    private var viewPager: ViewPager2? = null
    private var adapter: ViewPagerAdapter? = null
    private var tabLayout: TabLayout? = null
    private var toolbar: Toolbar? = null

    private var order = hashMapOf<String, Int>(
        Pair(File.Type.Photo.name, 0) // Photo -> 1
        , Pair(File.Type.Video.name, 1) // Video -> 2
        , Pair(File.Type.App.name, 2) // App -> 3
        , Pair(File.Type.Audio.name, 3) // Audio -> 4
        , Pair(File.Type.Pdf.name, 4) // Pdf -> 5
        , Pair(File.Type.Other.name, 5)) // Other -> 6

    val fileTypeComparator = Comparator<String> { o1, o2 ->
        when {
            order[o1]!! < order[o2]!! -> {
                -1
            }
            order[o1]!! > order[o2]!! -> {
                1
            }
            else -> {
                0
            }
        }
    }

    private var datasetMap = HashMap<String, ArrayList<File>>()
    private var mimeTypeIndex = ArrayList<String>()
    private val selectedFiles = ArrayList<File>()
    private val fileApkDirMap = HashMap<File, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        initViews()
        setSupportActionBar(toolbar)
        setActionBarTitle()
        setupViewPager()

        createDataset()
        progress.visibility = View.GONE
//        Flowable.fromCallable { createDataset() }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//                setupViewPager()
//                progress.visibility = View.GONE
//            }
    }

    override fun onDestroy() {
        super.onDestroy()
        FileListAdapter.selectedFiles.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
//            R.id.main_menu_done -> {
//                sendResult()
//                true
//            }
            R.id.main_menu_system_file -> {
                launchFileExplorer()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun launchFileExplorer() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("*/*")
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE)
    }

    override fun onFileClicked(file: File, isSelected: Boolean) {
        if (isSelected) {
            selectedFiles.add(file)
        } else {
            selectedFiles.remove(file)
        }
        setActionBarTitle()
        setButtonTextAndVisibility()

    }

    private fun setButtonTextAndVisibility() {
        if (selectedFiles.size >= 1) {
            sendContainer.visibility = View.VISIBLE
            sendButton.text =
                resources.getQuantityString(R.plurals.send_x_files, selectedFiles.size, selectedFiles.size)
        } else{
            sendContainer.visibility = View.GONE
        }
    }

    private fun initViews() {
        viewPager = findViewById(R.id.main_view_pager)
        tabLayout = findViewById(R.id.main_tab_layout)
        toolbar = findViewById(R.id.main_toolbar)
        sendContainer.setOnClickListener {
            sendResult()
        }
    }

    private fun setupViewPager() {
        try {
            adapter = ViewPagerAdapter(this, datasetMap, mimeTypeIndex)
            viewPager?.adapter = adapter
            TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
                tab.text = mimeTypeIndex[position]
            }.attach()
        } catch (e: KotlinNullPointerException) {
            e.printStackTrace()
        }
    }

    private fun setActionBarTitle() {
        toolbar?.title = resources.getQuantityString(
            R.plurals.num_files_selected_text,
            selectedFiles.size,
            selectedFiles.size
        )
    }

    private fun createDataset() {
        val dataset = Dataset(object : Listener{
            override fun onListLoaded(datasetMap : HashMap<String, ArrayList<File>>) {
                for(keyVal in datasetMap){
                    if(this@ListActivity.datasetMap.containsKey(keyVal.key)){
                        this@ListActivity.datasetMap[keyVal.key]?.addAll(keyVal.value)
                        adapter?.itemListInserted(mimeTypeIndex.indexOf(keyVal.key), keyVal.value)
                    }else{
                        this@ListActivity.datasetMap[keyVal.key] = keyVal.value
                        val index = -(Collections.binarySearch(mimeTypeIndex, keyVal.key, fileTypeComparator) + 1)
                        mimeTypeIndex.add(index, keyVal.key)
                        adapter?.itemInserted(index)
                    }
                }
            }
        })
        dataset.createDataset(this, 100)
    }

    private fun sendResult() {
        val returnIntent = Intent()
        returnIntent
            .putParcelableArrayListExtra(Viewer.KEY_VIEWER_SELECTED_LIST, getResultList())
            .putExtra(
                Viewer.KEY_VIEWER_SELECTED_LIST_TYPE,
                Viewer.KEY_VIEWER_SELECTED_LIST_TYPE_FILEPATH
            )
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private fun getResultList(): ArrayList<Sendable> {
        val resultList = ArrayList<Sendable>()
        selectedFiles.forEach { file ->
            when (file.type) {
                File.Type.App -> {
                    fileApkDirMap[file]?.let { apkDir ->
                        resultList.add(Sendable(file.title, apkDir))
                    }
                }

                else -> {
                    file.path?.let { path ->
                        resultList.add(Sendable(file.title, path))
                    }
                }
            }
        }
        return resultList
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val clipData: ClipData? = data?.clipData
                val clipDataList = ArrayList<String>()
                for (index in 0 until (clipData?.itemCount ?: 0)) {
                    clipData?.getItemAt(index)?.uri?.let { clipDataList.add(it.toString()) }
                }
                data?.data?.let {
                    clipDataList.add(it.toString())
                }
                val returnIntent = Intent()
                val sendableList = ArrayList<Sendable>()
                clipDataList.forEach { uri ->
                    contentResolver?.query(Uri.parse(uri), null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        cursor.moveToFirst()
                        val fileName = cursor.getString(nameIndex)
                        sendableList.add(Sendable(fileName, uri))
                    }
                }

                returnIntent.putParcelableArrayListExtra(
                    Viewer.KEY_VIEWER_SELECTED_LIST,
                    sendableList
                ).putExtra(
                    Viewer.KEY_VIEWER_SELECTED_LIST_TYPE,
                    Viewer.KEY_VIEWER_SELECTED_LIST_TYPE_URI
                )
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}