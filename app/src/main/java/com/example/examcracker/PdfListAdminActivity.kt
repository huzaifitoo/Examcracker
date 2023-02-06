package com.example.examcracker

import android.content.Intent
import android.graphics.ColorSpace.Model
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.examcracker.databinding.ActivityPdfListAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfListAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfListAdminBinding

    private companion object{
        const val TAG = "PDF_LIST_ADMIN_TAG"
    }

    private lateinit var pdfArrayList : ArrayList<ModelPdf>
    private var categoryId = ""
    private var category = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityPdfListAdminBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent = intent
        categoryId = intent.getStringExtra("categoryId")!!
        categoryId = intent.getStringExtra("category")!!

        binding.subtitleTv.text = category

        loadPdfList()

    }

    private fun loadPdfList() {

        pdfArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Books")
            ref.orderByChild("categoryId").equalTo(categoryId)

                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        pdfArrayList.clear()
                        for (ds in snapshot.children){
                            val model = ds.getValue(ModelPdf::class.java)

                            pdfArrayList.add(model!!)
                            Log.d(TAG,"on data change ${model.title} ${model.categoryId}")
                        }

                    }
                    ad


                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
    }
}