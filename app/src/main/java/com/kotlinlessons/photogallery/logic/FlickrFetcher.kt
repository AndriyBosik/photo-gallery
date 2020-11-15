package com.kotlinlessons.photogallery.logic

import android.net.Uri
import android.util.Log
import com.kotlinlessons.photogallery.models.GalleryItem
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class FlickrFetcher {

    companion object {
        const val TAG = "FlickrFetcher"
        const val API_KEY = "f5c168a1fedf175ecb3aaf707987d2d1"
    }

    fun getUrlBytes(urlSpec: String): ByteArray {
        val url = URL(urlSpec)
        val connection = url.openConnection() as HttpURLConnection
        try {
            val outStream = ByteArrayOutputStream()
            val inStream = connection.inputStream
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException(connection.responseMessage + ": with " + urlSpec)
            }
            var bytesRead = 0
            val buffer = ByteArray(1024)
            bytesRead = inStream.read(buffer)
            while (bytesRead > 0) {
                outStream.write(buffer, 0, bytesRead)
                bytesRead = inStream.read(buffer)
            }
            outStream.close()
            return outStream.toByteArray()
        } finally {
            connection.disconnect()
        }
    }

    @Throws(IOException::class)
    fun getUrlString(urlSpec: String): String = String(getUrlBytes(urlSpec))

    fun fetchItems(): List<GalleryItem> {
        val items = mutableListOf<GalleryItem>()
        try {
            val url = Uri.parse("https://api.flickr.com/services/rest/")
                .buildUpon()
                .appendQueryParameter("method", "flickr.photos.getRecent")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("extras", "url_s")
                .build()
                .toString()
            val jsonString = getUrlString(url)
            Log.i(TAG, "Received JSON: $jsonString")
            val jsonBody = JSONObject(jsonString)
            parseItems(items, jsonBody)
        } catch (ioe: IOException) {
            Log.e(TAG, "Failed to fetch items", ioe)
        } catch (je: JSONException) {
            Log.e(TAG, "Failed to parse JSON", je)
        }
        return items
    }

    @Throws(IOException::class, JSONException::class)
    private fun parseItems(items: MutableList<GalleryItem>, jsonBody: JSONObject) {
        val photosJSONObject = jsonBody.getJSONObject("photos")
        val photoJSONArray = photosJSONObject.getJSONArray("photo")
        for (i in 0 until photoJSONArray.length()) {
            val photoJSONObject = photoJSONArray.getJSONObject(i)
            val item = GalleryItem()
            item.id = photoJSONObject.getString("id")
            item.caption = photoJSONObject.getString("title")
            if (!photoJSONObject.has("url_s")) {
                continue
            }
            item.url = photoJSONObject.getString("url_s")
            items.add(item)
        }
    }

}