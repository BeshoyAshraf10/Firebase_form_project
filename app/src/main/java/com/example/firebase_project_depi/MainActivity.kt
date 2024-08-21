package com.example.firebase_project_depi

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.firebase_project_depi.databinding.ActivityMainBinding
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    //Late initialization
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

        auth = Firebase.auth
        if(auth.currentUser != null && auth.currentUser?.isEmailVerified == true){
            val i = Intent(this, HomeActivity::class.java)
            i.putExtra("id", auth.currentUser!!.uid)
            startActivity(i)
            finish()
        }
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val scale = resources.displayMetrics.density
            val desiredPx = (16* scale+0.5f).toInt()
            v.setPadding(
                systemBars.left + desiredPx,
                systemBars.top + desiredPx,
                systemBars.right + desiredPx,
                systemBars.bottom + desiredPx
            )
            insets
        }
        binding.notUserTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        binding.loginBtn.setOnClickListener {
            hideKeyboard(it)
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()
            if (email.isBlank() || password.isBlank())
                Toast.makeText(this, "Missing Field/s!", Toast.LENGTH_SHORT).show()
            else {
                binding.progress.isVisible = true
                login(email, password)
                    }

        }

        binding.forgotPassTv.setOnClickListener {
            sendPasswordResetEmail(binding.emailEt.text.toString())
        }


    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                   if(auth.currentUser!!.isEmailVerified){
                       val user = FirebaseAuth.getInstance().currentUser
                       //intent
                       val i = Intent(this,HomeActivity::class.java)
                       i.putExtra("id",auth.currentUser!!.uid)
                       startActivity(i)
                       finish()
                   }else{
                       Toast.makeText(this,"Verify your email first!", Toast.LENGTH_SHORT).show()
                       binding.progress.isVisible = false
                       Snackbar.make(binding.root,"Resend verification email!",BaseTransientBottomBar.LENGTH_INDEFINITE)
                           .setAction("Proceed"){
                               checkEmailVerification()
                           }
                           .setBackgroundTint(getColor(R.color.secColor))
                           .setTextColor(getColor(R.color.main))
                           .setActionTextColor(Color.parseColor("#2196F3"))
                           .show()
//                       FirebaseAuth.getInstance().signOut()

                   }
                } else {
                    val message = task.exception?.message
                    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
                    binding.progress.isVisible= false
                }
            }
    }

    private fun sendPasswordResetEmail(email: String) {
        if (email.isBlank())
            binding.emailEt.error = "Required!"
        else {
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    Toast.makeText(this,"Check your email",Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun checkEmailVerification() {
        auth.currentUser!!.sendEmailVerification()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this,"Check your email!",Toast.LENGTH_LONG).show()
                binding.progress.isVisible = false
            }
        }
}
}

