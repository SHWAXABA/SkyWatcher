package com.example.skywatcher

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BirdObservationCamera.newInstance] factory method to
 * create an instance of this fragment.
 */
class BirdObservationCamera : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private lateinit var photoUri: Uri
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragbut = inflater.inflate(R.layout.fragment_bird_observation_camera, container, false)
        val camFab = fragbut.findViewById<FloatingActionButton>(R.id.camereFloatButt)
        //Camera button
        camFab.setOnClickListener(){
            takePicture()
        }
         return fragbut
    }
    //Thise lines of code are what allows us to add take our picture
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {

            // Start AddItem activity and pass the image bitmap and URI
            val intent = Intent(requireContext(), AddItem::class.java)
            intent.putExtra("imageUri", photoUri)

            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Failed to take picture", Toast.LENGTH_SHORT).show()
        }
    }

    private fun takePicture() {
        // Create a file for the image and get its URI
        val photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(requireContext(), "com.example.skywatcher.fileprovider", photoFile)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)  // Direct the camera to save the image to photoUri
        }
        takePictureLauncher.launch(intent)
    }

    // Create a file to store the image
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }
}