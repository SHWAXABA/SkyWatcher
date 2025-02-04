package com.example.skywatcher

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class EditProfile : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Firebase stuff
        firebaseAuth = FirebaseAuth.getInstance()

        val uid = firebaseAuth.currentUser?.uid


        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        var changPic = findViewById<ImageView>(R.id.camera_icon)
        val backButton = findViewById<ImageView>(R.id.back_button)

        val saveChanges = findViewById<Button>(R.id.savechanges)

        readDetails(uid.toString())

        saveChanges.setOnClickListener(){

            var username = findViewById<EditText>(R.id.usernameBox).text.toString()

            var email = findViewById<EditText>(R.id.emailBox).text.toString()
            var passwordB = findViewById<EditText>(R.id.passwordBox).text.toString()
            var confirmPassword = findViewById<EditText>(R.id.confirmpasswordBox).text.toString()
            if(username.isNotEmpty()&&email.isNotEmpty()&&passwordB.isNotEmpty()&&confirmPassword.isNotEmpty()){
                if(passwordB == confirmPassword){
                    //uploadPhoto(uid.toString(),ImageU)
                    if (uid != null) {

                        updateDetails(uid,username,email,passwordB)
                        Toast.makeText(this,"data saved",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"data not saved",Toast.LENGTH_SHORT).show()
                    }

                }else{
                    Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"All Text Boxes Must Be Filled",Toast.LENGTH_SHORT).show()
            }

        }

        backButton.setOnClickListener(){
            onBackPressed()
        }

        changPic.setOnClickListener(){
            openGallery()
        }
    }
    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        openGalleryLauncher.launch(intent)
    }
    private val openGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            if (imageUri != null) {

                val profImage = findViewById<ImageView>(R.id.profile_image)


                profImage.setImageURI(imageUri)



            }
        } else {
            Toast.makeText(this, "Failed to pick image", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateDetails(uid: String, username: String?,email: String?,passwordB: String?){
        val userDetailsRef = databaseReference.child("Users").child(uid).child("Details")
        userDetailsRef.child("Username").setValue(username).addOnSuccessListener {
            Log.d("EditProfile", "Username updated successfully")
        }
            .addOnFailureListener { e ->
                Log.e("EditProfile", "Failed to update Username: ${e.message}")
            }
        userDetailsRef.child("Email").setValue(email)
        userDetailsRef.child("Password").setValue(passwordB).addOnSuccessListener {
            Toast.makeText(this, "User details updated", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update user details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun readDetails(uid: String){
        val userDetailsRef = databaseReference.child("Users").child(uid).child("Details")
        userDetailsRef.get().addOnSuccessListener {
            if (it.exists()) {
                var username = it.child("Username").value.toString()
                var email = it.child("Email").value.toString()
                var password = it.child("Password").value.toString()
                findViewById<EditText>(R.id.usernameBox).setText(username)
                findViewById<EditText>(R.id.emailBox).setText(email)
                findViewById<EditText>(R.id.passwordBox).setText(password)
                findViewById<EditText>(R.id.confirmpasswordBox).setText(password)
            } else {
                Toast.makeText(this, "User Does Not Exist", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun uploadPhoto(uid: String,imageUri: Uri){

        storageReference = FirebaseStorage.getInstance().getReference("Users" + uid)
        storageReference.putFile(imageUri).addOnSuccessListener(){

        }

    }
}