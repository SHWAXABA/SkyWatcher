package com.example.skywatcher

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class GoogleMapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        //JUNK CODE PLEASE IGNORE
        ///////////////////////////
//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//
//
//        val melbourne = LatLng(-37.8136, 144.9631)
//        googleMap.addMarker(MarkerOptions().position(melbourne).title("Marker in Melbourne"))
//
//        val brisbane = LatLng(-27.4698, 153.0251)
//        googleMap.addMarker(MarkerOptions().position(brisbane).title("Marker in Brisbane"))
//
//        val perth = LatLng(-31.9505, 115.8605)
//        googleMap.addMarker(MarkerOptions().position(perth).title("Marker in Perth"))
//
//        val adelaide = LatLng(-34.9285, 138.6007)
//        googleMap.addMarker(MarkerOptions().position(adelaide).title("Marker in Adelaide"))
//
//        val canberra = LatLng(-35.2809, 149.1300)
//        googleMap.addMarker(MarkerOptions().position(canberra).title("Marker in Canberra"))
//
//        val darwin = LatLng(-12.4634, 130.8456)
//        googleMap.addMarker(MarkerOptions().position(darwin).title("Marker in Darwin"))
//
//        val hobart = LatLng(-42.8821, 147.3272)
//        googleMap.addMarker(MarkerOptions().position(hobart).title("Marker in Hobart"))
//
//
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 4.0f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_google_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.Hotspotmap) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}