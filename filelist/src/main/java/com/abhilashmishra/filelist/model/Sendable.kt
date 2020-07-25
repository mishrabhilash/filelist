package com.abhilashmishra.filelist.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Sendable(val name: String, val path: String) : Parcelable