package com.example.bakalarkapokus.Adaptery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bakalarkapokus.Aktivity.SpizActivity
import com.example.bakalarkapokus.R
import com.example.bakalarkapokus.Tables.SQLdata
import kotlinx.android.synthetic.main.items_row.view.*

class SpizAdapter(val context: Context, val items: ArrayList<SQLdata.Spiz>):
    RecyclerView.Adapter<SpizAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.items_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder.tvName.text = item.name


        holder.tvDelete.setOnClickListener {
            if (context is SpizActivity) {
                context.deleteRecord(item)
            }
        }
        DrawableCompat.setTint(DrawableCompat.wrap(holder.tvDelete.drawable),ContextCompat.getColor(context,R.color.ikon))
        holder.tvEdit.setOnClickListener {
            if (context is SpizActivity) {
                context.updateRecord(item)
            }
        }
        DrawableCompat.setTint(DrawableCompat.wrap(holder.tvEdit.drawable),ContextCompat.getColor(context,R.color.third))
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