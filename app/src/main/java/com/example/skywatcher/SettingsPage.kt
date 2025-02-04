package com.example.skywatcher

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SettingsPage : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("SettingsPage", "onCreateView called")
        val view = inflater.inflate(R.layout.fragment_settings_page, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        Log.d("SettingsPage", "FirebaseAuth instance initialized")

        val convert = view.findViewById<Switch>(R.id.Notify)
        val logOut: Button = view.findViewById(R.id.Logout)
        val policy: TextView = view.findViewById(R.id.policyText)
        val aboutUs: TextView = view.findViewById(R.id.aboutusText)
        val profEdit = view.findViewById<Button>(R.id.textEditprofile)
        val uid = firebaseAuth.currentUser?.uid
        profEdit.setOnClickListener(){
            val intent = Intent(requireContext(),EditProfile::class.java)
            startActivity(intent)
        }
        aboutUs.setOnClickListener() {
            Log.d("SettingsPage", "About Us clicked")
            try {
                val intent = Intent(requireContext(), about_us::class.java)
                startActivity(intent)
                Log.d("SettingsPage", "Navigated to About Us page")
            } catch (e: Exception) {
                Log.e("SettingsPage", "Error navigating to About Us: ${e.message}")
            }
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")




        if(uid != null){
            val userDetailsRef = databaseReference.child("Users").child(uid).child("Details")
            userDetailsRef.get().addOnSuccessListener {
                if (it.exists()) {
                    var username = it.child("Username").value.toString()

                    view.findViewById<TextView>(R.id.textUsername).setText(username)

                } else {
                    Toast.makeText(requireContext(), "User Does Not Exist", Toast.LENGTH_SHORT).show()
                }
            }
        }


        logOut.setOnClickListener() {
            Log.d("SettingsPage", "Logout clicked")
            try {
                firebaseAuth.signOut()
                Log.d("SettingsPage", "User logged out successfully")

                val intent = Intent(requireContext(), LoginPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
                Log.d("SettingsPage", "Navigated to LoginPage after logout")
            } catch (e: Exception) {
                Log.e("SettingsPage", "Error during logout: ${e.message}")
            }
        }

        convert.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("SettingsPage", "Switch ON - Converting to miles")
            } else {
                Log.d("SettingsPage", "Switch OFF - Converting to kilometers")
            }
        }

        return view
    }

    private fun convertToMiles(kilometers: Double) {
        Log.d("SettingsPage", "Converting kilometers to miles: $kilometers")
        val miles = kilometers * 0.621371
    }

    private fun convertToKilometers(miles: Double) {
        Log.d("SettingsPage", "Converting miles to kilometers: $miles")
        val kilometers = miles / 0.621371
    }
}
