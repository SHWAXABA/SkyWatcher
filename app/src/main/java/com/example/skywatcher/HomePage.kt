package com.example.skywatcher

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.skywatcher.databinding.FragmentHomePageBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Response

private const val LOCATION_PERMISSION_REQUEST_CODE = 1

class HomePage : Fragment() {

    private lateinit var binding: FragmentHomePageBinding
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap // Store a reference to the GoogleMap
    private var currentLatLng: LatLng? = null // Store current location
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private val callback = OnMapReadyCallback { map ->
        // AssignS the GoogleMap instance
        googleMap = map
        //These gives our users more controls for their google maps
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isZoomGesturesEnabled = true

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Call method to handle location
        fetchLocationAndBirds()
        observationLocations()
    }


    //This code fetches ourrent location and the location of nearby bird hotspots
    private fun fetchLocationAndBirds() {

        //Phone permissions condition
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Requests location permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Fetch last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
                currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                val marker = googleMap.addMarker(
                    MarkerOptions().position(currentLatLng!!).title("Current Location").snippet("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )

                marker?.tag = CustomInfoWindowData(
                    "Current Location",""
                )
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng!!, 12f))
                // Fetch birds data using the current location
                fetchBirdsData(currentLatLng!!.latitude, currentLatLng!!.longitude)
            } else {
                // Default to Durban coordinates if location is not available
                val durban = LatLng(-29.8587, 31.0218)
                googleMap.addMarker(MarkerOptions().position(durban).title("Durban"))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(durban, 12f))
                // Fetch birds data for Durban
                fetchBirdsData(durban.latitude, durban.longitude)
            }
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(0.0, 0.0), 1.0f))

        googleMap.setOnMapLongClickListener { latLng ->
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title("Marker at (${latLng.latitude}, ${latLng.longitude})")
            googleMap.addMarker(markerOptions)
        }
    }

    private fun fetchBirdsData(lat: Double, lng: Double) {
        val retrofitServices = RetrofitInstance.getRetrofitInstance().create(BirdInterface::class.java)

        val responseLiveData: LiveData<Response<Birds>> = liveData {
            val response = retrofitServices.getBirds("5h58q7silkq7", lat, lng)
            emit(response)
        }
        //This gets the bird data and assigns them to the nearby markers(Have no idea why theres a lot of yellow lines)
        responseLiveData.observe(requireActivity(), { response ->
            if (response.isSuccessful) {
                val birdList = response.body()?.listIterator()
                //If statement to check if birdlist is null or not
                if (birdList != null) {
                    while (birdList.hasNext()) {
                        val birdItem = birdList.next()
                        // Create a LatLng object for each bird's location
                        val birdLocation = LatLng(birdItem.lat, birdItem.lng)
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(birdLocation)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                .title(birdItem.comName) // Display bird name as title
                        )
                        marker?.tag = CustomInfoWindowData(
                            "Name : ${birdItem.comName}",
                            "Location : ${birdItem.locName}\nHow Many : ${birdItem.howMany}\nPrivate Residence : ${birdItem.locationPrivate}"
                        )
                        googleMap.setInfoWindowAdapter(CustomInfoWindowAdapter(requireContext()))

                        // Set a click listener for the marker
                        googleMap.setOnMarkerClickListener { clickedMarker ->
                            if (clickedMarker == marker) {
                                // Draw polyline to this bird marker
                                drawPolyline(birdLocation)
                                true // Return true to indicate that we have consumed the event
                            } else {
                                false // Return false to allow the default behavior
                            }
                        }
                    }
                } else {
                    Log.e("API Error", "No bird sightings found.")
                }
            } else {
                Log.e("API Error", "Error: ${response.code()} - ${response.message()}")
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Function to draw the polyline
    private fun drawPolyline(destination: LatLng) {
        currentLatLng?.let { currentLocation ->
            val polylineOptions = PolylineOptions()
                .add(currentLocation)
                .add(destination)
                .width(5f) // Polyline width
                .color(android.graphics.Color.BLUE) // Polyline color

            // Add the polyline to the map
            googleMap.addPolyline(polylineOptions)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndBirds() // Retry fetching location and birds if permission is granted
            } else {
                // Handle permission denial, e.g., show a message to the user
            }
        }
    }

    // Observe Firebase in real-time
    private fun observationLocations() {
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        if (uid != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users/Observations/$uid")

            // Use addValueEventListener to update in real-time
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    googleMap.clear() // Clear existing markers to avoid duplicates
                    for (observationSnapshot in snapshot.children) {
                        val observation = observationSnapshot.getValue(BirdObservationData::class.java)
                        observation?.let {
                            val locationParts = it.location.split(",")
                            if (locationParts.size == 2) {
                                val lat = locationParts[0].toDoubleOrNull()
                                val lng = locationParts[1].toDoubleOrNull()
                                if (lat != null && lng != null) {
                                    val observationLatLng = LatLng(lat, lng)
                                    val marker = googleMap.addMarker(
                                        MarkerOptions()
                                            .position(observationLatLng)
                                            .title(it.birdName)
                                            .snippet("Details: ${it.addDetails}\nAmount: ${it.amount}")
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                    )
                                    marker?.tag = CustomInfoWindowData(
                                        "Name : ${it.birdName}",
                                        "Location : ${it.location}\nHow Many : ${it.amount}\nDetails : ${it.addDetails}"
                                    )
                                    googleMap.setInfoWindowAdapter(CustomInfoWindowAdapter(requireContext()))
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error retrieving observations: ${error.message}")
                }
            })
        } else {
            Log.e("Firebase", "User not authenticated.")
        }
    }

}
