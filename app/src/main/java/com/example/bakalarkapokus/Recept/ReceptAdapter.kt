package com.example.bakalarkapokus.Recept

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.Ingredience
import com.example.bakalarkapokus.Tables.SQLdata
import kotlinx.android.synthetic.main.items_row.view.*
import kotlinx.android.synthetic.main.recept_suroviny.view.*

class ReceptAdapter(private val sList: ArrayList<SQLdata.RvSurovinyRecept>) : RecyclerView.Adapter<ReceptAdapter.ViewHolder>(){



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvName = view.tvSurovina
        val tvQuantity = view.tvQuantyti
        val tvEdit = view.ivEdit
        val tvDelete = view.ivDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recept_suroviny,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = sList[position]
        holder.tvName.text = item.name
        holder.tvQuantity.text = item.quantity
        holder.tvEdit
        holder.tvDelete
    }

    override fun getItemCount(): Int {
        return sList.size
    }
}