package com.kotlinlessons.photogallery.models

class GalleryItem {

    var caption: String = ""
    var id: String? = null
    var url: String? = null

    override fun toString(): String = caption

}