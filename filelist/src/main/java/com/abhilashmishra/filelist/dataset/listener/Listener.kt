package com.abhilashmishra.filelist.dataset.listener

import com.abhilashmishra.filelist.model.File

interface Listener{
    fun onListLoaded(datasetMap : HashMap<String, ArrayList<File>>)
}