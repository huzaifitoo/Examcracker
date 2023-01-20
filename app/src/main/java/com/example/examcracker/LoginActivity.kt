package com.example.examcracker

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.examcracker.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.noAccountClick.setOnClickListener {

            startActivity(Intent(this,RegisterActivity::class.java))

        }

        binding.loginBtn.setOnClickListener {


            validateData()
        }
    }

    private val email = ""
    private val password = ""

    private fun validateData() {

        binding.emailEt.text.toString().trim()
        binding.passwordEt.text.toString().trim()

      //  !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (email.isEmpty()){
            Toast.makeText(this, "invalid email...", Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()){
            Toast.makeText(this, "please enter password", Toast.LENGTH_SHORT).show()
        }
        else {
            loginUser()
        }
    }

    private fun loginUser() {

        progressDialog.setMessage("logging in ...")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {

                checkUser()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "error logging in ", Toast.LENGTH_SHORT).show()
            } }

    private fun checkUser() {
        progressDialog.setMessage("checking user...")

        val firebaseUser = firebaseAuth.currentUser

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    progressDialog.dismiss()
                    val userType = snapshot.child("userType").value

                    if (userType == "user"){
                        startActivity(Intent(this@LoginActivity,DashboardUserActivity::class.java))
                        finish()
                    }
                    else if(userType == "admin"){
                        startActivity(Intent(this@LoginActivity,DashboardAdminActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })




}
}