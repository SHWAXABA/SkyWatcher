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
import com.example.skywatcher.databinding.FragmentHotspotPageBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Response

private const val LOCATION_PERMISSION_REQUEST_CODE = 1
//Semi Junk Code Please Ignore
class HotspotPage : Fragment() {

    private lateinit var binding: FragmentHotspotPageBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHotspotPageBinding.inflate(inflater, container, false)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Check location permissions and fetch location
        fetchLocationAndBirds()

        return binding.root
    }

    private fun fetchLocationAndBirds() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions
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
                val lat = lastLocation.latitude
                val lng = lastLocation.longitude
                fetchBirdsData(lat, lng)
            } else {
                // Default to Durban coordinates if location is not available
                fetchBirdsData(-29.8587, 31.0218)
            }
        }
    }

    private fun fetchBirdsData(lat: Double, lng: Double) {
        val retrofitServices = RetrofitInstance.getRetrofitInstance().create(BirdInterface::class.java)

        val responseLiveData: LiveData<Response<Birds>> = liveData {
            val response = retrofitServices.getBirds("5h58q7silkq7", lat, lng)
            emit(response)
        }

        responseLiveData.observe(requireActivity(), { response ->
            if (response.isSuccessful) {
                val birdList = response.body()?.listIterator()

                if (birdList != null) {
                    binding.dataBirdTextviewTest.text = ""  // Clear previous text
                    while (birdList.hasNext()) {
                        val birdItem = birdList.next()
                        val birdInfo = """
                            Bird Name: ${birdItem.comName}
                            Scientific Name: ${birdItem.sciName}
                            Location: ${birdItem.locName}
                            Date: ${birdItem.obsDt}
                            How Many: ${birdItem.howMany}
                            ------------------------------------
                        """.trimIndent()

                        binding.dataBirdTextviewTest.append(birdInfo + "\n")
                    }
                } else {
                    binding.dataBirdTextviewTest.text = "No bird sightings found."
                }
            } else {
                Log.e("API Error", "Error: ${response.code()} - ${response.message()}")
                binding.dataBirdTextviewTest.text = "Failed to retrieve data. Error: ${response.code()}"
            }
        })
    }
}
