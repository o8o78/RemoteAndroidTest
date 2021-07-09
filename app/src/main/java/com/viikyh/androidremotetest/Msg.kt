package com.viikyh.androidremotetest

import android.graphics.Bitmap

class Msg(val contentText: String, val contentImage: Bitmap?, val type: Int) {
    constructor(contentText: String): this(contentText, null, TYPE_TEXT)
    constructor(contentImage: Bitmap): this("", contentImage, TYPE_IMAGE)
    companion object{
        const val TYPE_TEXT = 0
        const val TYPE_IMAGE = 1
    }
}