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

class LoginPage : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseAuth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val signin = findViewById<Button>(R.id.SignInButton)
        //Sign in button that checks if our user is found on our database and allows them to login if they are
        //Also add a permanent sign in feature so they don't have to keep signing in after each use of the app
        signin.setOnClickListener(){
            val emailInput = findViewById<EditText>(R.id.Email).text.toString().trim()
            val passwordInput = findViewById<EditText>(R.id.Password).text.toString().trim()
            if(emailInput.isNotEmpty()&& passwordInput.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(emailInput,passwordInput).addOnCompleteListener(){
                    if(it.isSuccessful){
                        val intent1 = Intent(this, MainActivity::class.java)
                        startActivity(intent1)
                    }else{
                        Toast.makeText(this, "Username or Password incorrect", Toast.LENGTH_SHORT).show()
                        //Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Please Fill In All Fields", Toast.LENGTH_SHORT).show()
            }

        }
        //Opens the register page
        val register = findViewById<TextView>(R.id.SignUp)
        register.setOnClickListener(){
            val intent = Intent(this,RegisterPage::class.java)
            startActivity(intent)
        }
    }
    override  fun onStart(){
        super.onStart()
        if(firebaseAuth.currentUser!=null){
            val intent1 = Intent(this, MainActivity::class.java)
            startActivity(intent1)
        }
    }
}