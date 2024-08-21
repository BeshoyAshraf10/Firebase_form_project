package com.example.firebase_project_depi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.firebase_project_depi.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        val uid = intent.getStringExtra("id")!!
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db.collection("internshipSubmitting")
            .document(uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val internData = documentSnapshot.toObject<InternshipModel>()
                if (internData != null) {
                    binding.emailEt.setText(internData.email)
                    binding.nameEt.setText(internData.name)
                    binding.phoneEt.setText(internData.phoneNo)
                    binding.linkedinEt.setText(internData.linkedinLink)
                    binding.githubEt.setText(internData.githubLink)}
                Log.d("MyApp", "Internship Data: $internData")
            }
            .addOnFailureListener { e ->
                Log.e("MyApp", "Error getting document", e)
            }

        binding.submitBtn.setOnClickListener {
            binding.progress.isVisible= true
            val email = binding.emailEt.text.toString()
            val name = binding.nameEt.text.toString()
            val phone = binding.phoneEt.text.toString()
            val linkedin = binding.linkedinEt.text.toString()
            val github = binding.githubEt.text.toString()
            val form = InternshipModel(email, name, phone, linkedin, github)
            db.collection("internshipSubmitting")
                .document(uid)
                .set(form)
                .addOnSuccessListener {
                    Toast.makeText(this, "Sent successfully", Toast.LENGTH_SHORT).show()
                    binding.progress.isVisible= false

                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    binding.progress.isVisible= false

                }
        }
        binding.signOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
// Optionally redirect to login screen
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
