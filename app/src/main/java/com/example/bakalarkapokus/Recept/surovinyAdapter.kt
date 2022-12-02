package com.example.bakalarkapokus.Recept

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bakalarkapokus.*
import com.example.bakalarkapokus.Tables.SQLdata
import kotlinx.android.synthetic.main.items_row.view.*
import kotlinx.android.synthetic.main.recept_suroviny.view.*

class surovinyAdapter (val context: Context, private val sList: ArrayList<SQLdata.Suroviny>): RecyclerView.Adapter<surovinyAdapter.ViewHolder>(){


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvName = view.tvName
        val tvQuantyti = view.tvQuantyti
        val tvEdit = view.ivEdit
        val tvDelete = view.ivDelete
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): surovinyAdapter.ViewHolder {
        return surovinyAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.items_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = sList[position]
        holder.tvName.text = item.name
        holder.tvQuantyti.text = item.quantity
        if (context is AdvanceActivity){
            holder.tvEdit.visibility = View.GONE
        }
        holder.tvEdit.setOnClickListener{
            if ( context is AddRecept ){
                context.editSurovinaRV(item)
            }
        }
        holder.tvDelete.setOnClickListener {
            if (context is AddRecept ) {
                context.deleteSurovinu(item)
            }else if (context is AdvanceActivity){
                context.deleteSurovinu(item)
            }
        }

    }

    override fun getItemCount(): Int {
        return sList.size
    }
}