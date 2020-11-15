package com.kotlinlessons.photogallery.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlinlessons.photogallery.R
import com.kotlinlessons.photogallery.logic.FlickrFetcher
import com.kotlinlessons.photogallery.models.GalleryItem

class PhotoGalleryFragment: Fragment() {

    companion object {
        private const val TAG = "PhotoGalleryFragment"
        private const val COLUMN_COLUMN = 3

        fun newInstance(): PhotoGalleryFragment = PhotoGalleryFragment()
    }

    private lateinit var photoRecyclerView: RecyclerView
    private var items = ArrayList<GalleryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        FetchItemsTask().execute()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager = GridLayoutManager(activity, COLUMN_COLUMN)
        setupAdapter()
        return view
    }

    private fun setupAdapter() {
        if (isAdded) {
            photoRecyclerView.adapter = PhotoAdapter(items)
        }
    }

    private inner class FetchItemsTask: AsyncTask<Void, Void, List<GalleryItem>>() {
        override fun doInBackground(vararg params: Void?): List<GalleryItem> = FlickrFetcher().fetchItems()

        override fun onPostExecute(result: List<GalleryItem>?) {
            items = ArrayList(result)
            setupAdapter()
        }
    }

    private inner class PhotoHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val titleTextView = itemView as TextView

        fun bindGalleryItem(item: GalleryItem) {
            titleTextView.text = item.toString()
        }
    }

    private inner class PhotoAdapter(val galleryItems: MutableList<GalleryItem>): RecyclerView.Adapter<PhotoHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val textView = TextView(activity)
            return PhotoHolder(textView)
        }

        override fun getItemCount(): Int = galleryItems.size

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem = galleryItems[position]
            holder.bindGalleryItem(galleryItem)
        }

    }

}