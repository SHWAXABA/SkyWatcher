package com.example.skywatcher

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.skywatcher.databinding.ActivityAddItemBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddItem : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityAddItemBinding
    private lateinit var storageReference: StorageReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var imageUriL: Uri
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap // Store a reference to the GoogleMap
    private var currentLatLng: LatLng? = null // Store current location
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        if (uid == null) {
            Log.e("AddItem", "User not authenticated")
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("AddItem", "Activity view set with layout")

        val imageBitmap = intent.getParcelableExtra<Bitmap>("imageBitmap")
        val imageUri = intent.getParcelableExtra<Uri>("imageUri")
        imageUriL = imageUri ?: run {
            Log.e("AddItem", "No Image provided in intent")
            Toast.makeText(this, "No Image provided", Toast.LENGTH_SHORT).show()
            return
        }

        // Set the image
        binding.birdImage.apply {
            if (imageBitmap != null) {
                Log.d("AddItem", "Setting image bitmap")
                setImageBitmap(imageBitmap)
            } else {
                Log.d("AddItem", "Setting image URI")
                setImageURI(imageUri)
            }
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Users/Observations/")
        Log.d("AddItem", "Database reference initialized for Observations")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
                currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
            } else {
                currentLatLng = LatLng(-29.8587, 31.0218) // Durban coordinates
                Log.d("AddItem", "Location not available, defaulting to Durban coordinates")
            }
        }
        binding.saveButton.setOnClickListener {
            val birdname = binding.editBirdName.text.toString()
            val location = "${currentLatLng?.latitude}, ${currentLatLng?.longitude}"
            val birdAmount = binding.Amount.text.toString()
            val details = binding.additionalDetails.text.toString()
            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            Log.d("AddItem", "User input collected: location=$location, birdAmount=$birdAmount, details=$details, date=$currentDate, time=$currentTime")

            val birdDets = BirdObservationData(birdname,details,birdAmount,currentDate,location,currentTime)
            val uniqueObservationKey = databaseReference.child(uid).push().key

            if (uniqueObservationKey != null) {
                Log.d("AddItem", "Unique observation key generated: $uniqueObservationKey")
                databaseReference.child(uid).child(uniqueObservationKey).setValue(birdDets).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("AddItem", "Observation saved successfully")
                        saveImage(imageUriL, uniqueObservationKey)
                        Toast.makeText(this, "Observation Saved", Toast.LENGTH_SHORT).show()
                        onBackPressed()  // Only call this if saving is successful
                    } else {
                        val errorMessage = it.exception?.message ?: "Unknown error"
                        Log.e("AddItem", "Failed to save observation: $errorMessage")
                        Toast.makeText(this, "Failed to save Observation: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Log.e("AddItem", "Failed to generate unique key for observation")
                Toast.makeText(this, "Failed to generate unique key for observation", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImage(imageUri: Uri, uniqueKey: String) {
        val userUid = firebaseAuth.currentUser?.uid
        if (userUid != null) {
            storageReference = FirebaseStorage.getInstance().getReference("Users/Observations/$userUid/$uniqueKey.jpg")
            Log.d("AddItem", "Storage reference set for image upload: Users/Observations/$userUid/$uniqueKey.jpg")

            storageReference.putFile(imageUri).addOnSuccessListener {
                Log.d("AddItem", "Image saved successfully")
                Toast.makeText(this, "Observation Image Saved", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Log.e("AddItem", "Failed to save Observation Image: ${it.message}")
                Toast.makeText(this, "Failed to save Observation Image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("AddItem", "User not authenticated, cannot save image")
            Toast.makeText(this, "User not authenticated, cannot save image", Toast.LENGTH_SHORT).show()
        }
    }
}
