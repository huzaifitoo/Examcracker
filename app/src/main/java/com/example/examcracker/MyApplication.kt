package com.example.examcracker

import android.annotation.SuppressLint
import android.app.Application
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        fun formatTimeStamp(timestamp: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp

            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }

        @SuppressLint("SetTextI18n")
        fun loadPdfSize(pdfUrl: String?, pdfTitle: String, sizeTv: TextView) {
            val TAG = "PDF_SIZE_TAG"

            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl!!)
            ref.metadata
                .addOnSuccessListener { storageMetadata ->
                    Log.d(TAG, "loadPdfSize: got metaData")

                    val bytes = storageMetadata.sizeBytes.toDouble()
                    Log.d(TAG, "loadPdfSize: Size Bytes $bytes")

                    val kb = bytes / 1024
                    val mb = kb / 1024
                    if (mb > 1) {
                        sizeTv.text = "${String.format("$.2f", mb)}MB"
                    } else if (kb >= 1) {
                        sizeTv.text = "${String.format("$.2f", kb)}KB"
                    } else {
                        sizeTv.text = "${String.format("$.2f", bytes)}bytes"
                    }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "failed to load data due to : ${e.message}")

                }
        }

        @SuppressLint("SuspiciousIndentation")
        fun loadPdfFromUrlSinglePage(
            pdfUrl: String,
            pdfTitle: String,
            pdfView: PDFView,
            progressBar: ProgressBar,
            pagesTv: TextView?
        ) {
            val TAG = "PDF_THUMBNAIL_TAG"

            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl!!)
            ref.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener { bytes ->

                    Log.d(TAG, "loadPdfSize: Size Bytes $bytes")

             pdfView.fromBytes(bytes)
                 .pages(0)
                 .spacing(0)
                 .swipeHorizontal(false)
                 .enableSwipe(false)
                 .onError { e->
                     progressBar.visibility= View.INVISIBLE
                     Log.d(TAG,"error due to ${e.message}")
                 }
                 .onPageError { page, t ->
                     progressBar.visibility= View.INVISIBLE
                     Log.d(TAG,"page error due to ${t.message}")
                 }
                 .onLoad { nbPages ->
                     progressBar.visibility= View.INVISIBLE
                        if (pagesTv!=null){
                            pagesTv.text="$nbPages"
                        }
                 }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "failed to load data due to : ${e.message}")

                }

        }


        fun loadCategory(categoryId: String,categoryTv: TextView){

            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val category = "${snapshot.child("category").value}"

                        categoryTv.text=category
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }
    }


}