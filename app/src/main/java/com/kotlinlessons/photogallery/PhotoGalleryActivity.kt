package com.kotlinlessons.photogallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.kotlinlessons.photogallery.fragments.PhotoGalleryFragment

class PhotoGalleryActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment = PhotoGalleryFragment.newInstance()

}