package com.example.skywatcher

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterPage : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        setContentView(R.layout.activity_register_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val signIn = findViewById<TextView>(R.id.SignIn)
        val signUpbut = findViewById<Button>(R.id.SignUpButton)

        //This will allow our user to register to details to our database with a few conditions of course
        signUpbut.setOnClickListener(){
            val fullName = findViewById<EditText>(R.id.FullName).text.toString()
            val phoneNumber = findViewById<EditText>(R.id.PhoneNumber).text.toString()
            val email = findViewById<EditText>(R.id.EmailSignUp).text.toString()
            val password = findViewById<EditText>(R.id.PasswordSignUp).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.ConfirmPassword).text.toString()
            if(fullName.isNotEmpty()&&phoneNumber.isNotEmpty()&&email.isNotEmpty()&&password.isNotEmpty()&&confirmPassword.isNotEmpty()){
                if(password==confirmPassword){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(){

                        if(it.isSuccessful){
                            var intent2 = Intent(this,SignUpThankYou::class.java)
                            startActivity(intent2)
                        }else{
                            Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }

                }else{
                    Toast.makeText(this,"Passwords Do Not Match",Toast.LENGTH_SHORT).show()
                }
            }else{

                Toast.makeText(this,"Please fill in all fields",Toast.LENGTH_SHORT).show()
            }

        }


        //Opens the sign In activity
        signIn.setOnClickListener(){
            val intent = Intent(this,LoginPage::class.java)
            startActivity(intent)
        }
    }


}