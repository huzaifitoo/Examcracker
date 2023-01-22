package com.example.examcracker

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.examcracker.databinding.ActivityCategoryAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CategoryAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryAddBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private var category = ""
    private fun validateData() {

        category = binding.categoryText.text.toString().trim()

        if (category.isEmpty()){
            Toast.makeText(this, "please enter category", Toast.LENGTH_SHORT).show()
        }
        else{
            addCategorytoFirebase()
        }
    }

    private fun addCategorytoFirebase() {

        progressDialog.show()

        val timeStamp = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = timeStamp.toString()
        hashMap["category"] = category
        hashMap["timeStamp"] = timeStamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child("$timeStamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "failed to load due to ${e.message}", Toast.LENGTH_SHORT).show()
                
            }
    }
}