package com.example.examcracker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.examcracker.databinding.RowPdfAdminItemlayoutBinding

 class AdapterPdfAdmin : RecyclerView.Adapter<AdapterPdfAdmin.PdfAdminViewHolder>,Filterable{

    private lateinit var context: Context
    var pdfArrayList: ArrayList<ModelPdf>

    private val filterList: ArrayList<ModelPdf>

    private lateinit var binding: RowPdfAdminItemlayoutBinding

    var filter : FilterPdfAdmin? = null

    constructor(pdfArrayList: ArrayList<ModelPdf>) : super() {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList = pdfArrayList
    }

    inner class PdfAdminViewHolder(private var binding: RowPdfAdminItemlayoutBinding): RecyclerView.ViewHolder(binding.root) {


       val pdfView = binding.pdfView
       val titleTv = binding.tvBookTitle
       val progressBar = binding.progressBar
       val description = binding.tvBookDesp
       val date = binding.tvDate
       val size = binding.tvSize
       val category = binding.tvCategory
    }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfAdminViewHolder {
         val binding = RowPdfAdminItemlayoutBinding.inflate(LayoutInflater.from(context),parent,false)
         return PdfAdminViewHolder(binding)
     }

     override fun getItemCount(): Int {
         return pdfArrayList.size
     }

     override fun onBindViewHolder(holder: PdfAdminViewHolder, position: Int) {

         val model = pdfArrayList[position]
         val pdfId = model.id
         val categoryId = model.categoryId
         val title = model.title
         val description = model.description
         val pdfUrl = model.url
         val timestamp = model.timestamp
         val formattedDate = MyApplication.formatTimeStamp(timestamp)

         holder.titleTv.text = title
         holder.description.text=description
         holder.date.text=formattedDate

         MyApplication.loadCategory(categoryId,holder.category)

         MyApplication.loadPdfFromUrlSinglePage(pdfUrl, title, holder.pdfView, holder.progressBar, null)

         MyApplication.loadPdfSize(pdfUrl,title, holder.size)
     }

     override fun getFilter(): Filter {
         if (filter == null)
         {
             filter = FilterPdfAdmin(filterList,this)
         }
         return filter as FilterPdfAdmin
     }
 }