package com.example.bakalarkapokus.Tables

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bakalarkapokus.AdvanceActivity
import com.example.bakalarkapokus.DruhaAktivita
import com.example.bakalarkapokus.MainActivity
import com.example.bakalarkapokus.R
import kotlinx.android.synthetic.main.items_row.view.*

class ItemAdapter(val context: Context, val items: ArrayList<SQLdata.Spiz>):
    RecyclerView.Adapter<ItemAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.items_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemAdapter.ViewHolder, position: Int) {
        val item = items.get(position)
        holder.tvName.text = item.name


        holder.tvDelete.setOnClickListener {
            if (context is DruhaAktivita) {
                context.deleteRecord(item)
            }
        }
        DrawableCompat.setTint(DrawableCompat.wrap(holder.tvDelete.drawable),ContextCompat.getColor(context,R.color.primary))
        holder.tvEdit.setOnClickListener {
            if (context is DruhaAktivita) {
                context.updateRecord(item)
            }
        }
        DrawableCompat.setTint(DrawableCompat.wrap(holder.tvEdit.drawable),ContextCompat.getColor(context,R.color.primary))
    }


    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llMain = view.llMain
        val tvName = view.tvName
        val tvEdit = view.ivEdit
        val tvDelete = view.ivDelete
    }
}