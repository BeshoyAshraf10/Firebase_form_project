package com.example.firebase_project_depi
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.firebase_project_depi.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.alreadyUserTv.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.signUpBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passwordEt.text.toString()
            val conPass = binding.conPassEt.text.toString()
            if (email.isBlank() || pass.isBlank() || conPass.isBlank())
                Toast.makeText(this, "Missing Field/s!", Toast.LENGTH_SHORT).show()
            else if (pass.length < 6)
                Toast.makeText(this, "Short Password!", Toast.LENGTH_SHORT).show()
            else if (pass != conPass)
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            else {
                binding.progress.isVisible = true
                signUpUser(email, pass)
            }
        }

    }


    private fun signUpUser(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    verifyEmail()
                } else {
                    val message = task.exception?.message
                    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
                    binding.progress.isVisible= false
                }
            }
    }

    private fun verifyEmail() {

        auth.currentUser!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

        Toast.makeText(this,"Check your email!",Toast.LENGTH_LONG).show()
        binding.progress.isVisible = false
                }
            }
    }

}