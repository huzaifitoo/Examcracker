package com.example.examcracker

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.examcracker.databinding.RowCategoryItemBinding
import com.google.firebase.database.FirebaseDatabase

class AdapterCategory : RecyclerView.Adapter<AdapterCategory.HolderCategory>,Filterable{

    private val context: Context
    var categoryArrayList: ArrayList<ModelCategory>

    private var filterList : ArrayList<ModelCategory>
    private var filter: FilterCategory? = null


    private lateinit var binding: RowCategoryItemBinding

    constructor(context: Context,categoryArrayList: ArrayList<ModelCategory>){
        this.context = context
        this.categoryArrayList = categoryArrayList
        this.filterList = categoryArrayList
    }

    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView){

        var categoryTv : TextView = binding.categoryText
        var deleteBtn : ImageView = binding.deleteBtn

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        binding= RowCategoryItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderCategory(binding.root)
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {

        val model = categoryArrayList[position]
        val id = model.id
        val category = model.category
        val uid = model.uid
        val timestamp= model.timeStamp

        holder.categoryTv.text = category

        holder.deleteBtn.setOnClickListener {
            val builder= AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are you sure want to delete this category")
                .setPositiveButton("confirm"){a, d ->
                    Toast.makeText(context, "deleting...", Toast.LENGTH_SHORT).show()
                    deleteCategory(model,holder)
                }
                .setNegativeButton("cancel"){a,d->
                    a.dismiss()
                }
                .show()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context,PdfListAdminActivity::class.java)
            intent.putExtra("categoryId", id)
             intent.putExtra("category", category)
            context.startActivity(intent)

        }
    }

    private fun deleteCategory(model: ModelCategory, holder: HolderCategory) {
       val id = model.id

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "deleting...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(context, "unable to delete due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getFilter(): Filter {
       if (filter == null){
           filter = FilterCategory(filterList,this)

       }
        return filter as FilterCategory
    }
}