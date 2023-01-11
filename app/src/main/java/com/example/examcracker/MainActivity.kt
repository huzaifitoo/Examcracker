package com.example.examcracker


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.examcracker.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var imageUrl : Uri? = null

    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode== Activity.RESULT_OK){
            imageUrl= it.data!!.data

            binding.uploadpdf.setImageURI(imageUrl)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            uploadpdf.setOnClickListener {

                val intent = Intent("android.intent.action.GET_CONTENT")
                intent.type = "application/pdf"
                launchGalleryActivity.launch(intent)
            }

        }
    }}