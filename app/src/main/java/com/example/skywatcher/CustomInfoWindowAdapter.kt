package com.example.skywatcher

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(private val context : Context) : GoogleMap.InfoWindowAdapter {
    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }
    //This code is a custom code for our googlemaps windows for the markers
    override fun getInfoContents(marker: Marker?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null)
        val title: TextView = view.findViewById(R.id.textcardName)
        val desc: TextView = view.findViewById(R.id.textcardDetails)

        val data = marker?.tag as? CustomInfoWindowData
        title.text = data?.title
        desc.text = data?.desc
        return view
    }

}