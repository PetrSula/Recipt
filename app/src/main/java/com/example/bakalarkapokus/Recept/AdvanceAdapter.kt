//package com.example.bakalarkapokus.Recept
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.bakalarkapokus.AddRecept
//import com.example.bakalarkapokus.R
//import com.example.bakalarkapokus.Tables.SQLdata
//import kotlinx.android.synthetic.main.recept_suroviny.view.*
//
//class AdvanceAdapter(private val sList: ArrayList<SQLdata.Ingredience>) : RecyclerView.Adapter<AdvanceAdapter.ViewHolder>(){
//
//
//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
//        val tvName = view.tvNamers
//        val tvQuantity = view.tvQuantytirs
////        val tvEdit = view.ivEdit
//        val tvDelete = view.ivDelete
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvanceAdapter.ViewHolder {
//        return ViewHolder(
//            LayoutInflater.from(parent.context).inflate(R.layout.items_row,parent,false)
//        )
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//        val item = sList.get(position)
//        holder.tvName.text = item.name
//
//        holder.tvDelete.setOnClickListener {
//            if (context is AddRecept) {
//                context.deleteSurovinu(item)
//            }
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return sList.size
//    }
//}