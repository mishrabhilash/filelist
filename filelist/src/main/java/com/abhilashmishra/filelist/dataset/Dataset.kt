package com.abhilashmishra.filelist.dataset

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import com.abhilashmishra.filelist.dataset.listener.Listener
import com.abhilashmishra.filelist.model.File
import kotlinx.coroutines.*


class Dataset(private val listener : Listener){
    private var currentPosition = 0

    fun createDataset(activity : Activity, paginationLimit : Int){
        AsyncTask.execute {
            paginateOther(activity, paginationLimit)
            loadApk(activity)
        }
    }

    private fun loadOther(activity: Activity, limit : Int, startPosition : Int) : Int {
        val cr = activity.contentResolver
        val uri: Uri = MediaStore.Files.getContentUri("external")
        val projection: Array<String>? = null
        val selection: String? = null
        val selectionArgs: Array<String>? = null
        val sortOrder: String? = MediaStore.MediaColumns.DATE_ADDED + " DESC LIMIT " + limit + " OFFSET " + startPosition
        val datasetMap = HashMap<String, ArrayList<File>>()

        cr.query(uri, projection, selection, selectionArgs, sortOrder).use { cur ->
            if (cur != null) {
                while (cur.moveToNext()) {
                    val path = cur.getString(cur.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                    val title = java.io.File(path).name
                    val mimeType =
                        cur.getString(cur.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE))
                    if (mimeType != null) {
                        val file = getFile(path, title, mimeType, null)
                        if (!datasetMap.containsKey(file.type.name)) {
                            datasetMap[file.type.name] = arrayListOf(file)
                        } else {
                            datasetMap[file.type.name]?.add(file)
                        }
                    }
                }
                activity.runOnUiThread{
                    listener.onListLoaded(datasetMap)
                }

                return cur.count
            }
        }
        return -1
    }

    private fun loadApk(activity : Activity){
        val datasetMap = HashMap<String, ArrayList<File>>()
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val packageManager = activity.packageManager
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)?.let {
            it.forEach { applicationInfo ->
                val icon = applicationInfo.loadIcon(packageManager)
                if (!isSystemPackage(applicationInfo)) {
                    val appName = "${packageManager.getApplicationLabel(applicationInfo)}.apk"
                    val file = getFile(getApkDir(applicationInfo), appName.toString(), "APK", icon)
                    if (!datasetMap.contains(file.type.name)) {
                        datasetMap[file.type.name] = arrayListOf(file)
                    } else {
                        datasetMap[file.type.name]?.add(file)
                    }
                }
            }
            activity.runOnUiThread {
                listener.onListLoaded(datasetMap)
            }

        }
    }

    private fun paginateOther(activity : Activity, paginationLimit : Int){
        do{
            val count = loadOther(activity, paginationLimit, currentPosition)
            currentPosition += count
        }while (count >= paginationLimit || count == -1)
    }

    private fun isSystemPackage(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    private fun getApkDir(applicationInfo: ApplicationInfo): String {
        return applicationInfo.sourceDir
    }

    private fun getFile(path: String, title: String, mimeType: String, icon: Drawable?): File {
        val type = when {
            mimeType.contains("pdf", true) -> {
                File.Type.Pdf
            }

            mimeType.contains("jpg", true)
                    || mimeType.contains("png", true)
                    || mimeType.contains("jpeg", true)
                    || mimeType.contains("webp", true)
                    || mimeType.contains("dng", true) -> {
                File.Type.Photo
            }

            mimeType.contains("mp4", true) -> {
                File.Type.Video
            }

            mimeType.contains("mp3", true)
                    || mimeType.contains("mpeg", true)
                    || mimeType.contains("aac-adts", true)
                    || mimeType.contains("ogg", true) -> {
                File.Type.Audio
            }

            mimeType.contains("APK", true) -> {
                File.Type.App
            }

            else -> {
                File.Type.Other
            }
        }

        return File(type, title, path, icon)
    }
}