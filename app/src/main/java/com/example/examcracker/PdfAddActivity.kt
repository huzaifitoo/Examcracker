package com.example.examcracker

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.examcracker.databinding.ActivityPdfAddBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PdfAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfAddBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    private var pdfUri: Uri? = null

    private val TAG = "PDF_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPdfAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loadPdfCategories()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("please wait")
        progressDialog.setCanceledOnTouchOutside(false)


        binding.categoryTV.setOnClickListener {
            categoryPickDialog()
        }
        binding.attachImg.setOnClickListener {
            pdfPickIntent()
        }
        binding.uploadBtn.setOnClickListener {
            validateData()
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private var title = ""
    private var description = ""
    private var category = ""
    private fun validateData() {
        Log.d(TAG, "validating data")

        title = binding.titleET.text.toString().trim()
        description = binding.descriptionET.text.toString().trim()
        category = binding.categoryTV.text.toString().trim()


        if (title.isEmpty()) {
            Toast.makeText(this, "please enter title", Toast.LENGTH_SHORT).show()
        } else if (description.isEmpty()) {
            Toast.makeText(this, "please enter description", Toast.LENGTH_SHORT).show()
        } else if (category.isEmpty()) {
            Toast.makeText(this, "please select category", Toast.LENGTH_SHORT).show()

        } else {
            pdfToStorage()
        }
    }

    private fun pdfToStorage() {

        Log.d(TAG, "uploading pdf to storage")

        progressDialog.setMessage("uploading pdf...")
        progressDialog.show()

        val timestamp = System.currentTimeMillis()

        val filePathAndName = "Books/$timestamp"

        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!)

            .addOnSuccessListener { taskSnapshot ->

                Log.d(TAG, "upload pdf to storage now url getting")

                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedPdfUrl = "${uriTask.result}"

                uploadPdfInfoToDb(uploadedPdfUrl, timestamp)


            }

            .addOnFailureListener { e ->

                Log.d(TAG, "failed to load pdf due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "unable to upload due to: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun uploadPdfInfoToDb(uploadedPdfUrl: String, timestamp: Long) {

        Log.d(TAG, "uploading pdf info to db")
        progressDialog.setMessage("uploading to db ...")
        progressDialog.show()

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap["categoryId"] = "$selectedCategoryId"
        hashMap["url"] = "$uploadedPdfUrl"
        hashMap["timestamp"] = timestamp
        hashMap["viewsCount"] = 0
        hashMap["downloadsCount"] = 0

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child("$timestamp")
            .setValue(hashMap)

            .addOnSuccessListener {

                Log.d(TAG, "uploaded to db successful")
                progressDialog.dismiss()
                Toast.makeText(this, "uploaded to db successfully", Toast.LENGTH_SHORT).show()
                pdfUri = null
            }

            .addOnFailureListener { e ->

                Log.d(TAG, "failed to upload to db pdf due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "unable to upload due to: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }

    }

    private fun loadPdfCategories() {
        Log.d(TAG, "loadPdfCategories : Loading Pdf Categories")

        categoryArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                categoryArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelCategory::class.java)
                    categoryArrayList.add(model!!)
                    Log.d(TAG, "onDataChange : ${model.category}")

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""
    private fun categoryPickDialog() {
        Log.d(TAG, "CategoryPickDialog :  showing pdf categories dialog")

        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        for (i in categoryArrayList.indices) {
            categoriesArray[i] = categoryArrayList[i].category
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("pick category")
            .setItems(categoriesArray) { dialog, which ->

                selectedCategoryTitle = categoryArrayList[which].category
                selectedCategoryId = categoryArrayList[which].id

                binding.categoryTV.text = selectedCategoryTitle


                Log.d(TAG, "CategoryPickDialog :  selected category id: $selectedCategoryId")
                Log.d(TAG, "CategoryPickDialog :  showing category title: $selectedCategoryTitle")
            }
            .show()
    }

    private fun pdfPickIntent() {
        Log.d(TAG, "pdf picking :  start picking pdf")

        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)

    }

    private val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d(TAG, "pdf picked")
                pdfUri = result.data!!.data
            } else {
                Log.d(TAG, "pdf pick cancelled")
                Toast.makeText(this, "pick pdf cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )
}