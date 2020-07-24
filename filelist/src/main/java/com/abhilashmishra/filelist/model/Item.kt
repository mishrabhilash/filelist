package com.abhilashmishra.filelist.model

data class Item(var path : String, val type : Type){
    enum class Type{
        Photo
        , Video
        , PDF
    }
}